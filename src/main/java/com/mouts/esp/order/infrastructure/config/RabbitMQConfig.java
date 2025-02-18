package com.mouts.esp.order.infrastructure.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitAdmin;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String ORDERS_GENERATED_EXCHANGE = "orders.generated.exchange";
    public static final String ORDERS_PROCESSED_EXCHANGE = "orders.processed.exchange";
    public static final String ORDERS_GENERATED_ROUTING_KEY = "orders.generated.key";
    public static final String ORDERS_PROCESSED_ROUTING_KEY = "orders.processed.key";
    public static final String ORDERS_GENERATED_QUEUE = "orders.generated.queue";
    public static final String ORDERS_PROCESSED_QUEUE = "orders.processed.queue";

    @Bean
    public Queue ordersGeneratedQueue() {
        return new Queue(ORDERS_GENERATED_QUEUE, true);
    }

    @Bean
    public Queue ordersProcessedQueue() {
        return new Queue(ORDERS_PROCESSED_QUEUE, true);
    }

    @Bean
    public TopicExchange ordersGeneratedExchange() {
        return new TopicExchange(ORDERS_GENERATED_EXCHANGE);
    }

    @Bean
    public TopicExchange ordersProcessedExchange() {
        return new TopicExchange(ORDERS_PROCESSED_EXCHANGE);
    }

    @Bean
    public Binding bindingOrdersGenerated(Queue ordersGeneratedQueue, TopicExchange ordersGeneratedExchange) {
        return BindingBuilder.bind(ordersGeneratedQueue).to(ordersGeneratedExchange).with(ORDERS_GENERATED_ROUTING_KEY);
    }

    @Bean
    public Binding bindingOrdersProcessed(Queue ordersProcessedQueue, TopicExchange ordersProcessedExchange) {
        return BindingBuilder.bind(ordersProcessedQueue).to(ordersProcessedExchange).with(ORDERS_PROCESSED_ROUTING_KEY);
    }

    @Bean
    public RabbitAdmin rabbitAdmin(RabbitTemplate rabbitTemplate, ConnectionFactory connectionFactory) {
        RabbitAdmin rabbitAdmin = new RabbitAdmin(connectionFactory);
        rabbitAdmin.declareQueue(ordersGeneratedQueue());
        rabbitAdmin.declareQueue(ordersProcessedQueue());
        rabbitAdmin.declareExchange(ordersGeneratedExchange());
        rabbitAdmin.declareExchange(ordersProcessedExchange());
        rabbitAdmin.declareBinding(bindingOrdersGenerated(ordersGeneratedQueue(), ordersGeneratedExchange()));
        rabbitAdmin.declareBinding(bindingOrdersProcessed(ordersProcessedQueue(), ordersProcessedExchange()));
        rabbitAdmin.setAutoStartup(true);
        return rabbitAdmin;
    }
}
