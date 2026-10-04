package com.pitospies.pitospieswebservice.models;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("award")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Award {
    @Id
    Integer id;
    @NotNull(message = "Award name can't be empty!")
    String name;

    @Override
    public String toString() {
        return name;
    }
}