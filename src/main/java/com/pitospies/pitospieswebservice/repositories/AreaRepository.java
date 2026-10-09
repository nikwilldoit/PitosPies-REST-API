package com.pitospies.pitospieswebservice.repositories;

import com.pitospies.pitospieswebservice.models.Area;
import com.pitospies.pitospieswebservice.models.Pie;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AreaRepository extends ListCrudRepository<Area, Integer> {

    Area findAreaById(int id);
    Boolean existsAreaByDescription(String description);
    Area findAreaByDescription(String description);

    @Query("SELECT a.* FROM area a JOIN `order` o ON a.id = o.area_id WHERE o.id = :orderId")
    Area findAreaByOrderId(int id);
}
