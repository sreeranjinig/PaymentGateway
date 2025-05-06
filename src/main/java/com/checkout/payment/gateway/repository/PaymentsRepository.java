package com.checkout.payment.gateway.repository;

import com.checkout.payment.gateway.repository.entity.Payment;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class PaymentsRepository {

  private final HashMap<UUID, Payment> payments = new HashMap<>();

  public Optional<Payment> findByPaymentId(UUID id) {
    return Optional.ofNullable(payments.get(id));
  }

  public Optional<Payment> save(Payment paymentRequest) {
    payments.putIfAbsent(paymentRequest.getId(), paymentRequest);
    return Optional.ofNullable(payments.get(paymentRequest.getId()));
  }

}
