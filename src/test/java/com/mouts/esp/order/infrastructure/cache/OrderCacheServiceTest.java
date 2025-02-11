package com.mouts.esp.order.infrastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import com.mouts.esp.order.domain.entities.Order;

class OrderCacheServiceTest {

	@Mock
	private RedisTemplate<String, Order> redisTemplate;

	@Mock
	private ValueOperations<String, Order> valueOperations;

	@InjectMocks
	private OrderCacheService orderCacheService;

	@Value("${cache.order.ttl:10}")
	private long ttlTimeout;

	@BeforeEach
	void setUp() {
	    MockitoAnnotations.openMocks(this);
	    when(redisTemplate.opsForValue()).thenReturn(valueOperations);
	}

	@Test
	void shouldCacheOrderSuccessfully() {
	    String orderId = "order123";
	    Order order = Order.builder().orderId(orderId).build();

	    orderCacheService.add(orderId, order);

	    verify(valueOperations, times(1)).set(orderId, order, ttlTimeout, TimeUnit.MINUTES);
	}

	@Test
	void shouldRetrieveCachedOrderSuccessfully() {
	    String orderId = "order123";
	    Order expectedOrder = Order.builder().orderId(orderId).build();

	    when(valueOperations.get(orderId)).thenReturn(expectedOrder);

	    Order actualOrder = orderCacheService.get(orderId);

	    assertNotNull(actualOrder);
	    assertEquals(expectedOrder, actualOrder);
	    verify(valueOperations, times(1)).get(orderId);
	}

	@Test
	void shouldReturnNullIfOrderNotInCache() {
	    String orderId = "order123";

	    when(valueOperations.get(orderId)).thenReturn(null);

	    Order actualOrder = orderCacheService.get(orderId);

	    assertNull(actualOrder);
	    verify(valueOperations, times(1)).get(orderId);
	}

	@Test
	void shouldHandleRedisFailureOnCacheAdd() {
	    String orderId = "order123";
	    Order order = Order.builder().orderId(orderId).build();

	    doThrow(new RuntimeException("Redis error")).when(valueOperations).set(orderId, order, ttlTimeout, TimeUnit.MINUTES);

	    Exception exception = assertThrows(RuntimeException.class, () -> orderCacheService.add(orderId, order));
	    assertTrue(exception.getMessage().contains("Redis error"));
	}

	@Test
	void shouldReturnTrueIfOrderExistsInCache() {
	    String orderId = "order123";

	    when(redisTemplate.hasKey(orderId)).thenReturn(true);

	    boolean exists = Objects.nonNull(orderCacheService.get(orderId));

	    assertTrue(exists);
	    verify(redisTemplate, times(1)).hasKey(orderId);
	}

	@Test
	void shouldReturnFalseIfOrderDoesNotExistInCache() {
	    String orderId = "order123";

	    when(redisTemplate.hasKey(orderId)).thenReturn(false);

	    boolean exists = Objects.nonNull(orderCacheService.get(orderId));

	    assertFalse(exists);
	    verify(redisTemplate, times(1)).hasKey(orderId);
	}

}
