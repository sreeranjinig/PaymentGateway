package com.checkout.payment.gateway.model;

import com.checkout.payment.gateway.validation.currency.IsoCurrency;
import com.checkout.payment.gateway.validation.date.ValidExpiryDate;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.io.Serializable;

@ValidExpiryDate
public class PostPaymentRequest implements Serializable {

  @JsonProperty("card_number")
  @NotNull
  @Size(min = 14, max = 19, message = "Card number must be between 14 and 19 characters long")
  @Pattern(regexp = "\\d+", message = "Card number must contain only numeric characters")
  private String cardNumber;
  @JsonProperty("expiry_month")
  @NotNull
  @Min(value = 1, message = "Month must be at least 01")
  @Max(value = 12, message = "Month must be at most 12")
  private int expiryMonth;
  @JsonProperty("expiry_year")
  @NotNull
  private int expiryYear;
  @IsoCurrency
  private String currency;
  @Min(value = 1, message = "Amount is Required; Amount must be at least 1")
  private int amount;
  @NotNull
  @Size(min = 3, max = 4, message = "CVV must be between 3 and 4 characters long")
  @Pattern(regexp = "\\d+", message = "CVV must contain only numeric characters")
  private String cvv;

  public String getCardNumber() {
    return cardNumber;
  }

  public void setCardNumber(String cardNumber) {
    this.cardNumber = cardNumber;
  }

  public int getExpiryMonth() {
    return expiryMonth;
  }

  public void setExpiryMonth(int expiryMonth) {
    this.expiryMonth = expiryMonth;
  }

  public int getExpiryYear() {
    return expiryYear;
  }

  public void setExpiryYear(int expiryYear) {
    this.expiryYear = expiryYear;
  }

  public String getCurrency() {
    return currency;
  }

  public void setCurrency(String currency) {
    this.currency = currency;
  }

  public int getAmount() {
    return amount;
  }

  public void setAmount(int amount) {
    this.amount = amount;
  }

  public String getCvv() {
    return cvv;
  }

  public void setCvv(String cvv) {
    this.cvv = cvv;
  }

  @JsonProperty("expiry_date")
  public String getExpiryDate() {
    return String.format("%d/%d", expiryMonth, expiryYear);
  }

  @Override
  public String toString() {
    return "PostPaymentRequest{" +
        "cardNumberLastFour=" + cardNumber +
        ", expiryMonth=" + expiryMonth +
        ", expiryYear=" + expiryYear +
        ", currency='" + currency + '\'' +
        ", amount=" + amount +
        ", cvv=" + cvv +
        '}';
  }
}
