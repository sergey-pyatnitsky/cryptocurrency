package com.cryptocurrency.validation;

import com.cryptocurrency.entity.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@PropertySource("classpath:regexp.properties")
@Component
public class UserValidator implements Validator {

    private final Environment env;

    @Autowired
    private MessageSource messageSource;

    public UserValidator(Environment env) {
        this.env = env;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return User.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        User user = (User) target;
        if (user.getUsername().contains("@")) {
            if (checkForRegexp(user.getUsername(), env.getProperty("email.regexp")))
                errors.rejectValue("username", "email.not_matches",
                        messageSource.getMessage("profile.validator.email.regexp", null, LocaleContextHolder.getLocale()));
        } else {
            if (checkForRegexp(user.getUsername(), env.getProperty("username.regexp")))
                errors.rejectValue("username", "username.not_matches",
                        messageSource.getMessage("user.validator.login", null, LocaleContextHolder.getLocale()));

            if (checkForRegexp(user.getPassword(), env.getProperty("password.regexp")))
                errors.rejectValue("password", "password.not_matches",
                        messageSource.getMessage("user.validator.password", null, LocaleContextHolder.getLocale()));
        }
    }

    private boolean checkForRegexp(String input, String regexp) {
        return !input.matches(regexp);
    }
}
