package com.pitospies.pitospieswebservice.services;

import com.pitospies.pitospieswebservice.models.Award;
import com.pitospies.pitospieswebservice.models.Ingredient;
import com.pitospies.pitospieswebservice.models.Pie;
import com.pitospies.pitospieswebservice.repositories.IngredientRepository;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceAlreadyExistsException;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class PieService {

    @Autowired
    private PieRepository pieRepository;
    @Autowired
    private IngredientService ingredientService;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private Validator validator;

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
        if (existsPieByName(pie.getName())) {
            throw new ResourceAlreadyExistsException("This pie already exists (same name)");
        }
        pie = pieRepository.save(pie);
        saveIngredientsOfPie(pie, pie.getIngredients());

        return pie;
    }

    public boolean existsPieByName(String name) {
        Boolean check=pieRepository.existsPieByName(name);
        System.out.println("check: " + check);
        return check;
    }

    public Pie getPieByName(String name) {
        Pie pie = pieRepository.findPieByName(name);

        return pie;
    }

    public void deletePieIngredients(Pie pie) {
        List<String> ingredients = getPieIngredients(pie.getId());
        pie.setIngredients(ingredients);

        for (String ingredientName: pie.getIngredients()) {
            Ingredient ingredient = ingredientService.getIngredientByName(ingredientName);
            pieRepository.removeIngredientFromPie(pie.getId(), ingredient.getId());
        }
    }

    @Caching(evict= {
            @CacheEvict(value = "cache", key = "'pie-' + #id"),
            @CacheEvict(value = "cache", key = "'pies'")
    })
    public Pie deletePie(int id) {
        Pie pie = getPieById(id);

        if (pie==null)
            return null;
        else {
            //handle ingredients
            deletePieIngredients(pie);
        }
        pieRepository.delete(pie);
        return pie;
    }

    @Caching(evict= {
            @CacheEvict(value = "cache", key = "'pie-' + #pie.id"),
            @CacheEvict(value = "cache", key = "'pies'")
    })
    public Pie putPie(Pie pie, Pie updatedPieData){
        //update data
        updatedPieData.setId(pie.getId());
        pieRepository.save(updatedPieData);

        //handle ingredients
        //delete ingredients
        deletePieIngredients(pie);
        //add new ingredients
        saveIngredientsOfPie(pie, updatedPieData.getIngredients());

        return pie;
    }

    @Caching(evict= {
            @CacheEvict(value = "cache", key = "'pie-' + #pie.id"),
            @CacheEvict(value = "cache", key = "'pies'")
    })
    public Pie patchPie(Pie pie, Map<String, Object> updatedPieData) throws BindException {
        // update
        if (updatedPieData.containsKey("name"))
            pie.setName((String) updatedPieData.get("name"));
        if (updatedPieData.containsKey("price"))
            pie.setPrice((Double) updatedPieData.get("price"));
        if (updatedPieData.containsKey("filename"))
            pie.setFilename((String) updatedPieData.get("filename"));
        if (updatedPieData.containsKey("awards")) {
            List<Map<String, Object>> awardsData = (List<Map<String, Object>>) updatedPieData.get("awards");
            List<Award> awards = new ArrayList<>();
            for (Map<String, Object> awardData : awardsData) {
                Award award = new Award();
                award.setName((String) awardData.get("name"));
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

        // handle ingredients
        if (updatedPieData.containsKey("ingredients")) {
            // handle ingredients
            // delete ingredients
            deletePieIngredients(pie);
            // add new ingredients
            saveIngredientsOfPie(pie, (List<String>) updatedPieData.get("ingredients"));
        }

        return pie;
    }
}
