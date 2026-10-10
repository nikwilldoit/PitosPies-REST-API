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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
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
        Area area = areaService.getAreaEntity(id);

        return new ResponseEntity<>(area, HttpStatus.OK);
    }

    @GetMapping("/areas")
    public ResponseEntity<List<Area>> handleRequest2() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        System.out.println("Name: " + authentication.getName());
        System.out.println("credentials: " + authentication.getCredentials());
        System.out.println("authorities: " + authentication.getAuthorities());
        System.out.println("principal: " + authentication.getPrincipal());
        System.out.println("details: " + authentication.getDetails());

        List<Area> areas = areaService.getAllAreaEntities();
        return new ResponseEntity<>(areas, HttpStatus.OK);
    }

    //it covers 2 cases: save multiple and single object
    @PostMapping(value = "/areas", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<Area> handleRequest4(@Valid @RequestBody Area area) {
        if (areaService.existsAreaByDescription((area.getDescription())))
            throw new ResourceAlreadyExistsException("This area already exists (same name)");

        area = areaService.saveArea(area);
        return new ResponseEntity<>(area, HttpStatus.CREATED);
    }

    //olikh enhmerwsh
    @PutMapping(value="/area/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Area> handleRequest5(@Valid @PathVariable int id, @RequestBody Area updatedArea) {

        Area area = areaService.getAreaEntity(id);

        if(area==null){
            throw new ResourceNotFoundException("Area not found!");
        }

        area = areaService.putArea(area, updatedArea);
        return new ResponseEntity<>(area, HttpStatus.OK);
    }

    //merikh enhmerwsh
    @PatchMapping(value="/area/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<Area> handleRequest6(@PathVariable int id, @RequestBody Map<String, Object> updatedAreaData) throws BindException {
        Area area = areaService.getAreaEntity(id);

        if (area==null)
            throw new ResourceNotFoundException("Area not found!");

        area = areaService.patchArea(area, updatedAreaData);

        return new ResponseEntity<>(area, HttpStatus.OK);
    }

    @DeleteMapping(value="/area/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<?> handleRequest7(@PathVariable int id) {
        Area area = areaService.deleteArea(id);
        if (area==null)
            return ResponseEntity.noContent().build();
        else
            return new ResponseEntity<>(area, HttpStatus.OK);
    }
}
