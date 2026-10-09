package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.*;
import com.pitospies.pitospieswebservice.repositories.*;
import com.pitospies.pitospieswebservice.services.OrderService;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.Validator;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@Validated
@RequestMapping(path="/api", produces={"application/json", "application/xml"})
public class OrderRestController {

    @Autowired
    private OrderService orderService;

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
        Order order = orderService.getOrderEntity(id);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> handleRequest2() {
        List<Order> orders = orderService.getAllOrderEntities();

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    //paging kai sorting sugxronos
    @GetMapping(value="/orders", params = { "page", "size", "sortBy", "sortOrder" })
    public List<Order> findAllBySort(@RequestParam("page") int page,
                                   @RequestParam("size") int size,
                                   @RequestParam("sortBy") String sortBy,
                                   @RequestParam("sortOrder") String sortOrder) {

        PageRequest pageable = PageRequest.of(page, size, Sort.Direction.fromString(sortOrder), sortBy);
        Page<Order> result = orderPagingRepository.findAll(pageable);

        for (Order order: result) {
            order.setArea(areaRepository.findAreaByOrderId(order.getId()));
            order.setOrderItems(orderRepository.findOrderItems(order.getId()));
        }

        if (!result.isEmpty())
            return result.getContent();
        else
            return new ArrayList<Order>();
    }

    @PostMapping(value = "/orders", consumes = "application/json")
    public ResponseEntity<Order> handleRequest4(@Valid @RequestBody Order order) {
        order = orderService.saveOrder(order);

        return new ResponseEntity<>(order, HttpStatus.CREATED);
    }

    //olikh enhmerwsh
    @PutMapping(value="/order/{id}", consumes="application/json")
    public ResponseEntity<Order> handleRequest5(@Valid @PathVariable Integer id, @Valid @RequestBody Order updatedOrder) {
        Order order = orderService.getOrderEntity(id);

        if (order==null)
            throw new ResourceNotFoundException("Order not found!");

        order = orderService.putOrder(order, updatedOrder);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    //merikh enhmerwsh
    @PatchMapping(value="/order/{id}", consumes="application/json")
    public ResponseEntity<Order> handleRequest6(@PathVariable Integer id, @RequestBody Map<String, Object> updatedOrderData) throws BindException {
        Order order = orderService.getOrderEntity(id);

        if (order==null)
            throw new ResourceNotFoundException("Order not found!");

        order = orderService.patchOrder(order, updatedOrderData);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @DeleteMapping(value="/order/{id}")
    public ResponseEntity<?>  handleRequest7(@PathVariable int id) {
        Order order = orderService.deleteOrder(id);

        if (order==null)
            return ResponseEntity.noContent().build();
        else {
            return new ResponseEntity<>(order, HttpStatus.OK);
        }
    }
}
