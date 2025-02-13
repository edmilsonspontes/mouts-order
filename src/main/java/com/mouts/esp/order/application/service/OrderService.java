package com.mouts.esp.order.application.service;

import com.mouts.esp.order.domain.entities.Order;

public interface OrderService {
	Order create(final Order pOrder);
	Order get(final String pOrderId);
	Order getFromCaheOrDatabase(final String pOrderId);
	Order getFromDatabase(final String orderId);
	Order getFromCache(final String orderId);
}
 