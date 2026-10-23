package com.pcconfigurations.backend.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String frontendPath = new File("../frontend").getAbsolutePath() + "/";
        String frontendDirect = new File("frontend").getAbsolutePath() + "/";

        registry.addResourceHandler("/**")
                .addResourceLocations(
                        "file:" + frontendPath.replace("\\", "/"),
                        "file:" + frontendDirect.replace("\\", "/"),
                        "classpath:/static/"
                );
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("forward:/index.html");
    }
}
