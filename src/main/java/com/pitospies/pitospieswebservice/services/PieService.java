package com.pitospies.pitospieswebservice.services;

import com.pitospies.pitospieswebservice.models.Ingredient;
import com.pitospies.pitospieswebservice.models.Pie;
import com.pitospies.pitospieswebservice.repositories.IngredientRepository;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceAlreadyExistsException;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PieService {

    @Autowired
    private PieRepository pieRepository;
    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Cacheable(value="pie-cache", key="'pie-' + #id", sync=true) //sync=true ena ena thread epejergazetai cache
    public Pie getPieById(int id) {
        Pie pie = pieRepository.findPieById(id);

        return pie;
    }

    @CacheEvict(value="pie-cache", key="'pie-' + #id")
    public List<String> getPieIngredients(int id){

        return pieRepository.findIngredientsOfPie(id);
    }

    @Cacheable(value="cache", key="'pie-' + #id", unless="#result == null")
    public Pie getPieEntity(int id) {
        Pie pie = getPieById(id);
        if (pie==null)
            throw new ResourceNotFoundException("Pie not found!");

        pie.setIngredients(getPieIngredients(id));

        return pie;
    }

    @CacheEvict(value="pie-cache", key="'pies'")
    public List<Pie> getAllPieEntities(){
        List<Pie> pies = pieRepository.findAll();

        for(Pie pie : pies){
            pie.setIngredients(getPieIngredients(pie.getId()));
        }
        return pies;
    }

    private void saveIngredientsOfPie(Pie pie, List<String> ingredients) {
        for (String ingredientName : ingredients) {
            Ingredient ingredient = ingredientService.getIngredientByName(ingredientName);
            if (ingredient == null)
                ingredient = ingredientRepository.save(new Ingredient(null, ingredientName));

            pieRepository.addIngredientToPie(pie.getId(), ingredient.getId());
        }
        pie.setIngredients(ingredients);
    }

    @CacheEvict(value="pie-cache", key="'pies'")
    public Pie savePie(Pie pie){
        if (existsPieByName(pie.getName()))
            throw new ResourceAlreadyExistsException("This pie already exists (same name)");

        pie = pieRepository.save(pie);
        saveIngredientsOfPie(pie, pie.getIngredients());

        return pie;
    }

    public boolean existsPieByName(String name) {
        Boolean check=pieRepository.existsPieByName(name);
        System.out.println("check: " + check);
        return check;
    }

    @CacheEvict(value="pie-cache", key="'pie-' + #id")
    public Pie updatePie(int id, Pie updatedPie) {
        Pie pie = pieRepository.findPieById(id);

        if (pie==null)
            throw new ResourceNotFoundException("Pie not found!");

        // update
        updatedPie.setId(id);
        // save
        pieRepository.save(updatedPie);


        // handle ingredients
        // delete ingredients
        pie.setIngredients(pieRepository.findIngredientsOfPie(id));

        for (String ingredientName: pie.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
            pieRepository.removeIngredientFromPie(pie.getId(), ingredient.getId());
        }
        // add new ingredients
        for (String ingredientName: updatedPie.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
            if (ingredient==null)
                ingredient = ingredientRepository.save(new Ingredient(null, ingredientName));

            pieRepository.addIngredientToPie(pie.getId(), ingredient.getId());
        }

        return updatedPie;
    }
}
