package com.checkout.payment.gateway.validation.date;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import static java.lang.annotation.ElementType.ANNOTATION_TYPE;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * Validates a bean predicate method as returning true. Bean predicates must be of the form
 * {@code isSomething} or they'll be silently ignored.
 */
@Target({TYPE, ANNOTATION_TYPE, FIELD})
@Retention(RUNTIME)
@Constraint(validatedBy = ExpiryDateValidator.class)
@Documented
public @interface ValidExpiryDate {
  /**
   * The validation message for this constraint.
   *
   * @return the message
   */
  String message() default "Expiry date must be in the future";

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
