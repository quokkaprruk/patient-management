# Patient Management Microservice

## 1. Architecture and Key Concepts

This section outlines the fundamental concepts and structure used in this microservice and its related systems.

### Spring Boot Layered Architecture

The application is structured into four main layers:

1.  **Controller:** Handles incoming HTTP requests, calls the Service layer, and formats the final HTTP response.
2.  **Service:** Contains the core **business logic**. It coordinates actions, communicates with the Repository, and is responsible for converting **Entity** objects to **DTO**s and vice-versa.
3.  **Repository:** Manages data access by talking directly to the database, typically leveraging **Spring Data JPA**.
4.  **Database:** Stores the application data in tables, which are represented in the Java code by **Entity** classes.

### Data Transfer Object (DTO)

A **DTO** is a plain Java object used to transfer data between layers or over a network.

* The **Frontend** sends a request body (e.g., JSON).
* **Spring Boot** automatically converts the incoming JSON/request body into a **DTO** object for the Controller to use.

### gRPC - High Performance Communication

gRPC is a modern, high-performance communication protocol that provides a lightweight alternative to traditional REST/JSON.

* It uses **HTTP/2** for transport and **Protocol Buffers (.protobuf files)** for defining the data structure.
* **gRPC Server** defines services and methods; **gRPC Client** calls those methods remotely.
* They communicate using **protobuf messages** which are binary and very fast.

### Kafka

**Kafka** is a distributed messaging/event streaming system used for communication between different microservices (Asynchronous Communication).

* **Producers** send events (messages) to a Kafka topic.
* **Consumers** receive and process events from a Kafka topic.

### API Gateway

The **API Gateway** acts as the single, external entry point for all client requests.

* It sits **in front of all microservices**.
* Its primary function is to handle cross-cutting concerns, such as checking the **JWT token** for authentication/authorization, before forwarding valid requests to the internal services.
* The **API Gateway** runs on port **`4004`** (External Entry Point).
* Internal services run on their own ports (e.g., `4000` - Internal Service Port).

### LocalStack & Networking

The `LocalStack.java` file (or equivalent CDK file) is crucial for local development as it:
* Defines the **environment variables** necessary to connect the application to the local AWS services.
* Defines the **network topology** for the services, ensuring that components like the gRPC server and client can communicate via the **Docker Network** when running multiple containers.

***

## 2. Tutorial Credits
YouTube Channel: https://www.youtube.com/watch?v=tseqdcFfTUY&t=42341s
### 
***



## 3. Self Add-ons After Tutorial Completion

After completing a 12-hour tutorial, these are my self-added implementations:

- **Designed gRPC communication** between `patient-service` and `billing-service`.
- **Implemented gRPC integration** between both services with a clean and organized file structure.
- **Configured** `billing-service` and its **PostgreSQL database**, ensuring a billing account is automatically created when a new patient is added.
- **Tested the billing system (internal use only)** and restricted external access by enabling billing routes **only during the development phase**.
- **Created a Docker image** for the `billing-service` database.
- **Implemented an `s3Uploader` class** to pre-stage CloudFormation templates (artifacts for deployment).
- **Updated infrastructure** to support `billing-service` database connections and integrate the `S3 uploader` class.
- **Debugged, fixed, and updated shell scripts** to enable successful deployment to **LocalStack** (an AWS-like local environment).

###
***

## 4. Challenges

One of the most difficult parts was updating the shell script for deployment using a CloudFormation template stored in **LocalStack S3**.

At first, I faced an **“Invalid AWS key”** error.  
The root cause was that the command  
`aws cloudformation deploy ...`  
automatically tries to upload the template to **real AWS S3**, even I specified the localstack endpoint and used fake credentials intended for LocalStack.

However, my goal was to deploy entirely within the LocalStack environment using mock AWS credentials.

The issue was finally fixed by:
- Implementing an **`S3Uploader` class** to upload the CloudFormation template to **LocalStack S3**.
- Updating the **shell script** to fetch the template from **LocalStack S3** instead of AWS and then deploy it locally.

###
***



