package com.mouts.esp.order.web.exceptions;

public class OrderAlreadyExistsException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public OrderAlreadyExistsException(String message) {
        super(message);
    }
}
