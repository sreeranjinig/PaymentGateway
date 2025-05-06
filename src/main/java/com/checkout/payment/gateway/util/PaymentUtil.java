package com.checkout.payment.gateway.util;

import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

@Component
public class PaymentUtil {

  private static final String PAYMENT_GATEWAY_URL = "http://localhost:8080/payments";
  private static final String EXCEPTION_MESSAGE = "Bank payment service unavailable";
  private final RestTemplate restTemplate;

  @Autowired
  private PaymentUtil(RestTemplate restTemplate) {
    this.restTemplate = restTemplate;
  }

  /**
   * Method for calling bank simulator Returns whether the payment is authorized or not
   *
   * @return boolean
   */
  public JsonNode callBankApi(PostPaymentRequest paymentRequest) throws EventProcessingException {
    try {
      HttpHeaders headers = new HttpHeaders();
      HttpEntity<PostPaymentRequest> entity = new HttpEntity<>(paymentRequest, headers);

      ResponseEntity<String> response = restTemplate.postForEntity(PAYMENT_GATEWAY_URL, entity, String.class);
      String jsonBody = response.getBody();
      ObjectMapper mapper = new ObjectMapper();

      return mapper.readTree(jsonBody);
    } catch (JsonProcessingException | HttpServerErrorException.ServiceUnavailable e) {
      throw new EventProcessingException(EXCEPTION_MESSAGE);
    }
  }

}
