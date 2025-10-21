package dev.pontin.basket_service.controller.requests;

import java.util.List;

public record BasketRequest(Long clientId, List<ProductRequest> products) {
}
