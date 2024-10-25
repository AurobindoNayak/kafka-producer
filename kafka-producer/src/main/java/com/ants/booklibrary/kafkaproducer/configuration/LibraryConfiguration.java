package com.ants.booklibrary.kafkaproducer.configuration;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class LibraryConfiguration {

    @Bean
    public NewTopic createTopic(){
        return TopicBuilder.name("library-topic")
                .partitions(2)
                .replicas(2)
                .build();
    }
}
