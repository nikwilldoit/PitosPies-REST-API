package com.pitospies.pitospieswebservice.models.validators;

import com.pitos.firstwebservice.models.OrderItem;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class OrderItemValuesValidator implements ConstraintValidator<OrderItemValuesConstraint, List<OrderItem>> {
    @Override
    public void initialize(OrderItemValuesConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<OrderItem> orderItems, ConstraintValidatorContext constraintValidatorContext) {
        if (orderItems==null)
            return true;  // AtLeastOneItemConstraint will fail, and it's the only message we should have

        for (OrderItem orderItem: orderItems) {
            if (orderItem.getQuantity() <= 0 || orderItem.getQuantity() > 100)
                return false;
        }
        return true;
    }
}
