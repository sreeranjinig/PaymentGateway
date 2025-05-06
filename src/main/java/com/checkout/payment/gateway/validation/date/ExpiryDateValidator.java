package com.checkout.payment.gateway.validation.date;

import com.checkout.payment.gateway.model.PostPaymentRequest;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.YearMonth;

public class ExpiryDateValidator implements ConstraintValidator<ValidExpiryDate, PostPaymentRequest> {

  @Override
  public boolean isValid(PostPaymentRequest postPaymentRequest, ConstraintValidatorContext constraintValidatorContext) {

    int month = postPaymentRequest.getExpiryMonth();
    int year = postPaymentRequest.getExpiryYear();

    if (month < 1 || month > 12) {
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext.buildConstraintViolationWithTemplate("Expiry month must be between 1 and 12")
          .addConstraintViolation();
      return false;
    }

    YearMonth expiry = YearMonth.of(year, month);
    YearMonth now = YearMonth.now();

    if (!expiry.isAfter(now)) {
      constraintValidatorContext.disableDefaultConstraintViolation();
      constraintValidatorContext.buildConstraintViolationWithTemplate("Card expiry date(expiry_month + expiry_year) must be in the future")
          .addPropertyNode("expiryDate")
          .addConstraintViolation();
      return false;
    }

    return true;
  }
}
