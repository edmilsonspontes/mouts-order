package com.mouts.esp.order.domain.entities;

import java.io.Serializable;
import java.util.Objects;

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
public class Product implements Serializable {
	private static final long serialVersionUID = 1L;
	
    private String id;
    private String name;
    private double price;
    private int quantity;

    @Override
    public String toString() {
        return String.format("Product{id='%s', name='%s', price=%.2f, quantity=%d}", id, name, price, quantity);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) 
        	return true;
        if (o == null || getClass() != o.getClass()) 
        	return false;
        Product product = (Product) o;
        return product.name.equals(((Product)o).getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, price, quantity);
    }
}
