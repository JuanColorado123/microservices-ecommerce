package com.ecommerce.order_service.config;

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
    public TopicExchange orderEventsExchanged(){
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue orderConfirmedQueue(){
        return new Queue("order-confirmed-queue",true);
    }

    @Bean
    public Binding confirmedBinding(Queue orderConfirmedQueue, TopicExchange orderEventsExchanged){
        return BindingBuilder.bind(orderConfirmedQueue).to(orderEventsExchanged).with("order.confirmed");
    }

    @Bean
    public Queue orderCancelleddQueue(){
        return new Queue("order-cancelled-queue",true);
    }

    @Bean
    public Binding cancelledBinding(Queue orderCancelleddQueue, TopicExchange orderEventsExchanged){
        return BindingBuilder.bind(orderCancelleddQueue).to(orderEventsExchanged).with("order.cancelled");
    }
}
