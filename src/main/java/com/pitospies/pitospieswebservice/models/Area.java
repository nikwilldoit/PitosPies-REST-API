package com.pitospies.pitospieswebservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("area")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Area {

    @Id
    Integer id;
    String description;

    @Override
    public String toString() {
        return description;
    }
}