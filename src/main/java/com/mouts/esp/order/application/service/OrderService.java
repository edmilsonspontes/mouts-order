package com.mouts.esp.order.application.service;

import com.mouts.esp.order.domain.entities.Order;

public interface OrderService {
	Order create(final Order pOrder);
	Order get(final String pOrderId);
	Order exists(final String pOrderId);
	Order existsInDatabase(final String orderId);
	Order existsInCache(final String orderId);
}
 