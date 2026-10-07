package com.pitospies.pitospieswebservice.services;

import com.pitospies.pitospieswebservice.models.Ingredient;
import com.pitospies.pitospieswebservice.repositories.IngredientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class IngredientService {

    @Autowired
    private IngredientRepository ingredientRepository;

    @Cacheable(value="cache", key="'ingredient-' + #name", unless="#result == null")
    public Ingredient getIngredientByName(String name) {
        return ingredientRepository.findIngredientByName(name);
    }
}
