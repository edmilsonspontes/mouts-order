package com.mouts.esp.order.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.Duration;
import java.util.Optional;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.mouts.esp.order.application.service.OrderService;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;

@Testcontainers
@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.mongodb.uri=mongodb://localhost:27017/orderdb",
    "spring.rabbitmq.host=localhost",
    "spring.rabbitmq.port=5672",
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379"
})
public class OrderIntegrationTest {

    @Container
    @ServiceConnection
    private static final MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @Container
    @ServiceConnection
    private static final RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3.12-management");

    @Container
    @ServiceConnection
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.0").withExposedPorts(6379);

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private RedisTemplate<String, Order> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @BeforeAll
    static void startContainers() {
        mongoDBContainer.start();
        rabbitMQContainer.start();
        redisContainer.start();
    }

    @AfterAll
    static void stopContainers() {
        mongoDBContainer.stop();
        rabbitMQContainer.stop();
        redisContainer.stop();
    }

    @Test
    void shouldCreateAndRetrieveOrderSuccessfully() {
        String orderId = "123";

        Order order = Order.builder().orderId(orderId).build();
        orderService.create(order);

        Optional<Order> savedOrder = orderRepository.findByOrderId(orderId);
        assertTrue(savedOrder.isPresent(), "Pedido não foi salvo na base de dados/Mongo!");

        Order cachedOrder = redisTemplate.opsForValue().get(orderId);
        assertNotNull(cachedOrder, "Pedido não foi salvo no cache/Redis!");

        rabbitTemplate.convertAndSend("orders.queue", order);
        assertTimeout(Duration.ofSeconds(5), () -> {
            Order receivedOrder = (Order) rabbitTemplate.receiveAndConvert("orders.queue");
            assertNotNull(receivedOrder, "Pedido não foi processado pelo RabbitMQ!");
        });
    }

}
