// config/RabbitMQConfig.java
package com.example.user.config;
/*
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;*/

import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE = "transaction.exchange";
    public static final String QUEUE = "transaction.queue";
    public static final String ROUTING_KEY = "transaction.routing";

    //TODO: RabbitMQ server will be installed and application.properties will be updated
    /*
    @Bean
    public DirectExchange exchange() {
        return new DirectExchange(EXCHANGE);
    }

    @Bean
    public Queue queue() {
        return new Queue(QUEUE, true);
    }

    @Bean
    public Binding binding(Queue queue, DirectExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }*/
}
