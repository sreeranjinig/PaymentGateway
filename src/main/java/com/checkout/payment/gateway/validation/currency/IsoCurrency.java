package com.checkout.payment.gateway.validation.currency;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Validates a bean predicate method as returning true. Bean predicates must be of the form
 * {@code isSomething} or they'll be silently ignored.
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = IsoCurrencyValidator.class)
@Documented
public @interface IsoCurrency {

  /**
   * The validation message for this constraint.
   *
   * @return the message
   */
  String message() default "is not valid";

  /**
   * The groups the constraint belongs to.
   *
   * @return an array of classes representing the groups
   */
  Class<?>[] groups() default {};

  /**
   * The payloads of this constraint.
   *
   * @return the array of payload classes
   */
  @SuppressWarnings("UnusedDeclaration") Class<? extends Payload>[] payload() default {};
}
