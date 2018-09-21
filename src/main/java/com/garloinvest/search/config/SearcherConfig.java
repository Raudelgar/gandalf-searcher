package com.garloinvest.search.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

@Configuration
@PropertySource(value={"urls.properties"},ignoreResourceNotFound = true)
@PropertySource(value={"file:urls.properties"},ignoreResourceNotFound = true)
@PropertySource(value={"file:config/urls.properties"},ignoreResourceNotFound = true)
@PropertySource(value={"file:config/urls.yml"},ignoreResourceNotFound = true)
public class SearcherConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
}
