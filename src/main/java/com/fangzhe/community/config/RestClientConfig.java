package com.fangzhe.community.config;

import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.RestClients;
import org.springframework.data.elasticsearch.config.AbstractElasticsearchConfiguration;
import reactor.util.annotation.NonNull;

/**
 * @author ASUS
 * 与配置文件
 *   elasticsearch:
 *     rest:
 *       uris: http://localhost:9200
 *  同样作用
 *  springboot 2.5.6 elasticsearch 7.12.1
 */
/*
@Configuration
public class RestClientConfig extends AbstractElasticsearchConfiguration {

    @Override
    @Bean
    @NonNull
    public RestHighLevelClient elasticsearchClient() {

        final ClientConfiguration clientConfiguration
                = ClientConfiguration.builder()
            .connectedTo("localhost:9200")
            .build();

        return RestClients.create(clientConfiguration).rest();
    }
}*/
