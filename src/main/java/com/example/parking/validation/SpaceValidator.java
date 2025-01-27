package com.example.parking.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SpaceValidator implements ConstraintValidator<ValidSpace, Integer> {

    @Value("${application.parking.capacity}")
    private Integer capacity;

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        return integer > 0 && integer <= capacity;
    }
}
