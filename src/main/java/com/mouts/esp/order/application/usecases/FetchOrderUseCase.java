package com.mouts.esp.order.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.cache.OrderCacheService;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;

@Service
public class FetchOrderUseCase {

    private static final Logger logger = LoggerFactory.getLogger(FetchOrderUseCase.class);

    private final OrderCacheService orderCacheService;
    private final OrderRepository orderRepository;

    public FetchOrderUseCase(OrderCacheService orderCacheService, OrderRepository orderRepository) {
        this.orderCacheService = orderCacheService;
        this.orderRepository = orderRepository;
    }

    public Order fetchOrderById(final String orderId) {
        Order cachedOrder = orderCacheService.get(orderId);
        if (cachedOrder != null) {
            logger.debug("Pedido recuperado do cache: {}", orderId);
            return cachedOrder;
        }

        return orderRepository.findByOrderId(orderId)
                .map(order -> {
                    orderCacheService.add(orderId, order);
                    logger.debug("Pedido recuperado do banco e adicionado ao cache: {}", orderId);
                    return order;
                })
                .orElseGet(() -> {
                    logger.warn("Pedido não encontrado para ID: {}", orderId);
                    return null;
                });
    }
}
