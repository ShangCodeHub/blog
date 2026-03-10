package com.shang.Test;

import io.github.furstenheim.CopyDown;
import io.github.furstenheim.Options;
import io.github.furstenheim.OptionsBuilder;

public class HtmlConverteMd {
    public static void main(String[] args) {
        String htmlStr ="<h1 id=\"一标题\">一、标题</h1><h2id=\"1测试\">1.测试</h2><p><code>这是一段测试的文本</code></p>";
        System.out.println(htmlTansToMarkdown(htmlStr));

    }
    public static String htmlTansToMarkdown(String htmlStr) {
        OptionsBuilder optionsBuilder = OptionsBuilder.anOptions();
        Options options = optionsBuilder.withBr("-")
                // more options
                .build();
        CopyDown converter = new CopyDown(options);
        String markdownText = converter.convert(htmlStr);
        return markdownText;
    }

}
