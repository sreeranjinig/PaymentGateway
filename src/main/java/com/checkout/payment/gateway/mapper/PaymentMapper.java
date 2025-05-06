package com.checkout.payment.gateway.mapper;

import java.util.UUID;
import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.entity.Payment;
import com.fasterxml.jackson.databind.JsonNode;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, imports = {
    PaymentStatus.class})
public interface PaymentMapper {

  PaymentMapper MAPPER = Mappers.getMapper(PaymentMapper.class);

  @Mapping(target = "status", source = "paymentStatus", qualifiedByName = "mapToPaymentStatus")
  @Mapping(target = "cardNumberLastFour", source = "cardNumber")
  @Mapping(target = "expiryMonth", source = "expiryMonth", qualifiedByName = "mapMonthToString")
  @Mapping(target = "expiryYear", source = "expiryYear", qualifiedByName = "mapYearToString")
  GetPaymentResponse mapToGetPaymentResponse(Payment payment);

  @Mapping(target = "status", source = "paymentStatus", qualifiedByName = "mapToPaymentStatus")
  @Mapping(target = "cardNumberLastFour", source = "cardNumber")
  @Mapping(target = "expiryMonth", source = "expiryMonth", qualifiedByName = "mapMonthToString")
  @Mapping(target = "expiryYear", source = "expiryYear", qualifiedByName = "mapYearToString")
  PostPaymentResponse mapToPostPaymentResponse(Payment payment);

  @Mapping(target = "cardNumber", source = "paymentRequest.cardNumber", qualifiedByName = "maskCardNumber")
  @Mapping(target = "paymentStatus", expression = "java(mapBooleanToStatus(paymentResponse))")
  @Mapping(target = "id", expression = "java(mapAuthorizedId(paymentResponse))")
  Payment mapToPayment(PostPaymentRequest paymentRequest, JsonNode paymentResponse);

  default String mapBooleanToStatus(JsonNode paymentResponse) {
    return paymentResponse.path("authorized").asBoolean() ? PaymentStatus.AUTHORIZED.getName()
        : PaymentStatus.DECLINED.getName();
  }

  default UUID mapAuthorizedId(JsonNode paymentResponse) {
    return !paymentResponse.path("authorization_code").isEmpty() ?
        UUID.fromString(paymentResponse.path("authorization_code").asText()) : UUID.randomUUID();
  }

  @Named("mapMonthToString")
  default String mapMonthToString(int month) {
    return String.format("%02d", month);
  }

  @Named("mapYearToString")
  default String mapYearToString(int year) {
    return String.valueOf(year);
  }

  @Named("mapToPaymentStatus")
  default PaymentStatus toPaymentStatus(String status) {
    if (status == null || status.isEmpty()) {
      return null;
    }
    return PaymentStatus.valueOf(status.toUpperCase());
  }

  @Named("maskCardNumber")
  default String maskCardNumber(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 4) {
      throw new IllegalArgumentException("Card number must be at least 4 digits");
    }

    int unmaskedLength = 4;
    int maskedLength = cardNumber.length() - unmaskedLength;

    return "*".repeat(maskedLength) + cardNumber.substring(maskedLength);
  }
}
