package com.pitospies.pitospieswebservice.services;

import com.pitospies.pitospieswebservice.models.Area;
import com.pitospies.pitospieswebservice.repositories.AreaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

@Service
public class AreaService {

    @Autowired
    private AreaRepository areaRepository;

    @Cacheable(value="area-cache", key="'area-' + #id", sync=true) //sync=true ena ena thread epejergazetai cache
    public Area getAreaById(int id) {

        Area area = areaRepository.findAreaById(id);

        return area;
    }

}
