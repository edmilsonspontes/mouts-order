package com.mouts.esp.order.web.dtos;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderResponseDTO {
    private String orderId;
    private double totalAmount;
    private String status;
    private List<ProductDTO> products;

}

