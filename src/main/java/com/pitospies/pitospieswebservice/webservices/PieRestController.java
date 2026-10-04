package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.Award;
import com.pitospies.pitospieswebservice.models.Ingredient;
import com.pitospies.pitospieswebservice.models.Pie;
import com.pitospies.pitospieswebservice.repositories.IngredientRepository;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
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
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping(path="/api", produces="application/json")
public class PieRestController {

    @Autowired
    private PieRepository pieRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private Validator validator;


    @GetMapping("/pie/{id}")
    public ResponseEntity<Pie> handleRequest(@Valid @PathVariable int id) {
        Pie pie = pieRepository.findPieById(id);

        if(pie==null){
            throw new ResourceNotFoundException("Pie not found!");
        }

        pie.setIngredients(pieRepository.findIngredientsOfPie(id));

        return new ResponseEntity<>(pie, HttpStatus.OK);
    }

    @GetMapping("/pies")
    public ResponseEntity<List<Pie>> handleRequest2() {
        List<Pie> pies = pieRepository.findAll();

        for(Pie pie : pies){
            pie.setIngredients(pieRepository.findIngredientsOfPie(pie.getId()));
        }

        return new ResponseEntity<>(pies, HttpStatus.OK);
    }

    @PostMapping(value = "/pies", consumes = "application/json")
    public ResponseEntity<Pie> handleRequest4(@Valid @RequestBody Pie pie) {
        if (pieRepository.existsPieByName((pie.getName())))
            throw new ResourceAlreadyExistsException("This pie already exists (same name)");

        pie = pieRepository.save(pie);

        //handle ingredients
        for (String ingredientName: pie.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
            if (ingredient==null)
                ingredient = ingredientRepository.save(new Ingredient(null, ingredientName));

            pieRepository.addIngredientToPie(pie.getId(), ingredient.getId());
        }

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
    public ResponseEntity<Pie> handleRequest6(@PathVariable Integer id, @RequestBody Map<String, Object> updatedPieData) throws BindException {
        Pie pie = pieRepository.findPieById(id);

        if (pie==null)
            throw new ResourceNotFoundException("Pie not found!");

        // update
        if (updatedPieData.containsKey("name"))
            pie.setName((String) updatedPieData.get("name"));
        if (updatedPieData.containsKey("price"))
            pie.setPrice((Double) updatedPieData.get("price"));
        if (updatedPieData.containsKey("filename"))
            pie.setFilename((String) updatedPieData.get("filename"));
        if (updatedPieData.containsKey("awards")) {
            System.out.println("test");
            List<Map<String, Object>> awardsData = (List<Map<String, Object>>) updatedPieData.get("awards");
            System.out.println(awardsData);
            List<Award> awards = new ArrayList<>();
            for (Map<String, Object> awardData : awardsData) {
                Award award = new Award();
                award.setName((String) awardData.get("name"));
                // Set any other Award properties here
                awards.add(award);
            }
            pie.setAwards(awards);
        }

        //validate the new object
        BindingResult bindingResult = new BeanPropertyBindingResult(pie, "pie");
        validator.validate(pie, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        // save
        pieRepository.save(pie);

        return new ResponseEntity<>(pie, HttpStatus.OK);
    }

    @DeleteMapping(value="/pie/{id}")
    public ResponseEntity<?>  handleRequest7(@PathVariable int id) {
        Pie pie = pieRepository.findPieById(id);

        if (pie==null)
            return ResponseEntity.noContent().build();
        else {
            //handle ingredients
            pie.setIngredients(pieRepository.findIngredientsOfPie(id));

            for (String ingredientName: pie.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
                pieRepository.removeIngredientFromPie(pie.getId(), ingredient.getId());
            }

            pieRepository.delete(pie);
            return new ResponseEntity<>(pie, HttpStatus.OK);
        }
    }
}
