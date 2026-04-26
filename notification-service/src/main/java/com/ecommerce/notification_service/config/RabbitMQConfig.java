package com.ecommerce.notification_service.config;

import com.ecommerce.notification_service.event.OrderCancelledEvent;
import com.ecommerce.notification_service.event.OrderConfirmedEvent;
import com.ecommerce.notification_service.event.OrderPlaceEvent;
import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultClassMapper;
import org.springframework.amqp.support.converter.JacksonJsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "order-events";

    @Bean
    public MessageConverter messageConverter(){
        JacksonJsonMessageConverter converter = new JacksonJsonMessageConverter();
        DefaultClassMapper classMapper = new DefaultClassMapper();

        classMapper.setTrustedPackages("*");

        Map<String, Class<?>> idClassMapping = new HashMap<>();

        idClassMapping.put("com.ecommerce.inventory_service.event.OrderPlaceEvent", OrderConfirmedEvent.class);
        idClassMapping.put("com.ecommerce.inventory_service.event.OrderCancelledEvent", OrderCancelledEvent.class);

        classMapper.setIdClassMapping(idClassMapping);
        converter.setClassMapper(classMapper);

        return converter;
    }

    @Bean
    public Queue notificationQueue(){
        return QueueBuilder.durable("notification-queue")
                .withArgument("x-dead-letter-exchange", "notification-dlx")
                .withArgument("x-dead-letter-routing-key", "notification.dead")
                .build();
    }

    @Bean
    public TopicExchange orderEventsExchanged(){
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Binding confirmedBinding(Queue notificationQueue, TopicExchange orderEventsExchanged){
        return BindingBuilder.bind(notificationQueue).to(orderEventsExchanged).with("order.confirmed");
    }

    @Bean
    public Binding cancelledBinding(Queue notificationQueue, TopicExchange orderEventsExchanged){
        return BindingBuilder.bind(notificationQueue).to(orderEventsExchanged).with("order.cancelled");
    }

    @Bean
    public DirectExchange deadLetterExchanged(){
        return new DirectExchange("notification-dlx");
    }

    @Bean
    public Queue deadLetterQueue(){
        return new Queue("notification-dlq", true);
    }

    @Bean
    public Binding deadLetterBinding(Queue deadLetterQueue, DirectExchange deadLetterExchanged){
        return BindingBuilder.bind(deadLetterQueue).to(deadLetterExchanged).with("notification.dead");
    }
}
