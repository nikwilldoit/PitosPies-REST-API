package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.Area;
import com.pitospies.pitospieswebservice.models.Pie;
import com.pitospies.pitospieswebservice.repositories.AreaRepository;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path="/api", produces="application/json")
public class AreaRestController {

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    ObjectMapper objectMapper; // jackson. dependency included in spring-web

    @GetMapping("/area/{id}")
    public Area handleRequest(@PathVariable int id) {
        Area area = areaRepository.findAreaById(id);
        return area;
    }

    @GetMapping("/areas")
    public List<Area> handleRequest2() {
        List<Area> areas = areaRepository.findAll();
        return areas;
    }

    //it covers 2 cases: save multiple and single object
    @PostMapping(value = "/areas", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Object handleRequest4(@RequestBody Object object) {
        if (object instanceof Map) {
            Area area = objectMapper.convertValue(object, Area.class);
            area = areaRepository.save(area);
            return area;
        } else if (object instanceof List) {
            List<Area> areas = objectMapper.convertValue(object, new TypeReference<List<Area>>() {
            });
            areas = areaRepository.saveAll(areas);
            return areas;
        }
        return object;
    }

    //olikh enhmerwsh
    @PutMapping(value="/area/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public Area handleRequest5(@PathVariable int id, @RequestBody Area updatedArea) {

        //kanei update mono oti uparxei ta alla ta afhnei KENO
        updatedArea.setId(id);

        //save
        areaRepository.save(updatedArea);

        return updatedArea;
    }

    //merikh enhmerwsh
    @PatchMapping(value="/area/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public Area handleRequest6(@PathVariable int id, @RequestBody Map<String, Object> updatedPieData) {

        Area area = areaRepository.findAreaById(id);

        //kanei update mono oti uparxei ta alla ta afhnei OPWS EINAI
        if(updatedPieData.containsKey("description")){
            area.setDescription((String) updatedPieData.get("description"));
        }

        //save
        areaRepository.save(area);

        return area;
    }

    @DeleteMapping(value="/area/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleRequest7(@PathVariable int id) {
        Area area = areaRepository.findAreaById(id);

        //delete
        areaRepository.delete(area);
    }

}
