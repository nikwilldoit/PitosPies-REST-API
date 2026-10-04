package com.pitospies.pitospieswebservice.webservices;

import com.pitospies.pitospieswebservice.models.*;
import com.pitospies.pitospieswebservice.repositories.AreaRepository;
import com.pitospies.pitospieswebservice.repositories.IngredientRepository;
import com.pitospies.pitospieswebservice.repositories.OrderRepository;
import com.pitospies.pitospieswebservice.repositories.PieRepository;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceAlreadyExistsException;
import com.pitospies.pitospieswebservice.settings.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping(path="/api", produces="application/json")
public class OrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private IngredientRepository ingredientRepository;

    @Autowired
    private AreaRepository areaRepository;

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

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> handleRequest2() {
        List<Order> orders = (List<Order>) orderRepository.findAll();

        for(Order order : orders){
            order.setArea(areaRepository.findAreaByOrderId(order.getId()));
        }

        return new ResponseEntity<>(orders, HttpStatus.OK);
    }

    @PostMapping(value = "/orders", consumes = "application/json")
    public ResponseEntity<Order> handleRequest4(@Valid @RequestBody Order order) {
        order = orderRepository.save(order);

        //handle area
        Area area = areaRepository.findAreaByDescription(order.getArea().getDescription());
        order.setArea(area);
        orderRepository.updateArea(order.getId(), area.getId());


        // handle ingredients
/*        for (String ingredientName: pie.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
            if (ingredient==null)
                ingredient = ingredientRepository.save(new Ingredient(null, ingredientName));

            pieRepository.addIngredientToPie(pie.getId(), ingredient.getId());
        }
*/
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

/*        // handle ingredients
        // delete ingredients
        pie.setIngredients(pieRepository.findIngredientsOfPie(id));

        for (String ingredientName: pie.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
            pieRepository.removeIngredientFromPie(pie.getId(), ingredient.getId());
        }
        // add new ingredients
        for (String ingredientName: updatedPie.getIngredients()) {
            Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
            if (ingredient==null)
                ingredient = ingredientRepository.save(new Ingredient(null, ingredientName));

            pieRepository.addIngredientToPie(pie.getId(), ingredient.getId());
        }
*/
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

/*        if (updatedPieData.containsKey("awards")) {
            System.out.println("test");
            List<Map<String, Object>> awardsData = (List<Map<String, Object>>) updatedPieData.get("awards");
            System.out.println(awardsData);
            List<Award> awards = new ArrayList<>();
            for (Map<String, Object> awardData : awardsData) {
                Award award = new Award();
                award.setName((String) awardData.get("name"));
                // Set any other Award properties here
                awards.add(award);
            }
            pie.setAwards(awards);
        }
        if (updatedPieData.containsKey("ingredients")) {
            // handle ingredients
            // delete ingredients
            pie.setIngredients(pieRepository.findIngredientsOfPie(id));

            for (String ingredientName: pie.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
                pieRepository.removeIngredientFromPie(pie.getId(), ingredient.getId());
            }
            // add new ingredients
            System.out.println((List<String>) updatedPieData.get("ingredients"));
            for (String ingredientName: (List<String>) updatedPieData.get("ingredients")) {
                Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
                if (ingredient==null)
                    ingredient = ingredientRepository.save(new Ingredient(null, ingredientName));

                pieRepository.addIngredientToPie(pie.getId(), ingredient.getId());
            }
        }
*/
        //validate the new object
        BindingResult bindingResult = new BeanPropertyBindingResult(order, "order");
        validator.validate(order, bindingResult);
        if (bindingResult.hasErrors()) {
            throw new BindException(bindingResult);
        }

        // save
        orderRepository.save(order);

        return new ResponseEntity<>(order, HttpStatus.OK);
    }

    @DeleteMapping(value="/order/{id}")
    public ResponseEntity<?>  handleRequest7(@PathVariable int id) {
        Order order = orderRepository.findOrderById(id);

        if (order==null)
            return ResponseEntity.noContent().build();
        else {
/*            // handle ingredients
            pie.setIngredients(pieRepository.findIngredientsOfPie(id));

            for (String ingredientName: pie.getIngredients()) {
                Ingredient ingredient = ingredientRepository.findIngredientByName(ingredientName);
                pieRepository.removeIngredientFromPie(pie.getId(), ingredient.getId());
            }
*/
            orderRepository.delete(order);
            return new ResponseEntity<>(order, HttpStatus.OK);
        }
    }
}
