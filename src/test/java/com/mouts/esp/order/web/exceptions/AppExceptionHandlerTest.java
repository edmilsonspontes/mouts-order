package com.mouts.esp.order.web.exceptions;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

class AppExceptionHandlerTest {

    @InjectMocks
    private AppExceptionHandler exceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldHandleOrderNotFoundException() {
        OrderNotFoundException exception = new OrderNotFoundException("Pedido não encontrado.");
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleOrderNotFoundException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("Pedido não encontrado.", response.getBody().get("error"));
    }

    @Test
    void shouldHandleOrderAlreadyExistsException() {
        OrderAlreadyExistsException exception = new OrderAlreadyExistsException("Pedido já existe.");
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleOrderAlreadyExistsException(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("Pedido já existe.", response.getBody().get("error"));
    }

    @Test
    void shouldHandleArgumentNotValidException() {
        BindingResult bindingResult = new BeanPropertyBindingResult(new Object(), "orderRequest");
        bindingResult.addError(new FieldError("orderRequest", "orderId", "O ID do pedido é obrigatório."));
        bindingResult.addError(new FieldError("orderRequest", "products", "A lista de produtos não pode estar vazia."));

        MethodArgumentNotValidException exception = new MethodArgumentNotValidException(null, bindingResult);

        ResponseEntity<Map<String, String>> response = exceptionHandler.handleValidationException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("O ID do pedido é obrigatório.", response.getBody().get("orderId"));
        assertEquals("A lista de produtos não pode estar vazia.", response.getBody().get("products"));
    }

    @Test
    void shouldHandleGeneralException() {
        Exception exception = new Exception("Erro inesperado.");
        ResponseEntity<Map<String, String>> response = exceptionHandler.handleGeneralException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("Ocorreu um erro inesperado. Tente novamente mais tarde.", response.getBody().get("error"));
    }

}
