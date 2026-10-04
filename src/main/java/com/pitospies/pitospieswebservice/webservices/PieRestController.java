package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.Pie;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceAlreadyExistsException;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping(path="/api", produces="application/json")
public class PieRestController {

    @Autowired
    private PieRepository pieRepository;

    @Autowired
    ObjectMapper objectMapper; // jackson. dependency included in spring-web

    @GetMapping("/pie/{id}")
    public ResponseEntity<Pie> handleRequest(@Valid @PathVariable int id) {
        Pie pie = pieRepository.findPieById(id);

        if(pie==null){
            throw new ResourceNotFoundException("Pie not found!");
        }
        return new ResponseEntity<>(pie, HttpStatus.OK);
    }

    @GetMapping("/pies")
    public ResponseEntity<List<Pie>> handleRequest2() {
        List<Pie> pies = pieRepository.findAll();
        return new ResponseEntity<>(pies, HttpStatus.OK);
    }

    @PostMapping(value = "/pies", consumes = "application/json")
    public ResponseEntity<Pie> handleRequest4(@Valid @RequestBody Pie pie) {
        if (pieRepository.existsPieByName((pie.getName())))
            throw new ResourceAlreadyExistsException("This pie already exists (same name)");

        pie = pieRepository.save(pie);
        return new ResponseEntity<>(pie, HttpStatus.CREATED);
    }

    //olikh enhmerwsh
    @PutMapping(value="/pie/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Pie> handleRequest5(@Valid @PathVariable Integer id, @Valid @RequestBody Pie updatedPie) {
        Pie pie = pieRepository.findPieById(id);

        if(pie==null){
            throw new ResourceNotFoundException("Pie not found!");
        }

        //kanei update mono oti uparxei ta alla ta afhnei KENO
        updatedPie.setId(id);

        //save
        pieRepository.save(updatedPie);

        return new ResponseEntity<>(updatedPie, HttpStatus.OK);
    }

    //merikh enhmerwsh
    @PatchMapping(value="/pie/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Pie> handleRequest6(@PathVariable int id, @RequestBody Map<String, Object> updatedPieData) {

        Pie pie = pieRepository.findPieById(id);
        if(pie==null){
            throw new ResourceNotFoundException("Pie not found!");
        }

        //kanei update mono oti uparxei ta alla ta afhnei OPWS EINAI
        if(updatedPieData.containsKey("name")){
            pie.setName((String) updatedPieData.get("name"));
        }
        if(updatedPieData.containsKey("price")){
            pie.setPrice((Double) updatedPieData.get("price"));
        }
        if(updatedPieData.containsKey("filename")){
            pie.setFilename((String) updatedPieData.get("filename"));
        }

        //save
        pieRepository.save(pie);

        return new ResponseEntity<>(pie, HttpStatus.OK);
    }

    @DeleteMapping(value="/pie/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleRequest7(@PathVariable int id) {
        Pie pie = pieRepository.findPieById(id);

        //delete
        pieRepository.delete(pie);
    }
}
