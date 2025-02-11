package com.mouts.esp.order.infrastructure.adapters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouts.esp.order.domain.entities.Order;

@Service
public class MessageQueueAdapter {

    private static final Logger logger = LoggerFactory.getLogger(MessageQueueAdapter.class);

    private final RabbitTemplate rabbitTemplate;
    private final ObjectMapper objectMapper;

    public MessageQueueAdapter(RabbitTemplate rabbitTemplate, ObjectMapper objectMapper) {
        this.rabbitTemplate = rabbitTemplate;
        this.objectMapper = objectMapper;
    }

    public void publish(Order order) {
        try {
            String message = objectMapper.writeValueAsString(order);
            String routingKey = "orderId";
            String exchange = "orders.exchange";

            rabbitTemplate.convertAndSend(exchange, routingKey, message);
            logger.info("Pedido publicado na fila com routingKey {}: {}", routingKey, order.getOrderId());
        } catch (Exception e) {
            logger.error("Erro ao publicar pedido: {}", e.getMessage(), e);
        }
    }
}