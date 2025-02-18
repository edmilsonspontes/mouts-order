package com.mouts.esp.order.infrastructure.cache;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyString;
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

	    when(redisTemplate.hasKey(anyString())).thenReturn(false);
	    orderCacheService.add(orderId, order);

	    verify(valueOperations, times(1)).set(orderId, order, ttlTimeout, TimeUnit.MINUTES);
	}

	@Test
	void shouldRetrieveCachedOrderSuccessfully() {
	    String orderId = "order123";
	    Order order = Order.builder().orderId(orderId).build();

	    when(redisTemplate.opsForValue()).thenReturn(valueOperations); 
	    when(redisTemplate.hasKey(orderId)).thenReturn(true);
	    when(valueOperations.get(orderId)).thenReturn(order);

	    Order resultOrder = orderCacheService.get(orderId);

	    assertNotNull(resultOrder);
	    assertEquals(order, resultOrder);

	    verify(redisTemplate, times(1)).hasKey(orderId);
	    verify(valueOperations, times(1)).get(orderId);
	}


	@Test
	void shouldReturnNullIfOrderNotInCache() {
	    String orderId = "order123";

	    when(redisTemplate.hasKey(anyString())).thenReturn(false);

	    Order actualOrder = orderCacheService.get(orderId);

	    assertNull(actualOrder);
	}

	@Test
	void shouldReturnTrueIfOrderExistsInCache() {
	    String orderId = "order123";
	    Order order = Order.builder().orderId(orderId).build();

	    when(redisTemplate.hasKey(orderId)).thenReturn(true);
	    when(redisTemplate.opsForValue().get(orderId)).thenReturn(order);

	    Order result = orderCacheService.get(orderId);

	    assertNotNull(result);
	    assertEquals(orderId, result.getOrderId());

	    verify(redisTemplate, times(1)).hasKey(orderId);
	    verify(redisTemplate.opsForValue(), times(1)).get(orderId);
	}

	@Test
	void shouldReturnFalseIfOrderDoesNotExistInCache() {
	    String orderId = "order123";

	    when(redisTemplate.hasKey(orderId)).thenReturn(false);

	    boolean exists = Objects.nonNull(orderCacheService.get(orderId));

	    assertFalse(exists);
	}

}
