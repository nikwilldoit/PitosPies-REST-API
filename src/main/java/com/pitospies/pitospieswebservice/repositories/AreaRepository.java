package com.pitospies.pitospieswebservice.repositories;

import com.pitospies.pitospieswebservice.models.Area;
import com.pitospies.pitospieswebservice.models.Pie;
import org.springframework.data.repository.ListCrudRepository;

public interface AreaRepository extends ListCrudRepository<Area, Integer> {

    Area findAreaById(int id);
}
