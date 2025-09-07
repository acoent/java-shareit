package ru.practicum.shareit.item.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = ItemAvailableValidator.class)
@Documented
public @interface ItemAvailable {
    String message() default "Item is not available";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
