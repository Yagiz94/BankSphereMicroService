

BankSphereMicroService


A microservices-based banking application built with Spring Boot, Spring Cloud Gateway, Apache Kafka, Redis, MySQL, and Docker Compose. Each module (User, api-gateway Account, Transaction) is containerized and communicates over an internal Docker network. All services are orchestrated via Docker Compose, demonstrating a scalable, event-driven architecture.

Project Overview

BankSphereMicroService is a demonstration of a Dockerized microservices architecture for a simplified banking platform. It consists of:

  API Gateway: Routes external requests to internal microservices and applies security filters (JWT).

  User Service: Manages user registration(generates JWT on registration), login, and stores user‐specific data in MySQL.

  Account Service: Manages bank accounts (CRUD, validation), persists data in MySQL, listens for transactional events on Kafka in order to update the corresponding account's balance amount.

  Transaction Service: Processes transactions between accounts, produces transactional events and streams through Kafka, and ensures transactional consistency.

  Redis: Stores authentication tokens as secret keys with the corresponding userName as the key and user/session data to speed up login/authorization.

  MySQL Databases: Separate “banksphere_user,” “banksphere_account,” and “banksphere_transaction” schemas for each core service.

  Kafka & Zookeeper: Implements an event‐driven model; Transaction events are published to Kafka and consumed by Account Service.

  Docker Compose: Orchestrates all containers on a single Docker network (internal), handling ordered startup, healthchecks, and service discovery.
  
  Log4j is used to log important operations in the application. Seperate log files are generated and processed for each service, under the logs folder.

![system diagram](https://github.com/user-attachments/assets/fc4032be-5671-4392-acec-8aff0aaaa118)



Additional Datastores:
 - Redis (caching JWT secrets)
 - MySQL instances (one per service):

   • banksphere_user  
    • banksphere_account  
    • banksphere_transaction  

Logging & Monitoring

To enable centralized log collection and analysis, Filebeat, Elasticsearch, and Kibana are integrated alongside the microservices:

Filebeat: It is used for log shipment. It is implemented as a service inside the docker compose file. It tails service logs, and forwards them to Elasticsearch.
In our setup, a Filebeat sidecar container is attached to each microservice; it watches ./logs/service-name/*.log and transmits entries to the central cluster.

Elasticsearch: All logs are indexed here, enabling fast full-text search, aggregations, and time-series analysis over your logs.

Kibana: A visualization front end for Elasticsearch. You can build dashboards, define alerts, and drill into logs by service, severity level, or time range.

How It Works

Microservices (User, Transaction, Account, API-Gateway) write logs to their local stdout or log files. (log4j is used)

Filebeat (sidecar) collects and forwards these logs into Elasticsearch.

Kibana connects to Elasticsearch to provide a UI for searching and visualizing logs.


Technologies

  Java 17

  Spring Boot 3.1.4

  Spring Web (REST controllers)

  Spring Data JPA (MySQL)

  Spring Data Redis (Lettuce client)

  Spring Security + JWT

  Spring Cloud Gateway

  Spring Kafka

  Apache Kafka 3.x (running in KRaft mode)

  Apache Zookeeper (for legacy compatibility with Bitnami Kafka)

  Redis 7.x (standalone, no password)

  MySQL 8.x

  Docker & Docker Compose (v3.9)

  Maven (build system)

  Log4j

 
