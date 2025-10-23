package dev.pontin.basket_service.controller.requests;

import dev.pontin.basket_service.Entity.enums.PaymentMethods;

public record PaymentRequest(PaymentMethods paymentMethod) {
}
