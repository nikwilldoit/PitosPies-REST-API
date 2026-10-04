package com.pitospies.pitospieswebservice.models.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = AtLeastOneItemInOrderValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AtLeastOneItemInOrderConstraint {
    String message() default "Λάθος Παραγγελία";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
