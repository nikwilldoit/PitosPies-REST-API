package com.pitospies.pitospieswebservice.models.validators;

import com.pitospies.pitospieswebservice.models.OrderItem;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.List;

public class AtLeastOneItemInOrderValidator implements ConstraintValidator<AtLeastOneItemInOrderConstraint, List<OrderItem>> {
    @Override
    public void initialize(AtLeastOneItemInOrderConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<OrderItem> orderItems, ConstraintValidatorContext constraintValidatorContext) {
        if (orderItems==null) {
            return false;
        }
        else {
            for (var orderItem: orderItems)
                if (orderItem.getQuantity()>0)
                    return true;
        }
        return false;
    }
}
