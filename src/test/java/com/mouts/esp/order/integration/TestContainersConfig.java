package com.mouts.esp.order.integration;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

@Testcontainers
@SpringBootTest
public class TestContainersConfig {
	final static int REDIS_CONTAINER_PORT = 6379;
	final static int MONGOBD_CONTAINER_PORT = 27017;
	final static Integer[] RABITMQ_CONTAINER_PORT = {5672, 15672};
	
    public static final GenericContainer<?> redisContainer = 
        	new GenericContainer<>(DockerImageName.parse("redis:7.0")).withExposedPorts(REDIS_CONTAINER_PORT);

    public static final MongoDBContainer mongoDBContainer = 
        new MongoDBContainer(DockerImageName.parse("mongo:7.0")).withExposedPorts(MONGOBD_CONTAINER_PORT);

    public static final RabbitMQContainer rabbitMQContainer = 
        new RabbitMQContainer(DockerImageName.parse("rabbitmq:3-management")).withExposedPorts(RABITMQ_CONTAINER_PORT);

/*    
//    @Container
//    public static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0");

    @DynamicPropertySource
    static void setProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }
*/    

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

    public static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext context) {
            TestPropertyValues.of(
                "spring.data.mongodb.uri=" + mongoDBContainer.getReplicaSetUrl(),
                "spring.rabbitmq.host=" + rabbitMQContainer.getHost(),
                "spring.rabbitmq.port=" + rabbitMQContainer.getMappedPort(RABITMQ_CONTAINER_PORT[0]),
                "spring.redis.host=" + redisContainer.getHost(),
                "spring.redis.port=" + redisContainer.getMappedPort(REDIS_CONTAINER_PORT)
            ).applyTo(context.getEnvironment());
        }
    }
}
