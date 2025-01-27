package com.example.parking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SpaceValidator.class)
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidSpace {

    String message() default "Invalid space";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
