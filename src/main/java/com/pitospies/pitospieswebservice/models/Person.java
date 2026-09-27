package com.pitospies.pitospieswebservice.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Person {

    @NotEmpty
    private String firstname;
    @NotEmpty
    private String lastname;
    @Min(1)
    @Max(5)
    @NotNull
    private Integer grade;

}
