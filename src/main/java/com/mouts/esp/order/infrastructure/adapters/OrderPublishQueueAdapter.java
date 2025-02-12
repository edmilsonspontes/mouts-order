package com.mouts.esp.order.infrastructure.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.config.RabbitMQConfig;

@Service
public class OrderPublishQueueAdapter {
	
    private static final Logger logger = LoggerFactory.getLogger(OrderPublishQueueAdapter.class);

    private final RabbitTemplate rabbitTemplate;

    public OrderPublishQueueAdapter(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publish(Order order) throws Exception {
        try {
        	String message = convertToJson(order);
            rabbitTemplate.convertAndSend(RabbitMQConfig.ORDERS_PROCESSED_EXCHANGE, RabbitMQConfig.ORDERS_PROCESSED_ROUTING_KEY, message);
            System.out.println("✅ Pedido publicado no RabbitMQ: " + order.getOrderId());
        } catch (Exception e) {
            logger.error("Erro ao publicar pedido no RabbitMQ: ", e);
            throw e;
        }
    }


    private String convertToJson(Order order) throws Exception {
        try {
            return new ObjectMapper().writeValueAsString(order);
        } catch (Exception e) {
            logger.error("Erro ao converter pedido para JSON", e);
            throw e;
        }
    }

}