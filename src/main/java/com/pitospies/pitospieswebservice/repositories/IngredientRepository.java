package com.pitospies.pitospieswebservice.repositories;

import com.pitospies.pitospieswebservice.models.Ingredient;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IngredientRepository extends ListCrudRepository<Ingredient, Integer> {
    Ingredient findIngredientById(int id);
    Ingredient findIngredientByName(String name);
}
