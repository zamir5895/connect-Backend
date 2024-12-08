package com.backend.places;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;


@Configuration
public class Configuraciones {
    @Bean
    public StandardServletMultipartResolver multipartResolver() {
        return new StandardServletMultipartResolver();
    }
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
