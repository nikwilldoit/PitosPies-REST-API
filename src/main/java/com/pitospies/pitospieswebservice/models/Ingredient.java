package com.pitospies.pitospieswebservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("ingredient")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Ingredient {
    @Id
    Integer id;
    String name;
}