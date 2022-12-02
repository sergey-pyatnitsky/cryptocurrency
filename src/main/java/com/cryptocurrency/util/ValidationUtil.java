package com.cryptocurrency.util;

import com.cryptocurrency.exception.IncorrectDataException;
import org.springframework.validation.DataBinder;
import org.springframework.validation.Validator;

import java.util.Objects;

public class ValidationUtil {
    public static void validate(Object entity, Validator validator) {
        final DataBinder dataBinder = new DataBinder(entity);
        dataBinder.addValidators(validator);
        dataBinder.validate();

        if (dataBinder.getBindingResult().hasErrors()) {
            throw new IncorrectDataException(Objects.requireNonNull(dataBinder.getBindingResult().getFieldError()).getDefaultMessage());
        }
    }
}
