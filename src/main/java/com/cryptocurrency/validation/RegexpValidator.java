package com.cryptocurrency.validation;

import com.cryptocurrency.validation.entity.RegexpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
@PropertySource("classpath:regexp.properties")
public class RegexpValidator implements Validator {
    private final Environment env;

    @Autowired
    private MessageSource messageSource;

    public RegexpValidator(Environment env) {
        this.env = env;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RegexpEntity.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegexpEntity regexpEntity = (RegexpEntity) target;
        if(regexpEntity.getPassword() != null && !checkForRegexp(regexpEntity.getPassword(), env.getProperty("password.regexp")))
            errors.rejectValue("password", "password.not_matches",
                    messageSource.getMessage("user.validator.password", null, LocaleContextHolder.getLocale()));

        if(regexpEntity.getUsername() != null && !checkForRegexp(regexpEntity.getUsername(), env.getProperty("username.regexp")))
            errors.rejectValue("username", "username.not_matches",
                    messageSource.getMessage("user.validator.login", null, LocaleContextHolder.getLocale()));

        if(regexpEntity.getEmail() != null && !checkForRegexp(regexpEntity.getEmail(), env.getProperty("email.regexp")))
            errors.rejectValue("email", "email.not_matches",
                    messageSource.getMessage("profile.validator.email.regexp", null, LocaleContextHolder.getLocale()));

        if(regexpEntity.getPhone() != null && !checkForRegexp(regexpEntity.getPhone(), env.getProperty("phone.regexp")))
            errors.rejectValue("phone", "phone.not_matches",
                    messageSource.getMessage("profile.validator.phone.regexp", null, LocaleContextHolder.getLocale()));

    }

    private boolean checkForRegexp(String input, String regexp) {
        return input.matches(regexp);
    }
}
