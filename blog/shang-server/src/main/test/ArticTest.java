mport com.shang.service.impl.ArticleServiceImpl;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;

@SpringBootTest(classes = com.shang.NeatAdminApplication.class)
public class ArticTest {

    @Resource
    ArticleServiceImpl articleService;

    @Test
    public void test() {
        articleService.HtmlConvertMarkdown();
    }



}
