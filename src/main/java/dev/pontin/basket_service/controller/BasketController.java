package dev.pontin.basket_service.controller;

import dev.pontin.basket_service.Entity.Basket;
import dev.pontin.basket_service.controller.requests.BasketRequest;
import dev.pontin.basket_service.service.BasketService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/basket")
@RequiredArgsConstructor
public class BasketController {

    private final BasketService basketService;

    @GetMapping("/{id}")
    public ResponseEntity<Basket> findBasket(@PathVariable("id") String id) {
        return ResponseEntity.ok(basketService.findById(id));
    }

    @PostMapping
    public ResponseEntity<Basket> createBasket(@RequestBody BasketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(basketService.createBasket(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Basket> updateBasket(@PathVariable("id") String id, @RequestBody BasketRequest request) {
        return ResponseEntity.ok().body(basketService.updateBasket(id, request));
    }
}
