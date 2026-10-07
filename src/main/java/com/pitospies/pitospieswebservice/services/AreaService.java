package com.pitospies.pitospieswebservice.services;

import com.pitospies.pitospieswebservice.models.Area;
import com.pitospies.pitospieswebservice.repositories.AreaRepository;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.util.List;
import java.util.Map;

@Service
public class AreaService {

    @Autowired
    AreaRepository areaRepository;

    @Autowired
    private Validator validator;

    @Cacheable(value="cache", key="'area-' + #id", unless="#result == null")
    public Area getAreaEntity(int id) {
        Area area = areaRepository.findAreaById(id);
        if (area==null)
            throw new ResourceNotFoundException("Area not found!");

        return area;
    }

    @Cacheable(value="cache", key="'areas'")
    public List<Area> getAllAreaEntities() {
        List<Area> areas = areaRepository.findAll();

        return areas;
    }


    public boolean existsAreaByDescription(String name) {
        Boolean check=areaRepository.existsAreaByDescription(name);
        return check;
    }

    public Area getAreaByDescription(String name) {
        Area area = areaRepository.findAreaByDescription(name);
        return area;
    }

    @CacheEvict(value="cache", key="'areas'")
    public Area saveArea(Area area) {
        area = areaRepository.save(area);

        return area;
    }

    @Caching(evict= {
            @CacheEvict(value = "cache", key = "'area-' + #id"),
            @CacheEvict(value = "cache", key = "'areas'")
    })
    public Area deleteArea(int id) {
        Area area = getAreaEntity(id);

        if (area==null)
            return null;

        areaRepository.delete(area);
        return area;
    }

    @Caching(evict= {
            @CacheEvict(value = "cache", key = "'area-' + #area.id"),
            @CacheEvict(value = "cache", key = "'areas'")
    })
    public Area putArea(Area area, Area updatedAreaData) {
        area.setDescription(updatedAreaData.getDescription());
        areaRepository.save(area);

        return area;
    }

    @Caching(evict= {
            @CacheEvict(value = "cache", key = "'area-' + #area.id"),
            @CacheEvict(value = "cache", key = "'areas'")
    })
    public Area patchArea(Area area, Map<String, Object> updatedAreaData) throws BindException {
        // update
        if (updatedAreaData.containsKey("description"))
            area.setDescription((String) updatedAreaData.get("description"));

        //validate the new object
        BindingResult bindingResult = new BeanPropertyBindingResult(area, "area");
        validator.validate(area, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        // save
        areaRepository.save(area);

        return area;
    }
}