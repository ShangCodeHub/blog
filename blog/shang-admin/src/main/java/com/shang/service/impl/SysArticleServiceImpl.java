package com.shang.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shang.common.Constants;
import com.shang.common.ResultCode;
import com.shang.dto.article.ArticleQueryDto;
import com.shang.entity.SysArticle;
import com.shang.entity.SysTag;
import com.shang.exception.ServiceException;
import com.shang.mapper.SysArticleMapper;
import com.shang.mapper.SysTagMapper;
import com.shang.dify.entity.DifyApiKey;
import com.shang.dify.service.DifyApiKeyService;
import com.shang.service.AISendMessageService;
import com.shang.service.DifyAIChatService;
import com.shang.service.SysArticleService;
import com.shang.utils.AiUtil;
import com.shang.utils.PageUtil;
import com.shang.vo.article.ArticleListVo;
import com.shang.vo.article.SysArticleDetailVo;
import org.springframework.http.ResponseEntity;
import com.vladsch.flexmark.html2md.converter.FlexmarkHtmlConverter;
import com.vladsch.flexmark.util.data.MutableDataSet;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SysArticleServiceImpl extends ServiceImpl<SysArticleMapper, SysArticle> implements SysArticleService {

    private final SysTagMapper sysTagMapper;

    private final AiUtil aiUtil;
    @Resource
    AISendMessageService aiSendMessageService;
    
    @Resource
    private DifyAIChatService difyAIChatService;
    
    @Resource
    private DifyApiKeyService difyApiKeyService;

    @Override
    public IPage<ArticleListVo> selectPage(ArticleQueryDto articleQueryDto) {
        return baseMapper.selectPageList(PageUtil.getPage(), articleQueryDto);
    }

    @Override
    public SysArticleDetailVo detail(Integer id) {
        SysArticle sysArticle = baseMapper.selectById(id);

        SysArticleDetailVo sysArticleDetailVo = new SysArticleDetailVo();
        BeanUtils.copyProperties(sysArticle, sysArticleDetailVo);

        //获取标签
        List<Integer> tags = sysTagMapper.getTagIdsByArticleId(id);
        sysArticleDetailVo.setTagIds(tags);
        return sysArticleDetailVo;
    }
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean add(SysArticleDetailVo sysArticle) {
        SysArticle obj = new SysArticle();
        BeanUtils.copyProperties(sysArticle, obj);
        obj.setUserId(StpUtil.getLoginIdAsLong());
        baseMapper.insert(obj);
        //添加标签
        sysTagMapper.addArticleTagRelations(obj.getId(), sysArticle.getTagIds());

        ThreadUtil.execAsync(() -> {
            String res = aiSendMessageService.GetGenerateDescribe(obj.getContent() + "请提供一段简短的介绍描述该文章的内容");
            if (StringUtils.isNotBlank(res)) {
                obj.setAiDescribe(res);
                baseMapper.updateById(obj);
            }
        });
        
        // 异步上传文章到 Dify 知识库
        ThreadUtil.execAsync(() -> {
            uploadArticleToDifyKnowledgeBase(obj);
        });
        
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean update(SysArticleDetailVo sysArticle) {

        SysArticle obj = new SysArticle();
        BeanUtils.copyProperties(sysArticle, obj);

        //没有管理员权限就只能修改自己的文章
        if (!StpUtil.hasRole(Constants.ADMIN)) {
            SysArticle article = baseMapper.selectById(sysArticle.getId());
            if (article.getUserId() != StpUtil.getLoginIdAsLong()) {
                throw new ServiceException("只能修改自己的文章");
            }
        }

        baseMapper.updateById(obj);

        //先删除标签在新增标签
        sysTagMapper.deleteArticleTagsByArticleIds(Collections.singletonList(obj.getId()));
        //添加标签
        sysTagMapper.addArticleTagRelations(obj.getId(), sysArticle.getTagIds());
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean delete(List<Long> ids) {
        //没有管理员权限就只能删除自己的文章
        if (!StpUtil.hasRole(Constants.ADMIN)) {
            List<SysArticle> sysArticles = baseMapper.selectBatchIds(ids);
            for (SysArticle sysArticle : sysArticles) {
                if (sysArticle.getUserId() != StpUtil.getLoginIdAsLong()) {
                    throw new RuntimeException("只能删除自己的文章");
                }
            }
        }

        baseMapper.deleteBatchIds(ids);
        sysTagMapper.deleteArticleTagsByArticleIds(ids);
        return true;
    }


    @Override
    public void reptile(String url) {
        try {
            Document document = Jsoup.connect(url).get();
            Elements title  = document.getElementsByClass("title-article");
            Elements tags  = document.getElementsByClass("tag-link");
            Elements content  = document.getElementsByClass("article_content");
            if (StringUtils.isBlank(content.toString())) {
                throw new ServiceException(ResultCode.CRAWLING_ARTICLE_FAILED.getDesc());
            }

            //爬取的是HTML内容，需要转成MD格式的内容
            String newContent = content.get(0).toString().replaceAll("<code>", "<code class=\"lang-java\">");
            String markdown = FlexmarkHtmlConverter.builder(new MutableDataSet()).build().convert(newContent)
                    .replace("lang-java","java");

            SysArticle entity = SysArticle.builder().userId(StpUtil.getLoginIdAsLong()).contentMd(markdown)
                    .isOriginal(Constants.NO).originalUrl(url)
                    .title(title.get(0).text()).cover("https://api.btstu.cn/sjbz/api.php?lx=dongman&format=images").content(newContent).build();

            baseMapper.insert(entity);
            //为该文章添加标签
            List<Integer> tagIds = new ArrayList<>();
            tags.forEach(item ->{
                String tag = item.text();
                SysTag result = sysTagMapper.selectOne(new LambdaQueryWrapper<SysTag>().eq(SysTag::getName,tag ));
                if (result == null){
                    result = SysTag.builder().name(tag).build();
                    sysTagMapper.insert(result);
                }
                tagIds.add(result.getId());
            });
            sysTagMapper.addArticleTagRelations(entity.getId(),tagIds);

            System.out.println("文章抓取成功，内容为:" + JSON.toJSONString(entity));
            
            // 异步上传文章到 Dify 知识库
            ThreadUtil.execAsync(() -> {
                uploadArticleToDifyKnowledgeBase(entity);
            });
        } catch (IOException e) {
            throw new ServiceException(e.getMessage());
        }
    }
    
    /**
     * 上传文章到 Dify 知识库
     * 
     * @param article 文章对象
     */
    private void uploadArticleToDifyKnowledgeBase(SysArticle article) {
        try {
            // 从数据库查询知识库配置
            // 查询条件：user_id = 1, key_type = "dataset", resource_id = "knowledge_base_001"
            Long defaultUserId = 1L;
            String defaultResourceId = "knowledge_base";
            String defaultKeyType = "dataset";
            DifyApiKey apiKeyEntity = difyApiKeyService.getApiKeyEntity(
                    defaultUserId,
                    defaultResourceId,
                    defaultKeyType
            );
            
            if (apiKeyEntity == null) {
                System.out.println("未找到 Dify 知识库配置，跳过文章上传: articleId=" + article.getId() + 
                        ", userId=" + defaultUserId + ", resourceId=" + defaultResourceId + ", keyType=" + defaultKeyType);
                return;
            }
            
            // 使用查询到的 resource_id 作为 dataset_id（用于 API 请求路径）
            String datasetId = "c8e7cde0-afd9-4635-bc75-8d423f851630";
            
            if (StringUtils.isBlank(datasetId)) {
                System.out.println("知识库配置中的 resource_id 为空，跳过文章上传: articleId=" + article.getId());
                return;
            }
            
            // 获取文章内容（优先使用 contentMd，如果为空则使用 content）
            String articleContent = StringUtils.isNotBlank(article.getContentMd()) 
                    ? article.getContentMd() 
                    : article.getContent();
            
            if (StringUtils.isBlank(articleContent)) {
                System.out.println("文章内容为空，跳过上传: articleId=" + article.getId());
                return;
            }
            
            // 调用 DifyAIChatService 通过文本方式上传文章到知识库
            // resource_id 用于查找 API Key，dataset_id 用于 API 请求路径
            ResponseEntity<String> response = difyAIChatService.uploadArticleToKnowledgeBaseByText(
                    article.getTitle(),
                    articleContent,
                    datasetId,  // 作为 dataset_id 用于 API 请求路径
                    apiKeyEntity.getUserId(),  // 使用查询到的用户ID
                    apiKeyEntity.getResourceId(),  // 作为 resource_id 用于查找 API Key
                    apiKeyEntity.getKeyType()  // 使用查询到的密钥类型
            );
            
            System.out.println("文章已上传到 Dify 知识库: articleId=" + article.getId() + 
                    ", title=" + article.getTitle() + ", datasetId=" + datasetId + 
                    ", status=" + response.getStatusCode());
        } catch (Exception e) {
            // 上传失败不影响主流程，只记录日志
            System.err.println("上传文章到 Dify 知识库失败: articleId=" + article.getId() + ", error=" + e.getMessage());
            e.printStackTrace();
        }
    }
}
