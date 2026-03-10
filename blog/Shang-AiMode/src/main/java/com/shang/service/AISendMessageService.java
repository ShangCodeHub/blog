package com.shang.service;
import com.alibaba.dashscope.aigc.generation.Generation;
import com.alibaba.dashscope.aigc.generation.GenerationOutput;
import com.alibaba.dashscope.aigc.generation.GenerationParam;
import com.alibaba.dashscope.aigc.generation.GenerationResult;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesis;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisParam;
import com.alibaba.dashscope.aigc.imagesynthesis.ImageSynthesisResult;
import com.alibaba.dashscope.common.Message;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.InputRequiredException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.shang.dify.exception.BusinessException;
import com.shang.dify.common.vo.ResultCode;
import org.springframework.stereotype.Service;
import java.util.Arrays;
@Service
public class AISendMessageService {

    public  String GetGenerateImageAdress(String title, String source)  {
        String prompt2 = "\"为博客首页设计一张科技代码风格图片，主题围绕标题"+title+"。画面需融合编程元素（如流动的代码字符串、二进制数字、终端命令行界面、代码编辑器界面片段等），整体色调采用科技感配色（深蓝 / 深灰底色搭配荧光绿 / 亮蓝 / 紫色代码高亮），风格偏向未来感与数字化。标题文字需以醒目的科技字体呈现，可嵌入代码流中或悬浮于代码背景之上，周围点缀抽象的科技符号（如电路板纹理、数据流线条、像素颗粒等），营造出浓厚的编程与科技氛围，图片构图适合作为博客首页横幅，视觉冲击力强。\"";

        ImageSynthesisParam param =
                ImageSynthesisParam.builder()
                        .apiKey("sk-b9ad22cf0c084bf2939ee542955e4134")
                        .model("wanx2.1-t2i-turbo")
                        .prompt(prompt2)
                        .n(1)
                        .size("1024*1024")
                        .build();

        ImageSynthesis imageSynthesis = new ImageSynthesis();
        ImageSynthesisResult result = null;
        try {
            System.out.println("---sync call, please wait a moment----");
            result = imageSynthesis.call(param);
        } catch (ApiException | NoApiKeyException e){
            throw BusinessException.of(ResultCode.SERVER_ERROR, "调用AI生成图片异常: %s", e.getMessage());
        }
        String url = result.getOutput().getResults().get(0).get("url");

            return url;
    }

    public String GetGenerateDescribe(String questions)  {
        Generation gen = new Generation();
        Message systemMsg = Message.builder()
                .role(Role.SYSTEM.getValue())
                .content("根据标题解释含义，字数不要超过50字")
                .build();
        Message userMsg = Message.builder()
                .role(Role.USER.getValue())
                .content(questions)
                .build();
        GenerationParam param = GenerationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(System.getenv("sk-b9ad22cf0c084bf2939ee542955e4134"))
                .model("qwen-plus")
                .messages(Arrays.asList(systemMsg, userMsg))
                .resultFormat(GenerationParam.ResultFormat.MESSAGE)
                .build();
        try {
            String content = gen.call(param).getOutput().getChoices().get(0).getMessage().getContent();
            return content;
        } catch (NoApiKeyException e) {
            throw BusinessException.of(ResultCode.SERVER_ERROR, "API密钥异常: %s", e.getMessage());
        } catch (InputRequiredException e) {
            throw BusinessException.of(ResultCode.BAD_REQUEST, "输入参数异常: %s", e.getMessage());
        }
    }

}