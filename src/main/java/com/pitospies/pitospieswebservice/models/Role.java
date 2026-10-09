package com.pitospies.pitospieswebservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("role")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    @Id
    int id;
    String name;

}