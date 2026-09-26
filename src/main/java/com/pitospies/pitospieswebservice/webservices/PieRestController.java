package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.Pie;
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
public class PieRestController {

    @Autowired
    private PieRepository pieRepository;

    @Autowired
    ObjectMapper objectMapper; // jackson. dependency included in spring-web

    @GetMapping("/pie/{id}")
    public Pie handleRequest(@PathVariable int id) {
        Pie pie = pieRepository.findPieById(id);
        return pie;
    }

    @GetMapping("/pies")
    public List<Pie> handleRequest2() {
        List<Pie> pies = pieRepository.findAll();
        return pies;
    }

    //it covers 2 cases: save multiple and single object
    @PostMapping(value = "/pies", consumes = "application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Object handleRequest4(@RequestBody Object object) {
        if (object instanceof Map) {
            Pie pie = objectMapper.convertValue(object, Pie.class);
            pie = pieRepository.save(pie);
            return pie;
        } else if (object instanceof List) {
            List<Pie> pies = objectMapper.convertValue(object, new TypeReference<List<Pie>>() {
            });
            pies = pieRepository.saveAll(pies);
            return pies;
        }
        return object;
    }

    //olikh enhmerwsh
    @PutMapping(value="/pie/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public Pie handleRequest5(@PathVariable int id, @RequestBody Pie updatedPie) {

        //kanei update mono oti uparxei ta alla ta afhnei KENO
        updatedPie.setId(id);

        //save
        pieRepository.save(updatedPie);

        return updatedPie;
    }

    //merikh enhmerwsh
    @PatchMapping(value="/pie/{id}", consumes="application/json")
    @ResponseStatus(HttpStatus.OK)
    public Pie handleRequest6(@PathVariable int id, @RequestBody Map<String, Object> updatedPieData) {

        Pie pie = pieRepository.findPieById(id);

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

        return pie;
    }

    @DeleteMapping(value="/pie/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void handleRequest7(@PathVariable int id) {
        Pie pie = pieRepository.findPieById(id);

        //delete
        pieRepository.delete(pie);
    }
}
