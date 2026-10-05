package com.pitospies.pitospieswebservice.repositories;

import com.pitospies.pitospieswebservice.models.Pie;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PiePagingRepository extends PagingAndSortingRepository<Pie, Integer> {

}
