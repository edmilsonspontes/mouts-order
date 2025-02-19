package com.mouts.esp.order.infrastructure.cache;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.mouts.esp.order.domain.entities.Order;

@Service
public class OrderCacheService {

	private static final Logger logger = LoggerFactory.getLogger(OrderCacheService.class);

	@Value("${cache.order.ttl:10}")
	private long ttlTimeout;

	private final RedisTemplate<String, Order> redisTemplate;

	public OrderCacheService(RedisTemplate<String, Order> redisTemplate) {
	    this.redisTemplate = redisTemplate;
	}

	public void add(String orderId, Order order) {
		logger.info("Tentando adicionar pedido {} ao Redis: {}", orderId, order);
	    try {
	        redisTemplate.opsForValue().set("order", order, ttlTimeout, TimeUnit.MINUTES);
	        logger.info("Pedido {} adicionado ao cache Redis com timeout de {} minutos", orderId, ttlTimeout);
	    } catch (Exception e) {
	        logger.error("Erro ao adicionar pedido {} ao cache: {}", orderId, e.getMessage(), e);
	    }
	}

	public Order get(String orderId) {
		Order order = null;
	    if (Boolean.TRUE.equals(redisTemplate.hasKey(orderId))) {
	    	order = redisTemplate.opsForValue().get(orderId);
	    }

	    if (Objects.nonNull(order)) {
	        logger.debug("Pedido {} recuperado do cache", orderId);
	    } else {
	        logger.debug("Pedido {} não encontrado no cache", orderId);
	    }
	    return order;
	}

}
