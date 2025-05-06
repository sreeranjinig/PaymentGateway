package com.checkout.payment.gateway.validation.currency;

import java.util.Currency;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class IsoCurrencyValidator implements ConstraintValidator<IsoCurrency, String> {

  @Override
  public boolean isValid(String currency, ConstraintValidatorContext constraintValidatorContext) {
    if (StringUtils.isBlank(currency) || currency.length() != 3) {
      return false;
    }
    Set<Currency> currencies = Currency.getAvailableCurrencies();
    return currencies.stream().anyMatch(curr -> StringUtils.equalsIgnoreCase(curr.getCurrencyCode(), currency));
  }
}
