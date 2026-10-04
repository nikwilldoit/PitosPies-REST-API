package com.pitospies.pitospieswebservice.repositories;

import com.pitospies.pitospieswebservice.models.Pie;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PieRepository extends ListCrudRepository<Pie, Integer> {
    Pie findPieById(int id);
    Boolean existsPieByName(String name);


    @Query("SELECT ingredient.name AS name " +
            "FROM ingredient " +
            "    JOIN pie_ingredient ON ingredient.id = pie_ingredient.ingredient_id " +
            "    JOIN pie ON pie_ingredient.pie_id = pie.id " +
            "WHERE pie.id=:pie_id")

    List<String> findIngredientsOfPie(@Param("pie_id") int id);
}
