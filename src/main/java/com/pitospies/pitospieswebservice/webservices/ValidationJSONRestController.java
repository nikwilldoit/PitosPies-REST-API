package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.Person;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(path="/api", produces="application/json")
@Validated
public class ValidationJSONRestController {

    @GetMapping(value ="/person", consumes = "application/json")
    public ResponseEntity<Person> handleRequest(@Valid @RequestBody Person person){
        return new ResponseEntity<>(person, HttpStatus.CREATED);
    }

}
