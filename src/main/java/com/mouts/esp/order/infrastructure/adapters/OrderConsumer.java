package com.mouts.esp.order.infrastructure.adapters;

import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouts.esp.order.application.service.OrderService;
import com.mouts.esp.order.domain.entities.Order;

@Component
public class OrderConsumer {

    private static final Logger logger = LoggerFactory.getLogger(OrderConsumer.class);

    private final OrderService orderService;
    private final ObjectMapper objectMapper;
    
    public OrderConsumer(OrderService orderService, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.objectMapper = objectMapper;
    }
    
    @Retryable(
        retryFor = { AmqpException.class },
        maxAttempts = 3,
        backoff = @Backoff(delay = 5000)
    )
    @RabbitListener(queues = "orders.queue")
    public void consume(String message) throws Exception {
        if (Objects.isNull(message) || message.isBlank()) {
            logger.warn("Mensagem vazia");
            return;
        }

        try {
            Order order = objectMapper.readValue(message, Order.class);

            if (Objects.isNull(order.getOrderId()) || order.getOrderId().isBlank()) {
                logger.error("Mensagem inválida/Pedido sem ID: {}", message);
                return;
            }

            if (Objects.nonNull(orderService.exists(order.getOrderId()))) {
                logger.warn("Pedido duplicado: {}", order.getOrderId());
                return;
            }

            orderService.create(order);
            logger.info("Pedido processado: {}", order.getOrderId());

        } catch (Exception e) {
            logger.error("Erro ao processar mensagem: {}", 
                message.length() > 100 ? message.substring(0, 100) : message, e);
            
            if (e instanceof com.fasterxml.jackson.core.JsonProcessingException) {
                logger.error("Mensagem corrompida: {}", message);
                return;
            }

            throw e;
        }
    }

}
