package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.BasicAnnotations;
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
public class BasicAnnotationsRestController {

    @PostMapping("/basic")
    public ResponseEntity<BasicAnnotations> handleRequest(
            @Valid @RequestBody BasicAnnotations jsonObject) {

        return new ResponseEntity<>(jsonObject, HttpStatus.OK);
    }

}