package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.Award;
import com.pitospies.pitospieswebservice.models.Ingredient;
import com.pitospies.pitospieswebservice.models.Pie;
import com.pitospies.pitospieswebservice.repositories.IngredientRepository;
import com.pitospies.pitospieswebservice.repositories.PiePagingRepository;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
import com.pitospies.pitospieswebservice.services.PieService;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceAlreadyExistsException;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping(path="/api", produces={"application/json", "application/xml"})
public class PieRestController {

    @Autowired
    private PieRepository pieRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private PiePagingRepository piePagingRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private PieService pieService;


    @GetMapping("/pie/{id}")
    public ResponseEntity<Pie> handleRequest(@Valid @PathVariable int id) {
        Pie pie = pieService.getPieEntity(id);

        return new ResponseEntity<>(pie, HttpStatus.OK);
    }

    @GetMapping("/pies")
    public ResponseEntity<List<Pie>> handleRequest2() {
        List<Pie> pies = pieService.getAllPieEntities();

        return new ResponseEntity<>(pies, HttpStatus.OK);
    }

    @GetMapping(value="/pies", params = { "page", "size" })
    public List<Pie> findAllByPage(@RequestParam("page") int page,
                                   @RequestParam("size") int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<Pie> result = piePagingRepository.findAll(pageable);
        if (!result.isEmpty())
            return result.getContent();
        else
            return new ArrayList<Pie>();
    }

    @GetMapping(value="/pies", params = { "sortBy", "sortOrder" })
    public List<Pie> findAllBySort(@RequestParam("sortBy") final String sortBy,
                                   @RequestParam("sortOrder") final String sortOrder) {
        //opou Sort.Direction.fromString(sortOrder) {sortOrder = "asc" or "desc}
        //mporoume kai kateuthian Sort.Direction.ASC || Sort.Direction.DESC
        //to sortBy einai me vash ti theloume na kanoume sort p.x {id, name, ktlp}
        Sort sort = Sort.by(Sort.Direction.fromString(sortOrder), sortBy);
        return (List<Pie>) piePagingRepository.findAll(sort);
    }

    //tajinomhsh epi pollwn pediwn {"sortByField1", "sortByField2"}
    @GetMapping(value="/pies", params = { "sortByField1", "sortByField2" })
    public List<Pie> findAllBySort2(@RequestParam("sortByField1") final String sortByField1,
                                    @RequestParam("sortByField2") final String sortByField2) {
        Sort sort = Sort.by(Sort.Order.asc(sortByField1), Sort.Order.asc(sortByField2));
        return (List<Pie>) piePagingRepository.findAll(sort);
    }

    //paging kai sorting sugxronos
    @GetMapping(value="/pies", params = { "page", "size", "sortBy", "sortOrder" })
    public List<Pie> findAllBySort(@RequestParam("page") int page,
                                   @RequestParam("size") int size,
                                   @RequestParam("sortBy") String sortBy,
                                   @RequestParam("sortOrder") String sortOrder) {
        PageRequest pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
        Page<Pie> result = piePagingRepository.findAll(pageable);
        if (!result.isEmpty())
            return result.getContent();
        else
            return new ArrayList<Pie>();
    }

    @PostMapping(value = "/pies", consumes = "application/json")
    public ResponseEntity<Pie> handleRequest4(@Valid @RequestBody Pie pie) {
        if (pieService.existsPieByName(pie.getName()))
            throw new ResourceAlreadyExistsException("This pie already exists (same name)");

        pie = pieService.savePie(pie);

        return new ResponseEntity<>(pie, HttpStatus.CREATED);
    }

    //olikh enhmerwsh
    @PutMapping(value="/pie/{id}", consumes="application/json")
    public ResponseEntity<Pie> handleRequest5(@Valid @PathVariable Integer id, @Valid @RequestBody Pie updatedPie) {
        Pie pie = pieService.getPieEntity(id);

        if (pie==null)
            throw new ResourceNotFoundException("Pie not found!");

        pie = pieService.putPie(pie, updatedPie);

        return new ResponseEntity<>(pie, HttpStatus.OK);
    }

    //merikh enhmerwsh
    @PatchMapping(value="/pie/{id}", consumes="application/json")
    public ResponseEntity<Pie> handleRequest6(@PathVariable Integer id, @RequestBody Map<String, Object> updatedPieData) throws BindException {
        Pie pie = pieService.getPieEntity(id);

        if (pie==null)
            throw new ResourceNotFoundException("Pie not found!");

        pie = pieService.patchPie(pie, updatedPieData);

        return new ResponseEntity<>(pie, HttpStatus.OK);
    }

    @DeleteMapping(value="/pie/{id}")
    public ResponseEntity<?>  handleRequest7(@PathVariable int id) {
        Pie pie = pieService.deletePie(id);

        if (pie==null)
            return ResponseEntity.noContent().build();
        else {
            return new ResponseEntity<>(pie, HttpStatus.OK);
        }
    }
}
