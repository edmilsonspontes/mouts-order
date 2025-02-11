package com.mouts.esp.order.domain.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "orders")
public class Order implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    private String id;
    private String orderId;
    private List<Product> products;
    private double totalAmount;
    private String status = "Pending";

    public void calculateTotal() {
        if (products == null || products.isEmpty()) {
            totalAmount = 0.0;
        } else {
            totalAmount = products.stream()
                .mapToDouble(p -> p.getPrice() * p.getQuantity())
                .sum();
        }
        status = "Processed";
    }


    @Override
    public String toString() {
        return String.format("Order{id='%s', orderId='%s', totalAmount=%.2f, status='%s'}", id, orderId, totalAmount, status);
    }
    
}
