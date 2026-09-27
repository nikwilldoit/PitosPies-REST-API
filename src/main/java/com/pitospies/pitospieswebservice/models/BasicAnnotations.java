package com.pitospies.pitospieswebservice.models;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BasicAnnotations {
    @NotNull(message="Name can't be null")
    @NotEmpty(message="Name can't be empty")
    private String fullname;

    @NotNull(message="Must set an age")
    @Min(value=18, message="Age must be >= 18")
    @Max(value=85, message="Age must be <= 85")
    private Integer age;

    @NotEmpty(message = "Pick at least one destination")
    private List<String> destination;
}