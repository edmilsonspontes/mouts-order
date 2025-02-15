package com.mouts.esp.order.infrastructure.adapters;

import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouts.esp.order.application.service.OrderService;
import com.mouts.esp.order.application.usecases.ProcessOrderUseCase;
import com.mouts.esp.order.domain.entities.Order;

class OrderConsumerQueueAdapterTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ObjectMapper objectMapper;
    
    @Mock
    private ProcessOrderUseCase processOrderUseCase;

    @InjectMocks
    private OrderConsumerQueueAdapter orderConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessMessageWhenValid() throws Exception {
        String orderId = "123";
        String message = "{\"orderId\":\"123\"}";
        Order order = Order.builder().orderId(orderId).totalAmount(100.00).status("NEW").build();

        when(objectMapper.readValue(message, Order.class)).thenReturn(order);
        when(orderService.getFromCaheOrDatabase(orderId)).thenReturn(null);

        orderConsumer.consume(message);

        verify(orderService, times(1)).create(order);
    }

    @Test
    void shouldIgnoreInvalidMessage() throws Exception {
        String invalidMessage = "INVALID_JSON";

        doThrow(new JsonProcessingException("JSON error") {}).when(objectMapper).readValue(invalidMessage, Order.class);

        orderConsumer.consume(invalidMessage);

        verifyNoInteractions(orderService);
    }

}
