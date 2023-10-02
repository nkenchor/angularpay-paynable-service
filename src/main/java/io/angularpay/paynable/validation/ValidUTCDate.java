package io.angularpay.paynable.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = ValidUTCDateValidator.class)
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidUTCDate {
    String message() default "Please provide valid UTC date";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
