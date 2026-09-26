package com.pitospies.pitospieswebservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;

@Table("pie")
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