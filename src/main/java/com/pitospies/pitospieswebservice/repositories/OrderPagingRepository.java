package com.pitospies.pitospieswebservice.repositories;

import com.pitospies.pitospieswebservice.models.Order;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderPagingRepository extends PagingAndSortingRepository<Order, Integer> {

}
