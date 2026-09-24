package com.LinkSphere.ConnectionService.config;

import com.LinkSphere.ConnectionService.event.ConnectionAcceptedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class KafkaConfig {
    @Bean
    public NewTopic connectionRequestTopic() {
        return new NewTopic("connection_request_topic", 3, (short) 1);
    }
    @Bean
    public NewTopic connectionAcceptedTopic() {
        return new NewTopic("connection_accepted_topic", 3, (short) 1);
    }

}
