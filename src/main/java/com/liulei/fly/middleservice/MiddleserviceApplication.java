package com.liulei.fly.middleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.PathMatchConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

@SpringBootApplication
public class MiddleserviceApplication extends WebMvcConfigurationSupport {

    @Override
    protected void configurePathMatch(PathMatchConfigurer configurer) {
        configurer.setUseSuffixPatternMatch(true)//置是否是后缀模式匹配，如“/user”是否匹配/user.*；
                .setUseTrailingSlashMatch(false);//设置是否自动后缀路径模式匹配，如“/user”是否匹配“/user/”
    }

    public static void main(String[] args) {
        SpringApplication.run(MiddleserviceApplication.class, args);
    }

}

