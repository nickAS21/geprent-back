package com.georent.validation;

import com.georent.dto.RentOrderRequestDTO;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class StartBeforeEndDateValidator implements ConstraintValidator<StartBeforeEndDate, RentOrderRequestDTO> {

    @Override
    public void initialize(StartBeforeEndDate constraintAnnotation) {
    }

    @Override
    public boolean isValid(RentOrderRequestDTO value, ConstraintValidatorContext context) {
        return value.getStartTime().isBefore(value.getEndTime());
    }
}
