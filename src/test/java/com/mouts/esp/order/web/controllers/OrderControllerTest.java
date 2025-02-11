package com.mouts.esp.order.web.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.mouts.esp.order.application.service.OrderService;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.web.dtos.OrderResponseDTO;

class OrderControllerTest {

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderController orderController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldReturnOrderIfExists() {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();

        when(orderService.get(orderId)).thenReturn(order);

        ResponseEntity<OrderResponseDTO> response = orderController.getOrder(orderId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(orderId, response.getBody().getOrderId());
    }

    @Test
    void shouldReturnNotFoundIfOrderDoesNotExist() {
        String orderId = "123";

        when(orderService.get(orderId)).thenReturn(null);
        
        ResponseEntity<OrderResponseDTO> response = orderController.getOrder(orderId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }
}

