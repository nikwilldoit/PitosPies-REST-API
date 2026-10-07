package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.*;
import com.pitospies.pitospieswebservice.repositories.*;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceAlreadyExistsException;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping(path="/api", produces={"application/json", "application/xml"})
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private AreaRepository areaRepository;

    @Autowired
    private PieRepository pieRepository;

    @Autowired
    private OrderPagingRepository orderPagingRepository;
    @Autowired
    private Validator validator;


    @GetMapping("/order/{id}")
    public ResponseEntity<Order> handleRequest(@Valid @PathVariable int id) {
        Order order = orderRepository.findOrderById(id);

        if(order==null){
            throw new ResourceNotFoundException("Order not found!");
        }

        //handle area
        order.setArea(areaRepository.findAreaByOrderId(id));

        //handle pies
        order.setOrderItems(orderRepository.findOrderItems(order.getId()));

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> handleRequest2() {
        List<Order> orders = (List<Order>) orderRepository.findAll();

        //handle area
        for(Order order : orders){
            order.setArea(areaRepository.findAreaByOrderId(order.getId()));
        }

        //handle pies
        for(Order order : orders){
            order.setOrderItems(orderRepository.findOrderItems(order.getId()));
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //paging kai sorting sugxronos
    @GetMapping(value="/orders", params = { "page", "size", "sortBy", "sortOrder" })
    public List<Order> findAllBySort(@RequestParam("page") int page,
                                   @RequestParam("size") int size,
                                   @RequestParam("sortBy") String sortBy,
                                   @RequestParam("sortOrder") String sortOrder) {

        List<Order> orders = (List<Order>) orderRepository.findAll();

        //handle area
        for(Order order : orders){
            order.setArea(areaRepository.findAreaByOrderId(order.getId()));
        }

        //handle pies
        for(Order order : orders){
            order.setOrderItems(orderRepository.findOrderItems(order.getId()));
        }

        PageRequest pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
        Page<Order> result = orderPagingRepository.findAll(pageable);
        if (!result.isEmpty())
            return result.getContent();
        else
            return new ArrayList<Order>();
    }

    @PostMapping(value = "/orders", consumes = "application/json")
    public ResponseEntity<Order> handleRequest4(@Valid @RequestBody Order order) {
        order = orderRepository.save(order);

        //handle area
        Area area = areaRepository.findAreaByDescription(order.getArea().getDescription());
        order.setArea(area);
        orderRepository.updateArea(order.getId(), area.getId());

        //handle OrderItems
        for (OrderItem orderItem: order.getOrderItems()) {
            Pie pie = pieRepository.findPieByName(orderItem.getPie());
            orderRepository.addOrderItemToOrder(order.getId(), pie.getId(), orderItem.getQuantity());
        }

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    //olikh enhmerwsh
    @PutMapping(value="/order/{id}", consumes="application/json")
    public ResponseEntity<Order> handleRequest5(@Valid @PathVariable Integer id, @Valid @RequestBody Order updatedOrder) {
        Order order = orderRepository.findOrderById(id);

        if (order==null)
            throw new ResourceNotFoundException("Order not found!");

        // update
        updatedOrder.setId(id);
        // save
        orderRepository.save(updatedOrder);

        //handle area
        Area area = areaRepository.findAreaByDescription(order.getArea().getDescription());
        order.setArea(area);
        orderRepository.updateArea(order.getId(), area.getId());

        //handle OrderItems
        for (OrderItem orderItem: order.getOrderItems()) {
            Pie pie = pieRepository.findPieByName(orderItem.getPie());
            orderRepository.addOrderItemToOrder(order.getId(), pie.getId(), orderItem.getQuantity());
        }

        return new ResponseEntity<>(updatedOrder, HttpStatus.OK);
    }

    //merikh enhmerwsh
    @PatchMapping(value="/order/{id}", consumes="application/json")
    public ResponseEntity<Order> handleRequest6(@PathVariable Integer id, @RequestBody Map<String, Object> updatedOrderData) throws BindException {
        Order order = orderRepository.findOrderById(id);

        if (order==null)
            throw new ResourceNotFoundException("Order not found!");

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

        //handle area
        Area area = null;
        if (updatedOrderData.containsKey("area")) {
            Map<String, String> areaMap = (Map<String, String>) updatedOrderData.get("area");
            String areaName = areaMap.get("description");
            area = areaRepository.findAreaByDescription(areaName);
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
        orderRepository.save(order);
        if(area!=null){
            orderRepository.updateArea(order.getId(), area.getId());
        }
        if (orderItems!=null) {
            orderRepository.deleteOrderItems(order.getId());
            for (OrderItem orderItem: order.getOrderItems()) {
                Pie pie = pieRepository.findPieByName(orderItem.getPie());
                orderRepository.addOrderItemToOrder(order.getId(), pie.getId(), orderItem.getQuantity());
            }
        }
        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @DeleteMapping(value="/order/{id}")
    public ResponseEntity<?>  handleRequest7(@PathVariable int id) {
        Order order = orderRepository.findOrderById(id);

        if (order==null)
            return ResponseEntity.noContent().build();
        else {
            order.setArea(areaRepository.findAreaByOrderId(id));
            order.setOrderItems(orderRepository.findOrderItems(order.getId()));

            //handle OrderItems
            orderRepository.deleteOrderItems(id);
            orderRepository.delete(order);

            return new ResponseEntity<>(order, HttpStatus.OK);
        }
    }
}
