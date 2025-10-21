package dev.pontin.basket_service.Repository;

import dev.pontin.basket_service.Entity.Basket;
import dev.pontin.basket_service.Entity.enums.Status;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface BasketRepository extends MongoRepository<Basket, String> {

    Optional<Basket> findByClientIdAndStatus(Long client, Status status);
}
