package com.mouts.esp.order.infrastructure.application.usecases;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;

import com.mouts.esp.order.application.service.OrderService;
import com.mouts.esp.order.application.usecases.ProcessOrderUseCase;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.adapters.OrderPublishQueueAdapter;
import com.mouts.esp.order.infrastructure.cache.OrderCacheService;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;

class ProcessOrderUseCaseTest {

    @Mock
    private OrderService orderService;
    
    @Mock
    private OrderCacheService orderCacheService;
    
    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderPublishQueueAdapter orderPublishQueueAdapter;

    @Mock
    private Logger logger;

    @InjectMocks
    private ProcessOrderUseCase processOrderUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessAndSaveOrder() throws Exception {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(orderCacheService.get(orderId)).thenReturn(null);

        order.calculateTotal();

        processOrderUseCase.process(order);

        verify(orderRepository, times(1)).save(order);
        verify(orderCacheService, times(1)).add(orderId, order);
        verify(orderPublishQueueAdapter, times(1)).publish(order);
    }


    @Test
    void shouldIgnoreDuplicateOrders() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));
        when(orderCacheService.get(orderId)).thenReturn(order);
        when(orderService.get(orderId)).thenReturn(order);

        processOrderUseCase.process(order);

        verifyNoInteractions(orderPublishQueueAdapter);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldHandleErrorWhenSavingOrder() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        doThrow(new RuntimeException("Database error")).when(orderRepository).save(order);

        assertThrows(RuntimeException.class, () -> processOrderUseCase.process(order));

        verifyNoInteractions(orderPublishQueueAdapter);
    }

    @Test
    void shouldProcessOrderWhenCacheIsUnavailable() throws Exception {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(orderCacheService.get(orderId)).thenReturn(null);
        when(orderService.get(orderId)).thenReturn(null);
        
        doThrow(new RuntimeException("Redis error")).when(orderCacheService).add(orderId, order);

        assertDoesNotThrow(() -> processOrderUseCase.process(order));

        verify(orderRepository, times(1)).save(order);
        verify(orderPublishQueueAdapter, times(1)).publish(order);
    }

}
