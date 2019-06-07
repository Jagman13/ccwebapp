package com.csye6225.lms.validation;

import org.passay.*;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.Arrays;
import java.util.regex.Pattern;


public class PasswordValidator implements ConstraintValidator<PasswordConstraint,String> {

    @Override
    public void initialize(final PasswordConstraint constraintAnnotation) {
    }

    @Override
    public boolean isValid(final String password, final ConstraintValidatorContext context) {

        final org.passay.PasswordValidator validator = new org.passay.PasswordValidator(Arrays.asList(
                    new LengthRule(8,12),
                new UppercaseCharacterRule(1),
                        new DigitCharacterRule(1),
                        new SpecialCharacterRule(1),
                        new NumericalSequenceRule(3,false),
                        new WhitespaceRule()
                ));

        final RuleResult result = validator.validate(new PasswordData(password));
        if (result.isValid()) {
            return true;
        }

        context.disableDefaultConstraintViolation();

        String errors =validator.getMessages(result).toString();
//                stream()
//                .reduce("", (c, e) -> c + e + " ");

        context.buildConstraintViolationWithTemplate(errors).addConstraintViolation();
        return false;
    }
}
