package com.mouts.esp.order.integration;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mouts.esp.order.application.service.OrderService;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.infrastructure.config.RabbitMQConfig;
import com.mouts.esp.order.infrastructure.repositories.OrderRepository;

@SpringBootTest
@AutoConfigureMockMvc
class OrderControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;
    
    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;
    

    @Autowired
    private RedisTemplate<String, Order> redisTemplate;

    @Autowired
    private RabbitTemplate rabbitTemplate;    

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll();
    }

    @Test
    void shouldCreateOrder() throws Exception {
    	Order order = Order.builder().orderId("12345").build();

        mockMvc.perform(post("/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(order)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value("12345"));
    }

    @Test
    void shouldReturnNotFoundForNonExistentOrder() throws Exception {
        mockMvc.perform(get("/orders/99999"))
                .andExpect(status().isNotFound());
    }
    
    @Test
    void shouldCreateAndRetrieveOrderSuccessfully() throws Exception {
        String orderId = "123";
        Order order = Order.builder().orderId(orderId).build();
        orderService.create(order);

        Optional<Order> savedOrder = orderRepository.findByOrderId(orderId);
        assertTrue(savedOrder.isPresent(), "Pedido não foi salvo na base de dados!");

        Order cachedOrder = redisTemplate.opsForValue().get(orderId);
        assertNotNull(cachedOrder, "Pedido não foi salvo no cache!");

        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDERS_PROCESSED_QUEUE, order);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Order receivedOrder = (Order) rabbitTemplate.receiveAndConvert(RabbitMQConfig.ORDERS_PROCESSED_QUEUE);
            assertNotNull(receivedOrder, "Pedido não foi processado pelo RabbitMQ!");
        });
    }
    
    @Test
    void testOrderProcessingInRabbitMQ() throws Exception {
        Order order = Order.builder().orderId("456").build();
        rabbitTemplate.convertAndSend(RabbitMQConfig.ORDERS_GENERATED_QUEUE, order);

        Awaitility.await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Optional<Order> processedOrder = orderRepository.findByOrderId("456");
            assertTrue(processedOrder.isPresent(), "Pedido não foi processado corretamente pelo RabbitMQ.");
        });
    }

    
}
