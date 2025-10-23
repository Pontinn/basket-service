# Serviço de Carrinho de Compras (Basket Service)

Este projeto é um microsserviço de "Carrinho de Compras" (Basket Service) desenvolvido em Java com Spring Boot. Ele é projetado para gerenciar os carrinhos de compras de usuários em uma arquitetura de e-commerce, persistindo os dados em um banco NoSQL (MongoDB) e consumindo informações de produtos de uma API externa.

O serviço também utiliza Redis para cachear as respostas da API de produtos, melhorando a performance e reduzindo a latência em consultas repetidas.

## Funcionalidades Principais

* **Gerenciamento de Carrinho:**
    * Criar um novo carrinho para um cliente.
    * Atualizar os produtos dentro de um carrinho existente.
    * Buscar um carrinho pelo seu ID.
    * Realizar o "pagamento" de um carrinho, mudando seu status para `VENDIDO`.
* **Integração Externa:**
    * Consome uma API externa (Platzi Fake Store API) para validar e buscar informações de produtos (nome, preço) usando `Spring Cloud OpenFeign`.
* **Validação de Negócio:**
    * Impede que um cliente tenha mais de um carrinho com status `ABERTO` simultaneamente.
    * Valida se um produto existe na API externa antes de adicioná-lo ao carrinho.
    * Impede o pagamento de um carrinho que já foi `VENDIDO`.
* **Cache:**
    * Utiliza `Spring Cache` com **Redis** para armazenar em cache as chamadas para a API externa de produtos, otimizando a performance.
* **Persistência:**
    * Utiliza **MongoDB** para armazenar os dados dos carrinhos de forma flexível e escalável.

## Tecnologias Utilizadas

* **Java 17+**
* **Spring Boot**
    * `Spring Web`: Para a criação dos endpoints REST.
    * `Spring Data MongoDB`: Para persistência de dados no MongoDB.
    * `Spring Data Redis`: Para integração com o Redis.
    * `Spring Cache`: Para abstração e gerenciamento de cache.
    * `Spring Cloud OpenFeign`: Para comunicação declarativa com a API REST externa de produtos.
* **MongoDB**: Banco de dados NoSQL para armazenar os carrinhos.
* **Redis**: Banco de dados em memória usado para caching.
* **Lombok**: Para reduzir código boilerplate (getters, setters, constructors).

## Arquitetura Simplificada

1.  **Controller (`BasketController`, `ProductController`)**: Expõe os endpoints REST para o cliente (frontend, Postman, etc.).
2.  **Service (`BasketService`, `ProductService`)**: Contém a lógica de negócio principal.
    * `BasketService`: Orquestra a criação, atualização e pagamento dos carrinhos.
    * `ProductService`: Gerencia a busca de dados de produtos, aplicando a lógica de cache.
3.  **Repository (`BasketRepository`)**: Interface do Spring Data para interagir com a coleção `basket` no MongoDB.
4.  **Client (`PlatziStoreClient`)**: Interface Feign que define como o serviço se comunica com a API externa de produtos.
5.  **Cache**: As chamadas em `ProductService` são interceptadas pelo Spring Cache. Se a resposta estiver no Redis, ela é retornada de lá; senão, a chamada à API externa é feita e o resultado é salvo no Redis.
6.  **Database**: O `BasketService` salva as entidades `Basket` diretamente no MongoDB.

## Pré-requisitos

Para executar este projeto localmente, você precisará ter instalados:

* Java (JDK 17 ou superior)
* Maven ou Gradle
* MongoDB (rodando em `localhost:27017`)
* Redis (rodando em `localhost:6379`)

## Configuração

O arquivo de configuração principal é o `src/main/resources/application.yaml`. Certifique-se de que suas instâncias locais de MongoDB e Redis estão acessíveis nas portas padrão.

```yaml
spring:
  application:
    name: basket-service

  data:
    mongodb:
      host: localhost
      port: 27017
      database: basket-service
    redis:
      host: localhost
      port: 6379
      password: sa # Ajuste se sua instância do Redis tiver uma senha diferente

  cache:
    redis:
      time-to-live: 60000 # Cache expira em 60 segundos

basket:
  client:
    platzi: [https://api.escuelajs.co/api/v1](https://api.escuelajs.co/api/v1) # URL da API externa de produtos
```

## Endpoints da API

### Carrinho (Basket)

* `POST /basket`
    * Cria um novo carrinho.
    * **Body (exemplo):**
        ```json
        {
          "clientId": 1,
          "products": [
            { "id": 10, "quantity": 2 },
            { "id": 5, "quantity": 1 }
          ]
        }
        ```

* `GET /basket/{id}`
    * Busca um carrinho de compras pelo seu ID (String do MongoDB).

* `PUT /basket/{id}`
    * Atualiza (substitui) a lista de produtos de um carrinho existente.
    * **Body (exemplo):**
        ```json
        {
          "clientId": 1,
          "products": [
            { "id": 20, "quantity": 1 }
          ]
        }
        ```

* `PUT /basket/{id}/payment`
    * Efetua o pagamento de um carrinho, alterando seu status para `SOLD`.
    * **Body (exemplo):**
        ```json
        {
          "paymentMethod": "CREDIT"
        }
        ```
    * `paymentMethod` pode ser `CREDIT`, `DEBIT`, ou `PIX`.

### Produtos (Proxy Cacheado)

* `GET /products`
    * Retorna uma lista de todos os produtos disponíveis na API externa (Platzi). A resposta é cacheada.

* `GET /products/{id}`
    * Retorna os detalhes de um produto específico da API externa (Platzi). A resposta é cacheada.

## Como Executar

1.  Clone o repositório:

    ```bash
    git clone [https://github.com/Pontinn/basket-service.git](https://github.com/Pontinn/basket-service.git)
    cd basket-service
    ```

2.  Inicie o MongoDB e o Redis localmente.

3.  Execute a aplicação Spring Boot:

    ```bash
    ./mvnw spring-boot:run
    ```

    (Ou execute a classe `EcommerceCarrinhoDeComprasApplication.java` pela sua IDE).

A aplicação estará disponível em `http://localhost:8080`.
