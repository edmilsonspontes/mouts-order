package com.mouts.esp.order.infrastructure.adapters;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouts.esp.order.application.usecases.ProcessOrderUseCase;
import com.mouts.esp.order.domain.entities.Order;

class OrderConsumerTest {

    @Mock
    private ProcessOrderUseCase processOrderUseCase;

    @Mock
    private ObjectMapper objectMapper;

    @Mock
    private Logger logger;

    @InjectMocks
    private OrderConsumer orderConsumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldProcessMessageWhenValid() throws Exception {
        String orderId = "123";
        String message = "{\"orderId\":\"123\"}";
        Order order = Order.builder().orderId(orderId).build();

        when(objectMapper.readValue(message, Order.class)).thenReturn(order);

        orderConsumer.consume(message);

        verify(processOrderUseCase, times(1)).process(order);
    }

    @Test
    void shouldHandleInvalidMessageGracefully() throws Exception {
        String invalidMessage = "INVALID_JSON";

        doThrow(new JsonProcessingException("JSON error") {}).when(objectMapper).readValue(invalidMessage, Order.class);

        orderConsumer.consume(invalidMessage);

        verifyNoInteractions(processOrderUseCase);
        verify(logger).error(eq("Erro ao desserializar mensagem: {}"), eq(invalidMessage), any(JsonProcessingException.class));
    }

    @Test
    void shouldHandleProcessingExceptionGracefully() throws Exception {
        String message = "{\"orderId\":\"123\"}";
        Order order = Order.builder().orderId("123").build();

        when(objectMapper.readValue(message, Order.class)).thenReturn(order);
        doThrow(new RuntimeException("Processing error")).when(processOrderUseCase).process(order);

        orderConsumer.consume(message);

        verify(processOrderUseCase, times(1)).process(order);
        verify(logger).error(eq("Erro ao processar pedido: {}"), eq(order), any(RuntimeException.class));
    }
}
