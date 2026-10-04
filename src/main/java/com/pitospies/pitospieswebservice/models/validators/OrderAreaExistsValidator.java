package com.pitospies.pitospieswebservice.models.validators;

import com.pitospies.pitospieswebservice.models.Area;
import com.pitospies.pitospieswebservice.repositories.AreaRepository;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Autowired;

public class OrderAreaExistsValidator implements ConstraintValidator<OrderAreaExistsConstraint, Area> {
    @Autowired
    private AreaRepository areaRepository;

    @Override
    public void initialize(OrderAreaExistsConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(Area area, ConstraintValidatorContext constraintValidatorContext) {
        if (area==null)
            return false;
        return areaRepository.findAreaByDescription(area.getDescription())!=null;
    }
}
