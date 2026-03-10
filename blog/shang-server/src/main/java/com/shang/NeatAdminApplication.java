package com.shang;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@EnableFileStorage
@MapperScan({"com.shang.mapper", "com.shang.dify.mapper"})
@SpringBootApplication(
    scanBasePackages = {"com.shang"},
    excludeName = {
        "org.springdoc.core.configuration.SpringDocConfiguration",
        "org.springdoc.webmvc.core.configuration.SpringDocWebMvcConfiguration",
        "org.springdoc.webmvc.ui.configuration.SwaggerConfig",
        "org.springdoc.core.conditions.MultipleOpenApiSupportCondition"
    }
)
public class NeatAdminApplication {
    public static void main(String[] args) {
        SpringApplication.run(NeatAdminApplication.class, args);
        System.out.println("系统启动成功！");
    }
}