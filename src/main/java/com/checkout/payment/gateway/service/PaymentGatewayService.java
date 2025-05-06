package com.checkout.payment.gateway.service;

import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.mapper.PaymentMapper;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.repository.PaymentsRepository;
import com.checkout.payment.gateway.repository.entity.Payment;
import com.checkout.payment.gateway.util.PaymentUtil;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PaymentGatewayService {

  private static final Logger LOG = LoggerFactory.getLogger(PaymentGatewayService.class);

  private final PaymentsRepository paymentsRepository;

  private final PaymentUtil paymentUtil;

  public PaymentGatewayService(PaymentsRepository paymentsRepository, PaymentUtil paymentUtil) {
    this.paymentsRepository = paymentsRepository;
    this.paymentUtil = paymentUtil;
  }

  /**
   * Retrieves the payment for the corresponding id
   * @param id
   * @return GetPaymentResponse
   */
  public GetPaymentResponse getPaymentById(UUID id) {
    LOG.debug("Requesting access to to payment with ID {}", id);
    return paymentsRepository.findByPaymentId(id)
        .map(PaymentMapper.MAPPER::mapToGetPaymentResponse)
        .orElseThrow(() -> new EventProcessingException("Payment with id " + id + " not found"));
  }

  /**
   * Method for submitting the payment request to payment gateway and saving the payment details to repository.
   * Payment status is updated based on the response from the payment gateway
   *
   * @param paymentRequest : PostPaymentRequest send to the payment gateway
   * @return Package
   */
  public PostPaymentResponse processPayment(PostPaymentRequest paymentRequest) {
    LOG.debug("Creating a new payment");
    Payment payment = PaymentMapper.MAPPER.mapToPayment(paymentRequest,paymentUtil.callBankApi(paymentRequest));
    payment.setId(UUID.randomUUID());
    return paymentsRepository.save(payment)
        .map(PaymentMapper.MAPPER::mapToPostPaymentResponse)
        .orElseThrow(() -> new EventProcessingException("Payment not created"));
  }

}
