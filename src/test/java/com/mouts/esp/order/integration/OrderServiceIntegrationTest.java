package com.mouts.esp.order.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;

import com.mouts.esp.order.application.service.OrderService;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;

@SpringBootTest
@ContextConfiguration(initializers = TestContainersConfig.Initializer.class)
class OrderServiceIntegrationTest {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
    }

    @Test
    void shouldCreateAndRetrieveOrder() {
        Order order = Order.builder().orderId("12345").build();
        orderService.create(order);

        Order retrievedOrder = orderService.get("12345");

        assertNotNull(retrievedOrder);
        assertEquals("12345", retrievedOrder.getOrderId());
    }

    @Test
    void shouldReturnNullForNonExistentOrder() {
        Order retrievedOrder = orderService.get("99999");
        assertNull(retrievedOrder);
    }
}
