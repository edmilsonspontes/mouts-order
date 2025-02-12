package com.mouts.esp.order.integration;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers
@SpringBootTest
public class OrderIT {

    @Container
    static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @Container
    static RabbitMQContainer rabbitMQContainer = new RabbitMQContainer("rabbitmq:3-management")
        .withExposedPorts(5672, 15672);

    @Container
    static GenericContainer<?> redisContainer = new GenericContainer<>("redis:latest")
        .withExposedPorts(6379);

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
        registry.add("spring.rabbitmq.host", rabbitMQContainer::getHost);
        registry.add("spring.rabbitmq.port", () -> rabbitMQContainer.getMappedPort(5672));
        registry.add("spring.redis.host", redisContainer::getHost);
        registry.add("spring.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @BeforeAll
    static void setUp() {
        mongoDBContainer.start();
        rabbitMQContainer.start();
        redisContainer.start();
    }

    @AfterAll
    static void tearDown() {
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        mongoDBContainer.stop();
        rabbitMQContainer.stop();
        redisContainer.stop();
    }

    @Test
    void testMongoDBConnection() {
        assertTrue(mongoDBContainer.isRunning(), "MongoDB deve estar rodando.");
    }

    @Test
    void testRabbitMQConnection() {
        assertTrue(rabbitMQContainer.isRunning(), "RabbitMQ deve estar rodando.");
    }

    @Test
    void testRedisConnection() {
        assertTrue(redisContainer.isRunning(), "Redis deve estar rodando.");
    }
    
    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                "spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl(),
                "spring.rabbitmq.host=" + rabbitMQContainer.getHost(),
                "spring.rabbitmq.port=" + rabbitMQContainer.getMappedPort(5672),
                "spring.redis.host=" + redisContainer.getHost(),
                "spring.redis.port=" + redisContainer.getMappedPort(6379)
            ).applyTo(context.getEnvironment());
        }
    }
 
}
