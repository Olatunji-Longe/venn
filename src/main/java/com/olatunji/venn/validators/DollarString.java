package com.olatunji.venn.validators;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DollarStringValidator.class)
@Documented
public @interface DollarString {
    String message() default "Invalid currency string. Must be in the format: $0.00";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
