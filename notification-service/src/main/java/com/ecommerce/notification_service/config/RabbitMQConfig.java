package com.ecommerce.notification_service.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "order-events";

    @Bean
    public MessageConverter messageConverter(){
        return new JacksonJsonMessageConverter();
    }

    @Bean
    public Queue notificationQueue(){
        return new Queue("notification-queue", true);
    }

    @Bean
    public TopicExchange orderEventsExchanged(){
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding confirmedBinding(Queue notificationQueue, TopicExchange topicExchange){
        return BindingBuilder.bind(notificationQueue).to(topicExchange).with("order.confirmed");
    }

    @Bean
    public Binding cancelledBinding(Queue notificationQueue, TopicExchange topicExchange){
        return BindingBuilder.bind(notificationQueue).to(topicExchange).with("order.cancelled");
    }
}
