package com.checkout.payment.gateway.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.repository.entity.Payment;
import com.checkout.payment.gateway.util.PaymentUtil;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class PaymentGatewayServiceTest {

  @Mock
  private PaymentUtil paymentUtil;

  @Test
  void testFindByPaymentId() {
    PaymentsRepository fakePaymentRepository = new PaymentsRepository();
    PaymentGatewayService paymentGatewayService = new PaymentGatewayService(fakePaymentRepository, paymentUtil);

    // Arrange
    Payment paymentRequest =new Payment(UUID.randomUUID(), PaymentStatus.AUTHORIZED.getName(),
        "************4444", 01, 2030, "GBP", 100);
    fakePaymentRepository.save(paymentRequest);


    GetPaymentResponse postPaymentResponse = paymentGatewayService.getPaymentById(paymentRequest.getId());
    assertNotNull(postPaymentResponse);

  }


  @Test
  void testPaymentIdNotFoundThrowsException() {
    // Arrange
    PaymentsRepository fakePaymentRepository = new PaymentsRepository();
    PaymentGatewayService paymentGatewayService = new PaymentGatewayService(fakePaymentRepository, paymentUtil);
    UUID unknownId = UUID.randomUUID();
    Exception ex = assertThrows(EventProcessingException.class, () ->
        paymentGatewayService.getPaymentById(unknownId));

    assertEquals("Payment with id " + unknownId + " not found", ex.getMessage());
  }


}
