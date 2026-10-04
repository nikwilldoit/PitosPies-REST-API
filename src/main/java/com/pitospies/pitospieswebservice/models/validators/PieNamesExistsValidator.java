package com.pitospies.pitospieswebservice.models.validators;

import com.pitos.firstwebservice.models.OrderItem;
import com.pitos.firstwebservice.repositories.PieRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class PieNamesExistsValidator implements ConstraintValidator<PieNamesExistsConstraint, List<OrderItem>> {
    @Autowired
    private PieRepository pieRepository;

    @Override
    public void initialize(PieNamesExistsConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(List<OrderItem> orderItems, ConstraintValidatorContext constraintValidatorContext) {
        if (orderItems==null)
            return true;  // AtLeastOneItemConstraint will fail, and it's the only message we should have

        for (OrderItem orderItem: orderItems) {
            if (!pieRepository.existsPieByName(orderItem.getPie())) {
                return false;
            }
        }
        return true;
    }
}
