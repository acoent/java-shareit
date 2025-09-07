package ru.practicum.shareit.item.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ItemExistsValidator.class)
@Documented
public @interface ItemExists {
    String message() default "Item not found";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
