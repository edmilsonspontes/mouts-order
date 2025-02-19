package com.mouts.esp.order.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;

import com.mouts.esp.order.application.service.OrderService;
import com.mouts.esp.order.domain.entities.Order;

@SpringBootTest
@ContextConfiguration(initializers = OrderIT.Initializer.class)
class OrderServiceIT {

    @Autowired
    private OrderService orderService;

    @Container
    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0")
            .withReuse(true)
            .withExposedPorts(27017);

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
        System.setProperty("spring.data.mongodb.uri", mongoDBContainer.getReplicaSetUrl());
    }


    @Test
    void shouldCreateAndRetrieveOrder() {
        String orderId = "12345";
        
		Order order = Order.builder().orderId(orderId).build();
        orderService.create(order);

        Order retrievedOrder = orderService.get(orderId);

        assertNotNull(retrievedOrder);
        assertEquals(orderId, retrievedOrder.getOrderId());
    }

    @Test
    void shouldReturnNullForNonExistentOrder() {
        Order retrievedOrder = orderService.get("99999");
        assertNull(retrievedOrder);
    }
}
