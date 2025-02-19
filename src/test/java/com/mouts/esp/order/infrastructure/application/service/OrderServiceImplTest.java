package com.mouts.esp.order.infrastructure.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.data.redis.core.RedisTemplate;

import com.mouts.esp.order.application.service.OrderServiceImpl;
import com.mouts.esp.order.application.usecases.ProcessOrderUseCase;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.cache.OrderCacheService;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;
import com.mouts.esp.order.web.exceptions.OrderNotFoundException;

class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderCacheService orderCacheService;
    
    @Mock
    private ProcessOrderUseCase processOrderUseCase;
    
    @Mock
    private RedisTemplate<String, Order> redisTemplate;

    @Mock
    private Logger logger;

    @InjectMocks
    private OrderServiceImpl orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldCreateAndCacheOrderSuccessfully() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());
        when(redisTemplate.hasKey(anyString())).thenReturn(false);
        when(orderRepository.save(order)).thenReturn(order);

        Order createdOrder = orderService.create(order);

        assertNotNull(createdOrder);
    }

    @Test
    void shouldRetrieveOrderFromCacheIfExists() {
        String orderId = "123";
        Order cachedOrder = Order.builder().orderId(orderId).build();

        when(redisTemplate.hasKey(anyString())).thenReturn(false); 
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

        when(redisTemplate.hasKey(orderId)).thenReturn(false);
        when(orderCacheService.get(orderId)).thenReturn(null);
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));

        Order orderResult = orderService.get(orderId);

        assertNotNull(orderResult);
        assertEquals(orderId, orderResult.getOrderId());
        verify(orderCacheService, times(1)).add(orderId, orderResult);
    }

    
    @Test
    void shouldRetrieveOrderFromCacheIfAvailable() {
        String orderId = "123";
        Order cachedOrder = Order.builder().orderId(orderId).build();

        when(redisTemplate.hasKey(orderId)).thenReturn(true);
        when(orderCacheService.get(orderId)).thenReturn(cachedOrder);

        Order orderResult = orderService.get(orderId);

        assertNotNull(orderResult);
        assertEquals(orderId, orderResult.getOrderId());
        verify(orderCacheService, times(1)).get(orderId);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldThrowExceptionWhenOrderDoesNotExist() {
        String orderId = "123";

        when(redisTemplate.hasKey(orderId)).thenReturn(false);
        when(orderCacheService.get(orderId)).thenReturn(null);
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        assertThrows(OrderNotFoundException.class, () -> orderService.get(orderId));
        verify(orderRepository, times(1)).findByOrderId(orderId);
    }

    @Test
    void shouldHandleErrorWhenProcessOrder() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        doThrow(new RuntimeException("Erro ao processar pedido")).when(processOrderUseCase).process(any(Order.class));

        assertThrows(RuntimeException.class, () -> orderService.create(order));
    }


}
