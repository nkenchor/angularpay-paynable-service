package io.angularpay.paynable.validation;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Instant;

public class ValidUTCDateValidator implements ConstraintValidator<ValidUTCDate, String> {

    public void initialize(ValidUTCDate constraint) {
    }

    public boolean isValid(String date, ConstraintValidatorContext context) {
        try {
            Instant.parse(date);
            return true;
        } catch (Exception ignored) {
            return false;
        }
    }
}
