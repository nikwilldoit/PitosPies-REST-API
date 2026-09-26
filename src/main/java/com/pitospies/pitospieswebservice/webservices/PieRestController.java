package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.Pie;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path="/api", produces="application/json")
public class PieRestController {

    @Autowired
    private PieRepository pieRepository;

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

    @PostMapping(value="/pies", consumes="application/json")
    @ResponseStatus(HttpStatus.CREATED)
    public Pie handleRequest3(@RequestBody Pie pie) {
        pieRepository.save(pie);
        return pie;
    }
}
