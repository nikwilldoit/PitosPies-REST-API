package com.pitospies.pitospieswebservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_item")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItem {
    private String pie;
    private Integer quantity;
}
