package com.pitospies.pitospieswebservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Pie {
    @Id
    private Integer id;
    private String name;
    private Double price;
    private String filename;
}