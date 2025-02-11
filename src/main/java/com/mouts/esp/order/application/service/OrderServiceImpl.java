package com.mouts.esp.order.application.service;

import java.util.Objects;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.cache.OrderCacheService;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;
import com.mouts.esp.order.web.exceptions.OrderAlreadyExistsException;
import com.mouts.esp.order.web.exceptions.OrderNotFoundException;

@Service
public class OrderServiceImpl implements OrderService {
    
    private static final Logger logger = LoggerFactory.getLogger(OrderServiceImpl.class);

    private final OrderRepository orderRepository;
    private final OrderCacheService orderCacheService;

    public OrderServiceImpl(OrderRepository orderRepository, OrderCacheService orderCacheService) {
        this.orderRepository = orderRepository;
        this.orderCacheService = orderCacheService;
    }

    @Override
    public Order create(final Order order) {
        logger.info("Criando pedido: {}", order.getOrderId());
        
        if (exists(order.getOrderId()) != null) {
            throw new OrderAlreadyExistsException("Pedido com ID " + order.getOrderId() + " já cadastrado.");
        }

        Order savedOrder = orderRepository.save(order);
        logger.info("Pedido salvo no banco: {}", savedOrder);
        orderCacheService.add(savedOrder.getOrderId(), savedOrder);

        return savedOrder;
    }
    
    @Override
    public Order get(final String orderId) {
        return Optional.ofNullable(exists(orderId))
                .orElseThrow(() -> new OrderNotFoundException("Pedido com ID " + orderId + " não encontrado."));
    }

    @Override
    public Order exists(final String orderId) {
        return Optional.ofNullable(existsInCache(orderId))
                .orElseGet(() -> existsInDatabase(orderId));
    }

    @Override
    public Order existsInCache(final String orderId) {
        Order order = (Order) orderCacheService.get(orderId);
        if (Objects.nonNull(order)) {
            logger.debug("Pedido encontrado no cache: {}", orderId);
        }
        return order;
    }

    @Override
    public Order existsInDatabase(final String orderId) {
        Optional<Order> orderOptional = orderRepository.findByOrderId(orderId);
        
        orderOptional.ifPresent(order -> {
            orderCacheService.add(orderId, order);
            logger.debug("Pedido recuperado da base de dados: {}", orderId);
        });

        return orderOptional.orElse(null);
    }

}
