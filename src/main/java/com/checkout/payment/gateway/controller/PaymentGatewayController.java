package com.checkout.payment.gateway.controller;

import com.checkout.payment.gateway.model.GetPaymentResponse;
import com.checkout.payment.gateway.model.PostPaymentRequest;
import com.checkout.payment.gateway.model.PostPaymentResponse;
import com.checkout.payment.gateway.service.PaymentGatewayService;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;

@RestController("api")
public class PaymentGatewayController {

  private final PaymentGatewayService paymentGatewayService;

  public PaymentGatewayController(PaymentGatewayService paymentGatewayService) {
    this.paymentGatewayService = paymentGatewayService;
  }

  @GetMapping("/payment/{id}")
  @Operation(summary = "Get a new payment")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Success", content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = PostPaymentResponse.class))}),
      @ApiResponse(responseCode = "404", description = "Not Found", content = @Content)})
  public ResponseEntity<GetPaymentResponse> getPostPaymentEventById(@PathVariable UUID id) {
    return new ResponseEntity<>(paymentGatewayService.getPaymentById(id), HttpStatus.OK);
  }

  @PostMapping(value = "/payments")
  @Operation(summary = "Create a new payment")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Payment is created", content = {@Content(mediaType = "application/json",
          schema = @Schema(implementation = PostPaymentResponse.class))})})
  public ResponseEntity<PostPaymentResponse> create(@Valid @RequestBody PostPaymentRequest paymentRequest) {
    return ResponseEntity.status(HttpStatus.CREATED).body(paymentGatewayService.processPayment(paymentRequest));
  }
}
