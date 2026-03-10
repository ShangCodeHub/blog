package com.shang.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.thread.ThreadUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.shang.Test.HtmlConverteMd;
import com.shang.common.RedisConstants;
import com.shang.entity.SysArticle;
import com.shang.entity.SysCategory;
import com.shang.service.ArticleService;
import com.shang.utils.IpUtil;
import com.shang.utils.RedisUtil;
import com.shang.vo.article.ArchiveListVo;
import com.shang.vo.article.ArticleDetailVo;
import com.shang.vo.article.ArticleListVo;
import com.shang.vo.article.CategoryListVo;
import com.shang.mapper.SysArticleMapper;
import com.shang.mapper.SysCategoryMapper;
import com.shang.utils.PageUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ArticleServiceImpl implements ArticleService {

    private final SysArticleMapper sysArticleMapper;

    private final SysCategoryMapper sysCategoryMapper;
    private final RedisUtil redisUtil;




    public void  HtmlConvertMarkdown() {
        ArticleDetailVo articleDetail = sysArticleMapper.getArticleDetail(310L);
        String content = articleDetail.getContent();
        String markdown = HtmlConverteMd.htmlTansToMarkdown(content);
        System.out.println(markdown);
    }

    @Override
    public IPage<ArticleListVo> getArticleList(Integer tagId, Integer categoryId, String keyword) {
        // 获取分页参数
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<Object> page = PageUtil.getPage();
        int pageNum = (int) page.getCurrent();
        int pageSize = (int) page.getSize();
        
        // 构建缓存key，包含查询参数和分页参数以区分不同的查询条件
        String cacheKey = RedisConstants.ARTICLE_LIST_CACHE + 
                "tagId:" + (tagId == null ? "null" : tagId) + 
                ":categoryId:" + (categoryId == null ? "null" : categoryId) + 
                ":keyword:" + (keyword == null || keyword.isEmpty() ? "null" : keyword) +
                ":pageNum:" + pageNum +
                ":pageSize:" + pageSize;
        // 先从缓存中获取
        Object cached = redisUtil.get(cacheKey);
        if (cached != null) {
            // 如果缓存存在，直接返回
            return (IPage<ArticleListVo>) cached;
        }
        // 缓存不存在，查询数据库
        IPage<ArticleListVo> result = sysArticleMapper.getArticleListApi(page, tagId, categoryId, keyword);
        
        // 将查询结果存入缓存，设置10分钟过期时间
        redisUtil.set(cacheKey, result, RedisConstants.TEN_MINUTES_EXPIRE, java.util.concurrent.TimeUnit.SECONDS);
        
        return result;
    }

    @Override
    public ArticleDetailVo getArticleDetail(Long id) {
        ArticleDetailVo detailVo = sysArticleMapper.getArticleDetail(id);
        // 判断是否点赞
        Object userId = StpUtil.getLoginIdDefaultNull();
        if (userId != null) {
           detailVo.setIsLike(sysArticleMapper.getUserIsLike(id, Integer.parseInt(userId.toString())));
        }

        //添加阅读量
        String ip = IpUtil.getIp();
        ThreadUtil.execAsync(() -> {
            Map<Object, Object> map = redisUtil.hGetAll(RedisConstants.ARTICLE_QUANTITY);
            List<String> ipList = (List<String> ) map.get(id.toString());
            if (ipList != null) {
                if (!ipList.contains(ip)) {
                    ipList.add(ip);
                }
            }else {
                ipList = new ArrayList<>();
                ipList.add(ip);
            }
            map.put(id.toString(),ipList);
            redisUtil.hSetAll(RedisConstants.ARTICLE_QUANTITY,map);
        });
        return detailVo;
    }

    @Override
    public List<ArchiveListVo> getArticleArchive() {

        List<ArchiveListVo> list = new ArrayList<>();

        List<Integer> years = sysArticleMapper.getArticleArchive();
        for (Integer year : years) {
            List<ArticleListVo> articleListVos = sysArticleMapper.getArticleByYear(year);
            list.add(new ArchiveListVo(year, articleListVos));
        }
        return list;
    }

    @Override
    public List<CategoryListVo> getArticleCategories() {
        return sysCategoryMapper.getArticleCategories();
    }

    @Override
    public List<ArticleListVo> getCarouselArticle() {
        return getArticlesByCondition(SysArticle::getIsCarousel);
    }

    @Override
    public List<ArticleListVo> getRecommendArticle() {
        return getArticlesByCondition(SysArticle::getIsRecommend);
    }

    @Override
    public Boolean like(Long articleId) {
        // 判断是否点赞
        int userId = StpUtil.getLoginIdAsInt();
        Boolean isLike = sysArticleMapper.getUserIsLike(articleId, userId);
        if (isLike) {
            // 点过则取消点赞
            sysArticleMapper.unLike(articleId, userId);
        }else {
            sysArticleMapper.like(articleId, userId);
        }
        return true;
    }

    @Override
    public List<SysCategory> getCategoryAll() {
        return sysCategoryMapper.selectList(null);
    }

    private List<ArticleListVo> getArticlesByCondition(SFunction<SysArticle, Object> conditionField) {
        LambdaQueryWrapper<SysArticle> wrapper = new LambdaQueryWrapper<SysArticle>()
                .select(SysArticle::getId, SysArticle::getTitle, SysArticle::getCover, SysArticle::getCreateTime)
                .orderByDesc(SysArticle::getCreateTime)
                .eq(conditionField, 1);

        List<SysArticle> sysArticles = sysArticleMapper.selectList(wrapper);

        if (sysArticles == null || sysArticles.isEmpty()) {
            return Collections.emptyList();
        }

        return sysArticles.stream().map(item -> ArticleListVo.builder()
                .id(item.getId())
                .cover(item.getCover())
                .title(item.getTitle())
                .createTime(item.getCreateTime())
                .build()).collect(Collectors.toList());
    }
}
