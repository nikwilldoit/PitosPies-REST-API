package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.Area;
import com.pitospies.pitospieswebservice.repositories.AreaRepository;
import com.pitospies.pitospieswebservice.services.AreaService;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceAlreadyExistsException;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping(path="/api", produces={"application/json", "application/xml"})
public class AreaRestController {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private Validator validator;

    @Autowired
    ObjectMapper objectMapper; // jackson. dependency included in spring-web

    @Autowired
    private AreaService areaService;

    @GetMapping("/area/{id}")
    public ResponseEntity<Area> handleRequest(@Valid @PathVariable int id) {
        Area area = areaService.getAreaById(id);

        if(area==null){
            throw new ResourceNotFoundException("Area not found!");
        }
        return new ResponseEntity<>(area, HttpStatus.OK);
    }

    @GetMapping("/areas")
    public ResponseEntity<List<Area>> handleRequest2() {
        List<Area> areas = areaRepository.findAll();
        return new ResponseEntity<>(areas, HttpStatus.OK);
    }

    //it covers 2 cases: save multiple and single object
    @PostMapping(value = "/areas", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Area> handleRequest4(@Valid @RequestBody Area area) {
        if (areaRepository.existsAreaByDescription((area.getDescription())))
            throw new ResourceAlreadyExistsException("This area already exists (same name)");

        area = areaRepository.save(area);
        return new ResponseEntity<>(area, HttpStatus.CREATED);
    }

    //olikh enhmerwsh
    @PutMapping(value="/area/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Area> handleRequest5(@Valid @PathVariable int id, @RequestBody Area updatedArea) {

        Area area = areaRepository.findAreaById(id);

        if(area==null){
            throw new ResourceNotFoundException("Area not found!");
        }

        //kanei update mono oti uparxei ta alla ta afhnei KENO
        updatedArea.setId(id);

        //save
        areaRepository.save(updatedArea);

        return new ResponseEntity<>(updatedArea, HttpStatus.OK);
    }

    //merikh enhmerwsh
    @PatchMapping(value="/area/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Area> handleRequest6(@PathVariable int id, @RequestBody Map<String, Object> updatedAreaData) throws BindException {

        Area area = areaRepository.findAreaById(id);
        if (area==null)
            throw new ResourceNotFoundException("Area not found!");

        //kanei update mono oti uparxei ta alla ta afhnei OPWS EINAI
        if(updatedAreaData.containsKey("description")){
            area.setDescription((String) updatedAreaData.get("description"));
        }

        //validate the new object
        BindingResult bindingResult = new BeanPropertyBindingResult(area, "area");
        validator.validate(area, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        //save
        areaRepository.save(area);

        return new ResponseEntity<>(area, HttpStatus.OK);
    }

    @DeleteMapping(value="/area/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> handleRequest7(@PathVariable int id) {
        Area area = areaRepository.findAreaById(id);

        if (area==null)
            return ResponseEntity.noContent().build();
        else {
            //delete
            areaRepository.delete(area);
            System.out.println(area);
            return new ResponseEntity<>(area, HttpStatus.OK);
        }
    }
}
