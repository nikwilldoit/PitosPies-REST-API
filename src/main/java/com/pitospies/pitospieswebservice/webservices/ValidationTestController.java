package com.pitospies.pitospieswebservice.webservices;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/api", produces="application/json")
@Validated
public class ValidationTestController {

    @GetMapping("/valid/{id}")
    public ResponseEntity<Map<String, Object>> handleRequest(@Valid @Min(value=5, message= "Error: Id should be at least 5") @PathVariable Integer id){
        Map<String, Object> map = new HashMap<>();
        map.put("id", id);

        return new ResponseEntity<>(map, HttpStatus.OK);
    }

}
