package com.mouts.esp.order.web.controllers;

import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mouts.esp.order.application.service.OrderService;
import com.mouts.esp.order.domain.entities.Order;
import com.mouts.esp.order.web.dtos.OrderRequestDTO;
import com.mouts.esp.order.web.dtos.OrderProcessedResponseDTO;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "API para gerenciamento de pedidos")
public class OrderController {

    private final OrderService orderService;
    private final ModelMapper modelMapper = new ModelMapper();

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @Operation(summary = "Buscar pedido por ID", description = "Retorna um pedidos processado")
    @GetMapping("/{orderId}")
    public ResponseEntity<OrderProcessedResponseDTO> getOrder(@PathVariable String orderId) {
        Order order = orderService.get(orderId);
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(modelMapper.map(order, OrderProcessedResponseDTO.class));
    }

    @Operation(summary = "Cria pedido", description = "Cria um pedido com lista de produtos para processamento.")
    @PostMapping
    public ResponseEntity<String> getOrder(@RequestBody OrderRequestDTO order) {
        if (order == null) {
            return ResponseEntity.notFound().build();
        }
        orderService.create(modelMapper.map(order, Order.class));
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

}
