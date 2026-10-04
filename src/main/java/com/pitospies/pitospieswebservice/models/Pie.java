package com.pitospies.pitospieswebservice.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @MappedCollection(idColumn = "pie_id", keyColumn = "order")
    private List<Award> awards;

    @Transient //shmainei oti to spring data jdbc den kanei kati automata to diaxeirizomaste emeis
    private List<String> ingredients;
}