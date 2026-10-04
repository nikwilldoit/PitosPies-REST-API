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
    private int id;
    @NotBlank(message = "Το όνομα δεν πρέπει να είναι κενό")
    private String name;
    @Min(value = 1, message = "Η τιμή πρέπει να είναι τουλάχιστον 1")
    @Max(value = 20, message = "Η τιμή πρέπει να είναι το πολύ 20")
    private double price;
    @NotBlank(message = "Το όνομα αρχείου δεν πρέπει να είναι κενό")
    private String filename;
}