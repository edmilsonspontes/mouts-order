package com.mouts.esp.order.application.usecases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.adapters.OrderPublishQueueAdapter;
import com.mouts.esp.order.infrastructure.cache.OrderCacheService;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;

@Service
public class ProcessOrderUseCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessOrderUseCase.class);
    
    private final OrderRepository orderRepository;
    private final OrderCacheService orderCacheService;
    private final OrderPublishQueueAdapter orderPublishQueueAdapter;

    public ProcessOrderUseCase(OrderRepository orderRepository, OrderCacheService orderCacheService, OrderPublishQueueAdapter messageQueueAdapter) {
        this.orderRepository = orderRepository;
        this.orderCacheService = orderCacheService;
        this.orderPublishQueueAdapter = messageQueueAdapter;
    }

    public void process(final Order order) {
    	logger.info("Iniciando processamento: orderId={}", order.getOrderId());
        if (orderCacheService.get(order.getOrderId()) != null) {
            logger.warn("Pedido {} já processado, ignorando...", order.getOrderId());
            return;
        }

        if (orderRepository.findByOrderId(order.getOrderId()).isPresent()) {
            logger.warn("Pedido {} duplicado, ignorando...", order.getOrderId());
            return;
        }

        order.calculateTotal();
        orderRepository.save(order);
        orderCacheService.add(order.getOrderId(), order);
        
        logger.info("Pedido {} processado com sucesso.", order.getOrderId());
        try {
			orderPublishQueueAdapter.publish(order);
		} catch (Exception e) {
			logger.error("Erro ao processar mensagem: {}", e);
		}
    }
}
