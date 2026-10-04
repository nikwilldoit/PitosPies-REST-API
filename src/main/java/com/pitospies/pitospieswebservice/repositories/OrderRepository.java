package com.pitospies.pitospieswebservice.repositories;

import com.pitospies.pitospieswebservice.models.Order;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {

    @Query("SELECT * FROM `order` o WHERE o.user_id = :user_id ORDER BY o.stamp DESC LIMIT 5 ")
    List<Order> findTopFiveUserOrderIds(@Param("user_id") int user_id);

//    @Query("SELECT * FROM order_item WHERE order_id=:order_id AND quantity>0 ")
//    List<OrderItem> orderItems(@Param("order_id") int order_id);

    Order findOrderById(int id);
}