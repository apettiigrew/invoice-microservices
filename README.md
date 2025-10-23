
# Invoice Microservices App

The Invoice microservice app is a simple invoice management app built to demonstrate the Microservices Architecture Pattern using SpringBoot, 
Spring Cloud, Docker and Kubernetes. The project is intended as a pure learning process to try out various concepts in a microservices and distributed environment. 
You are free to fork it and turn into something else or even provide cool updates via pull request. I'm happy to collab.

## Tutorial overview
https://code2tutorial.com/tutorial/66218165-f888-4dc1-bd18-ed3466c91ee9/index.md

## Functional services

The Invoice System is composed into three core microservices. Each application is independently deployable and structured around specific business domains.

**![functional-services.jpg](docs/assets/functional-services.jpg)**

#### User service
Contains general logic to create/register a user to the open source Keycloak Identity Access and Management Server.

| Endpoint Name  | Method | URL                    | Description                      |  
|---------------|--------|------------------------|----------------------------------|  
| create-users  | POST   | `{{user_url}}`         | Create a new user account       |  
| update-users  | PATCH  | `{{user_url}}/:id`     | Update user details             |  
| get all users | GET    | `{{user_url}}?page=0&size=10` | Retrieve a paginated list of users |  
| get single user | GET    | `{{user_url}}/:id`     | Retrieve details of a specific user |  
| delete-users  | DELETE | `{{user_url}}/:id`     | Delete a user account           |  


#### Invoice service
Performs the CRUD operations required to manage an invoice.

| Endpoint Name       | Method  | URL                          | Description                        |  
|---------------------|--------|------------------------------|------------------------------------|  
| create invoice     | POST   | `{{invoice_url}}`            | Create a new invoice              |  
| get all invoices   | GET    | `{{invoice_url}}?page=0&size=10` | Retrieve a paginated list of invoices |  
| get single invoice | GET    | `{{invoice_url}}/:id`        | Retrieve details of a specific invoice |  
| update invoice     | PATCH  | `{{invoice_url}}/:id`        | Update details of an invoice      |  
| delete invoice     | DELETE | `{{invoice_url}}/:id`        | Delete an invoice                 |  
| contact-info       | GET    | `{{invoice_url}}/contact-info` | Retrieve invoice-related contact info |  
| bus refresh       | POST   | `{{invoice_url}}/actuator/busrefresh` | Refresh configuration bus |  


#### Notification service
Sends an email via SendGrid API whenever an invoice has been created, updated or deleted. This serves as an eg of standalone service to manage all notification in the system. Can be extended to send sms, send reminder etc.  
Currently this is implemented as a Spring Cloud function that receives event from a message broker and sends an email to the client who is the recipient of an invoice.

## Infrastructure
[Spring cloud](https://spring.io/projects/spring-cloud) provides powerful tools for developers to quickly implement common distributed systems patterns  
![stack.png](docs/assets/stack.png)

### Config Server
[Spring Cloud Config](http://cloud.spring.io/spring-cloud-config/spring-cloud-config.html) is horizontally scalable centralized configuration service for the distributed systems. It uses a pluggable repository layer that currently supports local storage, Git, and Subversion. In this project, the config server connects with a github repository to pull configuration for the different microservices. You can see shared configuration repo here [invoice-config-server](https://github.com/apettiigrew/invoice-config-server).

##### Spring Cloud Config Server & Spring Cloud Bus
With the Config Server you have a central place to manage external properties for applications across all environments. [Spring Cloud Bus](https://spring.io/projects/spring-cloud-bus), facilitates seamless communication between all connected application instances by establishing a convenient event broadcasting channel. It offers an implementation for AMQP brokers, such as RabbitMQ, and Kafka. Spring Cloud Config offers the Monitor library, which enables the triggering of configuration change events in the Config Service. By exposing the /monitor endpoint, it facilitates the propagation of these events to all listening applications via the Bus. The Monitor library allows push notifications from popular code repository providers such as GitHub.
### Service Discovery

[](https://github.com/sqshq/piggymetrics/blob/master/README.md#service-discovery)

Service Discovery allows automatic detection of the network locations for all registered services. These locations might have dynamically assigned addresses due to auto-scaling, failures or upgrades.

The key part of Service discovery is the Registry. In this project, we use Netflix Eureka. Eureka is a good example of the client-side discovery pattern, where client is responsible for looking up the locations of available service instances and load balancing between them.

With Spring Boot, you can easily build Eureka Registry using the  `spring-cloud-starter-eureka-server` dependency,  `@EnableEurekaServer` annotation and simple configuration properties.

### API Gateway

API Gateway is a single entry point into the system, used to handle requests and routing them to the appropriate backend service.  In this project the gateway also provide security measures by only allow authorized request via OAuth2 using Keycloak as an authorization server to grant permissions to handle various request.

### Observability
Observability reveals a system's internal state through its outputs. In microservices, this is achieved by analyzing metrics, logs, and traces.

In this project we managed logs by utilizing Grafana Loki & Grafan Alloy. Grafana is a popular tool for visualizing metrics, logs, and traces from a variety of sources.

- Grafana Loki is a horizontally scalable, highly available, and cost-effective log aggregation system. It is designed to be easy to use and to scale to meet the needs of even the most demanding applications.

- Grafana Alloy is a lightweight log agent that ships logs from your containers to Loki. It is easy to configure and can be used to collect logs from a wide variety of sources.

- Prometheus acts as our monitoring system that gives developers valuable insights into the health and performance of their software

All these tools feed data into Grafana that allows us to visualize and analyze the data in a user-friendly way.
**![observability-dashboard.png](docs/assets/observability-dashboard.png)**

### Event Driven Model
We've added a publisher/subscriber model using RabbitMQ to distributes events to our notification service.  Our Notification service handles events via spring cloud function that subscribes to the message queue.

### Important endpoints

| **Service**       | **URL**                                                         |
|-------------------|-----------------------------------------------------------------|
| **Config Server** | http://localhost:8071/                                          |
| **Keycloak Server** | http://localhost:7080/                                          |
| **Eureka Server** | http://localhost:8070/                                          |
| **Grafana**       | http://localhost:3000/                                          |
| **Prometheus**    | http://localhost:9090/targets                                   |
| **Metrics**       | server_url/actuator/metrics <br> server_url/actuator/prometheus |


### Kubernetes + Helm
There are also helm charts created for each service in the ecosystem. 
The charts are located in the `helm` directory. 

### Contributions are welcome!
Invoice Microservice system is open source, and would greatly appreciate your help. Feel free to suggest and implement any improvements.
