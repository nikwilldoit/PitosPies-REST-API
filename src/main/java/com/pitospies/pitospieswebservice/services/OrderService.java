package com.pitospies.pitospieswebservice.services;

import com.pitospies.pitospieswebservice.models.*;
import com.pitospies.pitospieswebservice.repositories.AreaRepository;
import com.pitospies.pitospieswebservice.repositories.IngredientRepository;
import com.pitospies.pitospieswebservice.repositories.OrderRepository;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private AreaService areaService;

    @Autowired
    private PieService pieService;

    @Autowired
    private Validator validator;

    private Order getOrderById(int id) {
        Order order = orderRepository.findOrderById(id);

        return order;
    }

    private Area getOrderArea(Order order) {
        return areaRepository.findAreaByOrderId(order.getId());
    }

    private List<OrderItem> getOrderItems(Order order) {
        return orderRepository.findOrderItems(order.getId());
    }

    @Cacheable(value="cache", key="'order-' + #id", unless="#result == null")
    public Order getOrderEntity(int id) {
        Order order = getOrderById(id);
        if (order==null)
            throw new ResourceNotFoundException("Order not found!");

        order.setArea(getOrderArea(order));
        order.setOrderItems(getOrderItems(order));

        return order;
    }

    @Cacheable(value="cache", key="'orders'")
    public List<Order> getAllOrderEntities() {
        List<Order> orders = (List<Order>) orderRepository.findAll();
        System.out.println("test");

        for (Order order: orders) {
            order.setArea(getOrderArea(order));
            order.setOrderItems(getOrderItems(order));
        }

        return orders;
    }

    private void saveOrderItems(Order order, List<OrderItem> orderItems) {
        for (OrderItem orderItem: orderItems) {
            Pie pie = pieService.getPieByName(orderItem.getPie());
            orderRepository.addOrderItemToOrder(order.getId(), pie.getId(), orderItem.getQuantity());
        }
        order.setOrderItems(orderItems);
    }

    private void saveArea(Order order, Area area) {
        area = areaService.getAreaByDescription(area.getDescription());
        order.setArea(area);
        orderRepository.updateArea(order.getId(), area.getId());
    }

    @CacheEvict(value="cache", key="'orders'")
    public Order saveOrder(Order order) {
        order.setStamp(LocalDateTime.now());
        order.setArea(areaService.getAreaByDescription(order.getArea().getDescription()));
        orderRepository.save(order);
        saveOrderItems(order, order.getOrderItems());
        saveArea(order, order.getArea());

        return order;
    }

    public void deleteOrderItems(Order order) {
        orderRepository.deleteOrderItems(order.getId());
    }

    @Caching(evict= {
            @CacheEvict(value = "cache", key = "'order-' + #id"),
            @CacheEvict(value = "cache", key = "'orders'")
    })
    public Order deleteOrder(int id) {
        Order order = getOrderEntity(id);

        if (order==null)
            return null;
        else
            deleteOrderItems(order);

        orderRepository.delete(order);
        return order;
    }

    @Caching(evict= {
            @CacheEvict(value = "cache", key = "'order-' + #order.id"),
            @CacheEvict(value = "cache", key = "'order'")
    })
    public Order putOrder(Order order, Order updatedOrderData) {
        // update data
        updatedOrderData.setId(order.getId());
        updatedOrderData.setStamp(LocalDateTime.now());
        orderRepository.save(updatedOrderData);

        // handle area
        Area area = areaService.getAreaByDescription(updatedOrderData.getArea().getDescription());
        updatedOrderData.setArea(area);
        orderRepository.updateArea(updatedOrderData.getId(), area.getId());

        // handle orderItems
        deleteOrderItems(order);
        saveOrderItems(updatedOrderData, updatedOrderData.getOrderItems());

        return updatedOrderData;
    }



    @Caching(evict= {
            @CacheEvict(value = "cache", key = "'order-' + #order.id"),
            @CacheEvict(value = "cache", key = "'orders'")
    })
    public Order patchOrder(Order order, Map<String, Object> updatedOrderData) throws BindException {
        // update
        if (updatedOrderData.containsKey("fullname"))
            order.setFullname((String) updatedOrderData.get("fullname"));
        if (updatedOrderData.containsKey("address"))
            order.setAddress((String) updatedOrderData.get("address"));
        if (updatedOrderData.containsKey("email"))
            order.setEmail((String) updatedOrderData.get("email"));
        if (updatedOrderData.containsKey("tel"))
            order.setTel((String) updatedOrderData.get("tel"));
        if (updatedOrderData.containsKey("comments"))
            order.setComments((String) updatedOrderData.get("comments"));
        if (updatedOrderData.containsKey("offer"))
            order.setOffer((Boolean) updatedOrderData.get("offer"));
        if (updatedOrderData.containsKey("payment"))
            order.setPayment((String) updatedOrderData.get("payment"));
        if (updatedOrderData.containsKey("stamp"))
            order.setStamp(LocalDateTime.parse((String) updatedOrderData.get("stamp"), DateTimeFormatter.ISO_LOCAL_DATE_TIME));

        // handle area
        Area area = null;
        if (updatedOrderData.containsKey("area")) {
            Map<String, String> areaMap = (Map<String, String>) updatedOrderData.get("area");
            String areaName = areaMap.get("description");
            area = areaService.getAreaByDescription(areaName);
            order.setArea(area);
        }

        // handle OrderItems
        List<OrderItem> orderItems = null;
        if (updatedOrderData.containsKey("orderItems")) {
            orderItems = new ArrayList<>();
            List<Map<String, Object>> newOrderItemsMap = (List<Map<String, Object>>) updatedOrderData.get("orderItems");
            for (Map<String, Object> newOrderItemMap: newOrderItemsMap ) {
                orderItems.add(new OrderItem(
                        (String) newOrderItemMap.get("pie"),
                        (Integer) newOrderItemMap.get("quantity")
                ));
            }
            order.setOrderItems(orderItems);
        }

        //validate the new object
        BindingResult bindingResult = new BeanPropertyBindingResult(order, "order");
        validator.validate(order, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        // save
        order.setStamp(LocalDateTime.now());
        orderRepository.save(order);
        if (area!=null) {
            saveArea(order, area);
        }
        if (orderItems!=null) {
            deleteOrderItems(order);
            saveOrderItems(order, orderItems);
        }

        return order;
    }


}
