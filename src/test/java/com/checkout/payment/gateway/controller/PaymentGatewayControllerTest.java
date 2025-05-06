package com.checkout.payment.gateway.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.checkout.payment.gateway.enums.PaymentStatus;
import com.checkout.payment.gateway.exception.EventProcessingException;
import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

@WebMvcTest(PaymentGatewayController.class)
class PaymentGatewayControllerTest {

  @Autowired
  private MockMvc mockMvc;

  @MockBean
  private PaymentGatewayService paymentService;

  @Test
  void shouldReturnPayment() throws Exception {
    GetPaymentResponse payment = new GetPaymentResponse();
    UUID paymentId = UUID.randomUUID();
    payment.setId(paymentId);
    payment.setAmount(10);
    payment.setCurrency("USD");
    payment.setStatus(PaymentStatus.AUTHORIZED);
    payment.setExpiryMonth("12");
    payment.setExpiryYear("2024");
    payment.setCardNumberLastFour("4444");

    when(paymentService.getPaymentById(paymentId)).thenReturn(payment);

    mockMvc.perform(MockMvcRequestBuilders.get("/payment/" + payment.getId()))
        .andExpect(status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status").value(payment.getStatus().getName()));
  }

  @Test
  void whenPaymentWithIdDoesNotExistThen404IsReturned() throws Exception {
    UUID paymentId = UUID.randomUUID();
    given(paymentService.getPaymentById(paymentId)).willThrow(
        new EventProcessingException("Payment with id " + paymentId + " not found"));

    mockMvc.perform(MockMvcRequestBuilders.get("/payment/" + paymentId))
        .andExpect(status().isNotFound())
        .andExpect(jsonPath("$.message").value("Payment with id " + paymentId + " not found"));
  }

  @Test
  void processPayment_Authorized() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343248877",
              "expiry_month": "12",
              "expiry_year": "2026",
              "currency": "GBP",
              "amount": 10,
              "cvv": "123"
            }
        """;

    PostPaymentResponse postPaymentResponse = new PostPaymentResponse();
    UUID paymentId = UUID.randomUUID();
    postPaymentResponse.setId(paymentId);
    postPaymentResponse.setAmount(10);
    postPaymentResponse.setCurrency("USD");
    postPaymentResponse.setStatus(PaymentStatus.AUTHORIZED);
    postPaymentResponse.setExpiryMonth("12");
    postPaymentResponse.setExpiryYear("2024");
    postPaymentResponse.setCardNumberLastFour("4444");

    when(paymentService.processPayment(any(PostPaymentRequest.class))).thenReturn(
        postPaymentResponse);
    mockMvc.perform(MockMvcRequestBuilders.post("/payments").contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status")
            .value(postPaymentResponse.getStatus().getName()));

  }

  @Test
  void processPayment_Declined() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "12",
              "expiry_year": "2026",
              "currency": "GBP",
              "amount": 10,
              "cvv": "123"
            }
        """;

    PostPaymentResponse postPaymentResponse = new PostPaymentResponse();
    UUID paymentId = UUID.randomUUID();
    postPaymentResponse.setId(paymentId);
    postPaymentResponse.setAmount(10);
    postPaymentResponse.setCurrency("USD");
    postPaymentResponse.setStatus(PaymentStatus.DECLINED);
    postPaymentResponse.setExpiryMonth("12");
    postPaymentResponse.setExpiryYear("2024");
    postPaymentResponse.setCardNumberLastFour("4444");

    when(paymentService.processPayment(any(PostPaymentRequest.class))).thenReturn(
        postPaymentResponse);
    mockMvc.perform(MockMvcRequestBuilders.post("/payments").contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isCreated())
        .andExpect(MockMvcResultMatchers.jsonPath("$.status")
            .value(postPaymentResponse.getStatus().getName()));

  }

  @Test
  void whenCardNumberHasString_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405abc42222",
              "expiry_month": "12",
              "expiry_year": "2026",
              "currency": "GBP",
              "amount": 10,
              "cvv": "123"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString(
            "Card number must contain only numeric characters")));
  }

  @Test
  void whenCardNumberMissing_thenReturns400() throws Exception {
    String requestJson = """
            {
              "expiry_month": "12",
              "expiry_year": "2026",
              "currency": "GBP",
              "amount": 10,
              "cvv": "123"
            }
        """; // Missing cardNumber

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("must not be null")));
  }

  @Test
  void whenCardNumberLengthNotBetween14And19_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "222240534324",
              "expiry_month": "12",
              "expiry_year": "2026",
              "currency": "GBP",
              "amount": 10,
              "cvv": "123"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString(
            "Card number must be between 14 and 19 characters long")));
  }

  @Test
  void whenExpiryMonthGreaterThan12_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "13",
              "expiry_year": "2026",
              "currency": "GBP",
              "amount": 10,
              "cvv": "123"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            content().string(org.hamcrest.Matchers.containsString("Month must be at most 12")));
  }

  @Test
  void whenExpiryMonthLessThan1_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "00",
              "expiry_year": "2026",
              "currency": "GBP",
              "amount": 10,
              "cvv": "123"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(
            content().string(org.hamcrest.Matchers.containsString("Month must be at least 01")));
  }

  @Test
  void whenExpiryDateIsNotInFuture_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "01",
              "expiry_year": "2024",
              "currency": "GBP",
              "amount": 10,
              "cvv": "123"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString(
            "Card expiry date(expiry_month + expiry_year) must be in the future")));
  }

  @Test
  void whenCVVHasString_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "01",
              "expiry_year": "2026",
              "currency": "GBP",
              "amount": 10,
              "cvv": "12a"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(
            org.hamcrest.Matchers.containsString("CVV must contain only numeric characters")));
  }

  @Test
  void whenCVVHasMoreThan4Chars_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "01",
              "expiry_year": "2026",
              "currency": "GBP",
              "amount": 10,
              "cvv": "12345"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(
            org.hamcrest.Matchers.containsString("CVV must be between 3 and 4 characters long")));
  }


  @Test
  void whenAmountIsMissing_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "01",
              "expiry_year": "2026",
              "currency": "GBP",
              "cvv": "123"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(
            org.hamcrest.Matchers.containsString("Amount is Required; Amount must be at least 1")));
  }

  @Test
  void whenCurrencyIsMissing_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "01",
              "expiry_year": "2026",
              "amount": 10,
              "cvv": "123"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("is not valid")));
  }


  @Test
  void whenCurrencyHas4Chars_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "01",
              "expiry_year": "2026",
              "currency": "GBPD",
              "amount": 10,
              "cvv": "123"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("is not valid")));
  }

  @Test
  void whenCurrencyIsNotValid_thenReturns400() throws Exception {
    String requestJson = """
            {
              "card_number": "2222405343242222",
              "expiry_month": "01",
              "expiry_year": "2026",
              "currency": "XYZ",
              "amount": 10,
              "cvv": "123"
            }
        """;

    mockMvc.perform(MockMvcRequestBuilders.post("/payments")
            .contentType(MediaType.APPLICATION_JSON)
            .content(requestJson))
        .andExpect(status().isBadRequest())
        .andExpect(content().string(org.hamcrest.Matchers.containsString("is not valid")));
  }
}
