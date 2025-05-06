package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.mapper.PaymentMapper;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.repository.entity.Payment;
import com.checkout.payment.gateway.util.PaymentUtil;
import java.util.Optional;
import java.util.UUID;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class PaymentGatewayServiceTest {

  @Mock
  private PaymentUtil paymentUtil;

  @Mock
  private PaymentMapper paymentMapper;

  @Mock
  private PaymentsRepository paymentsRepository;

  @InjectMocks
  private PaymentGatewayService paymentGatewayService;

  @Test
  void testFindByPaymentId() {
    UUID paymentId = UUID.randomUUID();
    Payment paymentRequest =new Payment(paymentId, PaymentStatus.AUTHORIZED.getName(),
        "************4444", 01, 2030, "GBP", 100);
    when(paymentsRepository.findByPaymentId(any(UUID.class))).thenReturn(Optional.of(paymentRequest));
    GetPaymentResponse postPaymentResponse = paymentGatewayService.getPaymentById(paymentId);
    assertNotNull(postPaymentResponse);

  }


  @Test
  void testPaymentIdNotFoundThrowsException() {
    UUID unknownId = UUID.randomUUID();
    Exception ex = assertThrows(EventProcessingException.class, () ->
        paymentGatewayService.getPaymentById(unknownId));

    assertEquals("Payment with id " + unknownId + " not found", ex.getMessage());
  }


  @Test
  void processPaymentTestSuccess() throws JsonProcessingException {

    PostPaymentRequest postPaymentRequest = new PostPaymentRequest();
    postPaymentRequest.setAmount(10);
    postPaymentRequest.setCurrency("USD");
    postPaymentRequest.setExpiryMonth(12);
    postPaymentRequest.setExpiryYear(2024);
    postPaymentRequest.setCardNumber("1111222233334444");
    postPaymentRequest.setCvv("123");

    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode mockNode = objectMapper.readTree("{\"authorized\":true, \"authorization_code\":\"083d3cbb-5db5-4995-89f6-9f339e7e5660\"}");
    when(paymentUtil.callBankApi(any(PostPaymentRequest.class))).thenReturn(mockNode);
    Payment paymentRequest =new Payment(UUID.randomUUID(), PaymentStatus.AUTHORIZED.getName(),
        "************4444", 01, 2030, "GBP", 100);
    when(paymentsRepository.save(any(Payment.class))).thenReturn(Optional.of(paymentRequest));
    PostPaymentResponse postPaymentResponse = paymentGatewayService.processPayment(postPaymentRequest);
    assertNotNull(postPaymentResponse);

  }

  @Test
  void processPaymentTestFailure() throws JsonProcessingException {

    PostPaymentRequest postPaymentRequest = new PostPaymentRequest();
    postPaymentRequest.setCurrency("USD");
    postPaymentRequest.setExpiryMonth(12);
    postPaymentRequest.setExpiryYear(2024);
    postPaymentRequest.setCardNumber("1111222233334444");
    postPaymentRequest.setCvv("123");
    ObjectMapper objectMapper = new ObjectMapper();
    JsonNode mockNode = objectMapper.readTree("{\"authorized\":true, \"authorization_code\":\"083d3cbb-5db5-4995-89f6-9f339e7e5660\"}");
    when(paymentUtil.callBankApi(any(PostPaymentRequest.class))).thenReturn(mockNode);

    Exception ex = assertThrows(EventProcessingException.class, () ->
        paymentGatewayService.processPayment(postPaymentRequest));

    assertEquals("Payment not created", ex.getMessage());
  }


}
