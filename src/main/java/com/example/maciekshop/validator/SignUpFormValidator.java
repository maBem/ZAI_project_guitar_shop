package com.example.maciekshop.validator;

import com.example.maciekshop.form.CustomerForm;
import com.example.maciekshop.form.SignUpForm;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

@Component
public class SignUpFormValidator implements Validator {

    private EmailValidator emailValidator = EmailValidator.getInstance();

    @Override
    public boolean supports(Class<?> clazz) {
        return clazz == SignUpForm.class;
    }

    @Override
    public void validate(Object target, Errors errors) {
        SignUpForm signUpForm = (SignUpForm) target;

        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "userName", "NotEmpty.signUpForm.userName");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "email", "NotEmpty.signUpForm.email");
        ValidationUtils.rejectIfEmptyOrWhitespace(errors, "password", "NotEmpty.SignUpForm.password");

        if (!emailValidator.isValid(signUpForm.getEmail())) {
            errors.rejectValue("email", "Pattern.signUpForm.email");
        }
    }
}
