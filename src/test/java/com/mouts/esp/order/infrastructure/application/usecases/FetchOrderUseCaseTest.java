package com.mouts.esp.order.infrastructure.application.usecases;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
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

import com.mouts.esp.order.application.usecases.FetchOrderUseCase;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.cache.OrderCacheService;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;

class FetchOrderUseCaseTest {

    @Mock
    private OrderCacheService orderCacheService;

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private FetchOrderUseCase fetchOrderUseCase;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnOrderFromCacheIfExists() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderCacheService.get(orderId)).thenReturn(order);

        Order fetchOrder = fetchOrderUseCase.fetchOrderById(orderId);

        assertNotNull(fetchOrder);
        assertEquals(orderId, fetchOrder.getOrderId());
        verify(orderCacheService, times(1)).get(orderId);
        verifyNoInteractions(orderRepository);
    }

    @Test
    void shouldReturnOrderFromDatabaseIfNotInCache() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderCacheService.get(orderId)).thenReturn(null);
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.of(order));

        Order fetchOrder = fetchOrderUseCase.fetchOrderById(orderId);

        assertNotNull(fetchOrder);
        assertEquals(orderId, fetchOrder.getOrderId());
        verify(orderCacheService, times(1)).add(orderId, order);
    }

    @Test
    void shouldReturnNullIfOrderNotFoundAnywhere() {
        String orderId = "123";

        when(orderCacheService.get(orderId)).thenReturn(null);
        when(orderRepository.findByOrderId(orderId)).thenReturn(Optional.empty());

        Order order = fetchOrderUseCase.fetchOrderById(orderId);

        assertNull(order);
    }
}
