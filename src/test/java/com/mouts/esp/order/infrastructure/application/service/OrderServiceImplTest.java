package com.mouts.esp.order.infrastructure.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

import com.mouts.esp.order.application.service.OrderServiceImpl;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.cache.OrderCacheService;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderCacheService orderCacheService;

    @Mock
    private Logger logger;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        Order order = Order.builder().orderId("123").build();
        when(orderRepository.findByOrderId("123")).thenReturn(Optional.of(order));
    }

    @Test
    void shouldCreateAndCacheOrderSuccessfully() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderRepository.save(order)).thenReturn(order);

        Order createdOrder = orderService.create(order);

        assertNotNull(createdOrder);
        verify(orderRepository, times(1)).save(order);
        verify(orderCacheService, times(1)).add(orderId, order);
    }

    @Test
    void shouldRetrieveOrderFromCacheIfExists() {
        String orderId = "123";
        Order cachedOrder = Order.builder().orderId(orderId).build();

        when(orderCacheService.get(orderId)).thenReturn(cachedOrder);

        Order orderResult = orderService.get(orderId);

        assertNotNull(orderResult);
        assertEquals(orderId, orderResult.getOrderId());
        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldRetrieveOrderFromDatabaseIfNotCached() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderCacheService.get(orderId)).thenReturn(null);
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));

        Order orderResult = orderService.get(orderId);

        assertNotNull(orderResult);
        assertEquals(orderId, orderResult.getOrderId());
        verify(orderCacheService, times(1)).add(orderId, orderResult);
    }

    @Test
    void shouldReturnNullWhenOrderDoesNotExist() {
        String orderId = "123";

        when(orderCacheService.get(orderId)).thenReturn(null);
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        Order orderResult = orderService.get(orderId);

        assertNull(orderResult);
    }

    @Test
    void shouldHandleErrorWhenSavingOrder() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        doThrow(new RuntimeException("Database error")).when(orderRepository).save(order);

        assertThrows(RuntimeException.class, () -> orderService.create(order));
        verifyNoInteractions(orderCacheService);
        verify(logger).error(eq("Erro ao salvar pedido: {}"), eq(order), any(RuntimeException.class));
    }
}
