package com.pitospies.pitospieswebservice.models;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Table("order")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {
    @Id
    private Integer id;
    private String fullname;
    private String address;
    private String email;
    private String tel;
    private String comments;
    private Integer offer;
    private String payment;
    private LocalDateTime stamp;

//    private Integer areaId;
//    private Integer userId;

//    @MappedCollection(idColumn="order_id")
//    private Set<OrderItem> orderItem;

//    public Order(FormDataOrder formDataOrder, Map<Integer, Integer> order) {
//        this.id = null;
//        this.fullname = formDataOrder.getFullname();
//        this.address = formDataOrder.getAddress();
//        this.email = formDataOrder.getEmail();
//        this.tel = formDataOrder.getTel();
//        this.comments = formDataOrder.getComments();
//
//        this.offer = formDataOrder.isOffer()?1:0;
//        this.payment = formDataOrder.getPayment();
//        this.stamp = formDataOrder.getStamp();
//
//        this.areaId = formDataOrder.getAreaId();
//
//        this.orderItem = new HashSet<>();
//        for (var pieId: order.keySet()) {
//            orderItem.add(new OrderItem(
//                    null,
//                    pieId,
//                    order.get(pieId)
//            ));
//        }
//    }
}