package com.garloinvest.searcher.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.client.RestTemplate;

import com.garloinvest.searcher.oanda.connection.OandaConnectionFXPractice;

@Configuration
@PropertySource(value={"urls.properties", "oanda.properties"},ignoreResourceNotFound = true)
@PropertySource(value={"file:urls.properties", "file:oanda.properties"},ignoreResourceNotFound = true)
@PropertySource(value={"file:config/urls.properties", "file:config/oanda.properties"},ignoreResourceNotFound = true)
@PropertySource(value={"file:config/urls.yml", "file:config/oanda.yml"},ignoreResourceNotFound = true)
public class SearcherConfig {

    @Bean
    public RestTemplate getRestTemplate() {
        return new RestTemplate();
    }
    
    /*@Bean
    public OandaConnectionFXPractice getOndaConnection() {
    	return new OandaConnectionFXPractice();
    }*/
}
