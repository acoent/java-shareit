package ru.practicum.shareit.booking.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({TYPE})
@Retention(RUNTIME)
@Constraint(validatedBy = StartBeforeEndValidator.class)
@Documented
public @interface StartBeforeEnd {
    String message() default "start must be before end";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
