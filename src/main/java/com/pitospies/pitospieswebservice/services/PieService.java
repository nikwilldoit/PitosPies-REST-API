package com.pitospies.pitospieswebservice.services;

import com.pitospies.pitospieswebservice.models.Ingredient;
import com.pitospies.pitospieswebservice.models.Pie;
import com.pitospies.pitospieswebservice.repositories.IngredientRepository;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class PieService {

    @Autowired
    private PieRepository pieRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Cacheable(value="my-cache", key="'pie-' + #id", sync=true)
    public Pie getPieById(int id) {
        Pie pie = pieRepository.findPieById(id);

        return pie;
    }

    @CacheEvict(value="my-cache", key="'pie-' + #id")
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
