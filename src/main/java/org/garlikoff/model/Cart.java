package org.garlikoff.model;

import lombok.Builder;
import lombok.Data;
import lombok.Singular;

import java.util.Set;

@Data
@Builder
public class Cart {
    private String id;
    private String userId;

    @Singular
    private Set<CartItem> cartItems;

    public Integer count(){
        return cartItems.size();
    }

    public Double getTotal(){
        return cartItems.stream()
                .mapToDouble(item-> item.getPrice()*item.getQuantity())
                .sum();
    }

}
