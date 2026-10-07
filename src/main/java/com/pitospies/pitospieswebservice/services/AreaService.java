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

    //prosthiki condition gia ta periexomena pou epistefei h cache: condition = "#id>2" an thelw na swzei mono id's>2
    @Cacheable(value="area-cache", key="'area-' + #id", sync=true) //sync=true ena ena thread epejergazetai cache
    public Area getAreaById(int id) {

        Area area = areaRepository.findAreaById(id);

        return area;
    }

}
