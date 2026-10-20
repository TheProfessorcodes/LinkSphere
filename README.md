# LinkSphere

**A LinkedIn-inspired professional networking backend built with Spring
Boot microservices.**

LinkSphere supports user authentication, professional connections,
image-based posts, likes, and event-driven notifications. It uses
PostgreSQL for relational data, Neo4j for the connection graph, Kafka
for asynchronous events, and Kubernetes on Google Kubernetes Engine
(GKE) for deployment.

![Java 21](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring
Boot](https://img.shields.io/badge/Spring_Boot-3.x%20%2F%204.x-6DB33F?logo=springboot&logoColor=white)
![Apache
Kafka](https://img.shields.io/badge/Apache_Kafka-event--driven-231F20?logo=apachekafka)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-4169E1?logo=postgresql&logoColor=white)
![Neo4j](https://img.shields.io/badge/Neo4j-graph%20database-008CC1?logo=neo4j&logoColor=white)
![Kubernetes](https://img.shields.io/badge/Kubernetes-GKE-326CE5?logo=kubernetes&logoColor=white)

## Highlights

-   **JWT authentication:** sign-up and login with BCrypt password
    hashing and stateless access tokens.
-   **API Gateway security:** validates JWTs and forwards the
    authenticated user's ID to downstream services through `X-User-Id`.
-   **Professional connections:** send, accept, and reject connection
    requests; retrieve first-degree connections using Neo4j.
-   **Posts and likes:** create posts with images, retrieve posts, and
    like or unlike posts.
-   **Media uploads:** upload images to Cloudinary through a dedicated
    service.
-   **Asynchronous notifications:** Kafka events drive notifications for
    connection requests, accepted connections, new posts, and likes.
-   **Microservice communication:** REST-based service calls with
    OpenFeign and asynchronous messaging with Kafka.
-   **Cloud deployment:** Kubernetes workloads and persistent database
    storage deployed on GKE.

## Architecture

``` mermaid
flowchart TB
    C[Client / Postman] --> I[GCE Ingress]
    I --> G[API Gateway<br/>JWT validation]

    G --> U[User Service]
    G --> P[Post Service]
    G --> CS[Connection Service]

    P -->|OpenFeign| CS
    P -->|OpenFeign| UP[Uploader Service]
    UP --> CL[Cloudinary]

    U --> UDB[(User PostgreSQL)]
    P --> PDB[(Posts PostgreSQL)]
    CS --> N[(Neo4j)]
    NS[Notification Service] --> NDB[(Notification PostgreSQL)]

    U -. user_created .-> K[(Apache Kafka)]
    P -. post_created / post_liked .-> K
    CS -. connection events .-> K
    K --> CS
    K --> NS

    E[Eureka Discovery Server<br/>local development] -. service discovery .-> G
    E -. service discovery .-> U
    E -. service discovery .-> P
    E -. service discovery .-> CS
    E -. service discovery .-> NS
    E -. service discovery .-> UP
```

### Design decisions

-   **Database per service:** User, Post, and Notification services use
    separate PostgreSQL databases; Connection Service stores
    relationships in Neo4j.
-   **Polyglot persistence:** relational records live in PostgreSQL,
    while connection relationships are represented as graph nodes and
    edges.
-   **Synchronous vs. asynchronous communication:** OpenFeign handles
    calls that need an immediate response; Kafka handles event-driven
    side effects.
-   **Gateway-level authentication:** the gateway validates the JWT and
    passes the caller's ID downstream in `X-User-Id`.
-   **Environment-specific discovery:** Eureka is used for local
    development; the Kubernetes profile relies on Kubernetes Services
    and DNS.

## Services

  ------------------------------------------------------------------------
  Service                   Port (local) Responsibility   Storage /
                                                          integrations
  ---------------- --------------------- ---------------- ----------------
  API Gateway                     `8080` Entry point,     Spring Cloud
                                         routing, JWT     Gateway
                                         validation,      
                                         identity header  

  Discovery Server                `8761` Service registry Eureka
                                         for local        
                                         development      

  User Service                    `9020` Sign-up, login,  PostgreSQL,
                                         password         Kafka
                                         hashing, JWT     
                                         issuance         

  Post Service                    `9010` Posts, likes,    PostgreSQL,
                                         connection       OpenFeign, Kafka
                                         lookups, post    
                                         events           

  Connection                      `9030` Connection       Neo4j, Kafka
  Service                                requests and     
                                         first-degree     
                                         connections      

  Notification                    `9040` Consumes events  PostgreSQL,
  Service                                and persists     Kafka
                                         notifications    

  Uploader Service                `9050` Image uploads    Cloudinary
  ------------------------------------------------------------------------

## Event-driven workflows

  -----------------------------------------------------------------------------------
  Kafka topic                   Producer          Consumer          Outcome
  ----------------------------- ----------------- ----------------- -----------------
  `user_created_topic`          User Service      Connection        Creates the
                                                  Service           user's `Person`
                                                                    node in Neo4j

  `connection_request_topic`    Connection        Notification      Notifies the
                                Service           Service           request recipient

  `connection_accepted_topic`   Connection        Notification      Notifies the
                                Service           Service           original
                                                                    requester

  `post_created_topic`          Post Service      Notification      Creates
                                                  Service           notifications for
                                                                    first-degree
                                                                    connections

  `post_liked_topic`            Post Service      Notification      Notifies the post
                                                  Service           owner
  -----------------------------------------------------------------------------------

### Example: creating a post

1.  The client sends a multipart request through the API Gateway.
2.  The Post Service asks the Uploader Service to upload the image to
    Cloudinary.
3.  The Post Service saves the post in PostgreSQL.
4.  It retrieves the author's first-degree connections through
    OpenFeign.
5.  It publishes post-created events to Kafka.
6.  The Notification Service consumes the events and stores
    notifications.

## API overview

All routes below are accessed through the API Gateway. Protected
endpoints require:

``` http
Authorization: Bearer <JWT>
```

### Authentication

  -----------------------------------------------------------------------------
  Method                  Endpoint                      Description
  ----------------------- ----------------------------- -----------------------
  `POST`                  `/api/v1/users/auth/signup`   Register a user

  `POST`                  `/api/v1/users/auth/login`    Log in and receive an
                                                        access token
  -----------------------------------------------------------------------------

### Posts and likes

  ----------------------------------------------------------------------------------------------
  Method                  Endpoint                                       Description
  ----------------------- ---------------------------------------------- -----------------------
  `POST`                  `/api/v1/posts/core`                           Create a post using
                                                                         `multipart/form-data`

  `GET`                   `/api/v1/posts/core/{postId}`                  Retrieve a post

  `GET`                   `/api/v1/posts/core/users/{userId}/allPosts`   List a user's posts

  `POST`                  `/api/v1/posts/likes/{postId}`                 Like a post

  `POST`                  `/api/v1/posts/unlikes/{postId}`               Remove your like
  ----------------------------------------------------------------------------------------------

For post creation, send a JSON part named `post` (for example,
`{"content":"Hello from LinkSphere"}`) and a file part named `file`.

### Connections

  --------------------------------------------------------------------------------------------------
  Method                  Endpoint                                           Description
  ----------------------- -------------------------------------------------- -----------------------
  `GET`                   `/api/v1/connections/core/{userId}/first-degree`   List first-degree
                                                                             connections

  `POST`                  `/api/v1/connections/core/request/{userId}`        Send a connection
                                                                             request

  `POST`                  `/api/v1/connections/core/accept/{userId}`         Accept a request from
                                                                             that user

  `POST`                  `/api/v1/connections/core/reject/{userId}`         Reject a request from
                                                                             that user
  --------------------------------------------------------------------------------------------------

The Connection Service validates common cases such as self-connection
attempts, duplicate requests, requests to already-connected users, and
accepting or rejecting a nonexistent request.

### Quick API example

``` bash
BASE=http://localhost:8080

# Register
curl -X POST "$BASE/api/v1/users/auth/signup" \
  -H "Content-Type: application/json" \
  -d '{"name":"Ada Lovelace","email":"ada@example.com","password":"replace-me"}'

# Log in
curl -X POST "$BASE/api/v1/users/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"ada@example.com","password":"replace-me"}'
```

Use the token returned by your login endpoint as a Bearer token for
protected routes. Check the response format in the User Service if you
want to automate token extraction in a script.

## Technology stack

  -----------------------------------------------------------------------
  Area                                Technologies
  ----------------------------------- -----------------------------------
  Language and framework              Java 21, Spring Boot, Spring Web
                                      MVC, Spring Data JPA, Spring Data
                                      Neo4j

  Microservices                       Spring Cloud Gateway, Eureka,
                                      OpenFeign

  Security                            JWT (`jjwt`), BCrypt

  Messaging                           Apache Kafka

  Databases                           PostgreSQL 16, Neo4j 4.4

  Media storage                       Cloudinary

  Build and container images          Maven Wrapper, Jib

  Deployment                          Docker, Kubernetes, Google
                                      Kubernetes Engine, GCE Ingress
  -----------------------------------------------------------------------

## Project structure

``` text
LinkSphere/
├── APIGateway/
├── DiscoveryServer/DiscoveryServer/
├── User-Service/User-Service/
├── Post-Service/Post-Service/
├── ConnectionService/ConnectionService/
├── Notification-Service/Notification-Service/
├── Uploader-Service/Uploader-Service/
└── k8s/
    ├── api-gateway.yml
    ├── user-service.yml
    ├── posts-service.yml
    ├── connections-service.yml
    ├── notification-service.yml
    ├── uploader-service.yml
    ├── kafka.yml
    ├── ingress.yml
    └── database manifests
```

## Run locally

### Prerequisites

-   JDK 21
-   Docker
-   A Cloudinary account and API credentials

### 1. Start the infrastructure

Start Kafka, PostgreSQL, and Neo4j using your preferred local setup.
Configure the required PostgreSQL databases (`LS_userDB`, `LinkSphere`,
and `notificationDb`) and set Neo4j authentication to match your local
service configuration.

**Use local-only development credentials. Do not reuse production
credentials or commit secrets to Git.**

### 2. Configure environment variables

Provide the Cloudinary credentials required by Uploader Service:

``` bash
export CLOUDINARY_CLOUD_NAME=<your-cloud-name>
export CLOUDINARY_API_KEY=<your-api-key>
export CLOUDINARY_API_SECRET=<your-api-secret>
```

Also configure database credentials and the JWT signing key as required
by each service's local configuration. The API Gateway and User Service
must use the same JWT key.

### 3. Start the services

Run each command from the repository root in a separate terminal. On
Windows, use `mvnw.cmd` if needed.

``` bash
cd DiscoveryServer/DiscoveryServer
./mvnw spring-boot:run
```

Repeat for the following service directories, then start the gateway:

``` text
Uploader-Service/Uploader-Service
User-Service/User-Service
ConnectionService/ConnectionService
Post-Service/Post-Service
Notification-Service/Notification-Service
APIGateway
```

For local development, start Eureka and the infrastructure first.
Confirm service configuration and ports before testing requests through
`http://localhost:8080`.

## Kubernetes deployment (GKE)

The repository contains Kubernetes manifests under `k8s/` for the
services, infrastructure, and ingress.

1.  Configure `gcloud` for the correct project and GKE cluster.
2.  Build and publish the service images using the repository's
    configured Jib settings.
3.  Create Kubernetes Secrets for credentials; ensure manifests
    reference those Secrets.
4.  Apply the manifests in the required order and verify the rollout.
5.  Check the ingress address and test the API through the gateway.

Useful verification commands:

``` bash
kubectl get deployments
kubectl get pods -o wide
kubectl get services
kubectl get ingress
kubectl rollout status deployment/api-gateway
```

Review the manifests before applying them to a new cluster. Confirm
image names, resource requests, persistent-volume availability, secret
references, and ingress configuration for your environment.

## Configuration and security

Important configuration includes:

-   `SPRING_PROFILES_ACTIVE`: selects the local or `k8s` profile.
-   `DB_SERVER`, `DB_NAME`, `DB_USERNAME`, `DB_PASSWORD`: database
    connectivity.
-   `UPLOADER_SERVICE_URI`, `CONNECTIONS_SERVICE_URI`: internal service
    addresses where configured.
-   `USER_SERVICE_URI`, `POSTS_SERVICE_URI`: gateway route targets where
    configured.
-   `CLOUDINARY_CLOUD_NAME`, `CLOUDINARY_API_KEY`,
    `CLOUDINARY_API_SECRET`: Cloudinary credentials.
-   `jwt.secretKey`: JWT signing and verification key; it must match
    between User Service and API Gateway.

**Never commit real API keys, JWT secrets, or database passwords.** Use
environment variables or Kubernetes Secrets, rotate any credentials that
were exposed, and avoid putting secret values in screenshots or logs.

## Current limitations and future work

-   Add API endpoints to read notifications and mark them as read.
-   Add comments and comment notifications.
-   Add refresh tokens and role-based authorization.
-   Build a feed/timeline from first-degree connections' posts.
-   Add second-degree connection suggestions using Neo4j.
-   Add CI/CD for automated testing, image publishing, and deployment.
-   Add centralized logging, metrics, and distributed tracing.
-   Build a web or mobile frontend.

## Author

**TheProfessorcodes** · [GitHub](https://github.com/TheProfessorcodes)

------------------------------------------------------------------------

If you explore the project, feedback and suggestions are welcome.
