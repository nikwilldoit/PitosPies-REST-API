package com.pitospies.pitospieswebservice.models;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("area")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Area {
    @Id
    private int id;
    @NotBlank(message = "Area description can't be empty")
    private String description;
}