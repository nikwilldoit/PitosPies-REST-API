package com.pitospies.pitospieswebservice.repositories;

import com.pitospies.pitospieswebservice.models.Order;
import com.pitospies.pitospieswebservice.models.OrderItem;
import org.springframework.data.domain.Sort;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {

    Order findOrderById(int id);


    @Query("SELECT * FROM `order` o WHERE o.user_id = :user_id ORDER BY o.stamp DESC LIMIT 5 ")
    List<Order> findTopFiveUserOrderIds(@Param("user_id") int user_id);

    @Modifying
    @Query("UPDATE `order` SET area_id=:area_id WHERE id=:order_id")
    void updateArea(int order_id, int area_id);

    @Query("SELECT p.`name` as pie, oi.quantity as quantity\n" +
            "FROM `order`o\n" +
            "\tJOIN order_item oi ON o.id = oi.order_id\n" +
            "\tJOIN pie p ON oi.pie_id = p.id\n" +
            "WHERE o.id = :order_id ")
    List<OrderItem> findOrderItems(@Param("order_id") int order_id);

    @Modifying
    @Query("INSERT INTO order_item (order_id, pie_id, quantity) VALUES (:order_id, :pie_id, :quantity)")
    void addOrderItemToOrder(int order_id, int pie_id, int quantity);

    @Modifying
    @Query("DELETE FROM order_item WHERE order_id=:order_id")
    void deleteOrderItems(int order_id);
}