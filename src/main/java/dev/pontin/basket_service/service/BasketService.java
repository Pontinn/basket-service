package dev.pontin.basket_service.service;

import dev.pontin.basket_service.Entity.Basket;
import dev.pontin.basket_service.Entity.Product;
import dev.pontin.basket_service.Entity.enums.Status;
import dev.pontin.basket_service.Repository.BasketRepository;
import dev.pontin.basket_service.client.response.PlatziProductResponse;
import dev.pontin.basket_service.controller.requests.BasketRequest;
import dev.pontin.basket_service.controller.requests.ProductRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BasketService {

    private final BasketRepository basketRepository;
    private final ProductService productService;

    public Basket findById(String id) {
        return basketRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Carrinho não encontrado"));
    }

    public Basket createBasket(BasketRequest basketRequest) {

        basketRepository.findByClientIdAndStatus(basketRequest.clientId(), Status.OPEN)
                .ifPresent(basket -> {
                    throw new IllegalArgumentException("Já existe um carrinho para esse cliente");
                });

        List<Product> products = new ArrayList<>();
        basketRequest.products().forEach(productRequest -> {
            PlatziProductResponse platziProductResponse = checkIfProductExists(productRequest);

            products.add(Product.builder()
                    .id(platziProductResponse.id())
                    .title(platziProductResponse.title())
                    .price(platziProductResponse.price())
                    .quantity(productRequest.quantity())
                    .build());
        });

        Basket basket = Basket.builder()
                .clientId(basketRequest.clientId())
                .status(Status.OPEN)
                .products(products)
                .build();

        basket.calculateTotalPrice();
        return basketRepository.save(basket);
    }

    public Basket updateBasket(String id, BasketRequest basketRequest) {
        Basket basket = findById(id);

        List<Product> products = new ArrayList<>();
        basketRequest.products().forEach(productRequest -> {
            PlatziProductResponse platziProductResponse = checkIfProductExists(productRequest);

            products.add(Product.builder()
                    .id(platziProductResponse.id())
                    .title(platziProductResponse.title())
                    .price(platziProductResponse.price())
                    .quantity(productRequest.quantity())
                    .build());
        });

        basket.setProducts(products);
        basket.calculateTotalPrice();

        return basketRepository.save(basket);
    }

    private PlatziProductResponse checkIfProductExists(ProductRequest productRequest) {
        PlatziProductResponse productsById = productService.getProductsById(productRequest.id());

        if (productsById == null) {
            throw new IllegalArgumentException("Id: " + productRequest.id() + " não está vincúlado a nenhum produto.");
        }

        return productsById;
    }
}
