package com.entr.sbdem.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AdditionalResourceWebConfig implements WebMvcConfigurer {

    final private String pathToUploadDir;
    final private FileUploadPathProperties uploadPathProperties;

    public AdditionalResourceWebConfig(final FileUploadPathProperties uploadPathProperties) {
        this.uploadPathProperties = uploadPathProperties;
        this.pathToUploadDir = uploadPathProperties.getPathToUploadDirToUriToString();
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/upload/**")
                .addResourceLocations("file:/"+ pathToUploadDir +"/")
                .setCachePeriod(0);
        registry.addResourceHandler(("/resources/**"))
                .addResourceLocations("classpath:/resources/")
                .setCachePeriod(0);
    }

   /* @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**").allowedMethods("GET", "POST", "PUT", "DELETE", "HEAD", "OPTIONS")
                .allowedOrigins("http://localhost:8080/**")
                .allowedHeaders("Authorization", "Cache-Control", "Content-Type", "Accept", "X-Requested-With", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin")
                .exposedHeaders("Access-Control-Expose-Headers", "Authorization", "Cache-Control", "Content-Type", "Access-Control-Allow-Origin", "Access-Control-Allow-Headers", "Origin");

    }*/


}
