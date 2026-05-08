# Guardrails API - Spring Boot Assignment

## Tech Stack
- Java 17
- Spring Boot 3
- PostgreSQL
- Redis
- Docker

---

# Project Overview

This project implements a backend system with Redis-based guardrails to safely handle bot interactions on posts while preventing AI compute runaway and notification spam.

The application is completely stateless and uses Redis for:
- virality tracking
- cooldown locks
- bot reply limits
- notification batching

PostgreSQL acts as the source of truth for persistent data.

---

# Features Implemented

## Phase 1 - Core API & Database
Implemented entities:
- User
- Bot
- Post
- Comment

REST APIs:
- Create Post
- Add Comment
- Like Post

---

## Phase 2 - Redis Virality Engine

### Virality Score Rules
- Bot Reply = +1
- Human Like = +20
- Human Comment = +50

Redis keys are updated in real-time using atomic increment operations.

### Guardrails Implemented

#### Horizontal Cap
A post can receive a maximum of 100 bot replies.

Implemented using Redis atomic INCR operations.

#### Vertical Cap
Comment depth cannot exceed 20.

#### Cooldown Cap
Bots cannot repeatedly interact with the same human user within the cooldown period.

Implemented using Redis `setIfAbsent()` with expiration.

---

## Phase 3 - Notification Engine

Implemented:
- Redis notification throttling
- Notification batching
- Scheduled notification sweeper using `@Scheduled`

Pending notifications are stored in Redis Lists.

---

# Thread Safety & Concurrency

Redis atomic operations were used to guarantee thread safety.

## Atomic Operations Used
- Redis INCR
- Redis SETNX (`setIfAbsent`)

These operations ensure concurrency-safe behavior during heavy simultaneous requests.

## Spam Test Handling
Even when multiple concurrent bot requests are sent:
- only the first 100 bot replies are accepted
- additional requests return HTTP 429

This prevents race condition failures.

---

# Statelessness

The application is fully stateless:
- no in-memory counters
- no static variables
- no HashMap storage

All runtime state is maintained in Redis.

---

# Running the Project

## Start Docker Containers

```bash
docker-compose up
```

## Run Spring Boot Application

```bash
mvn spring-boot:run
```

---

# API Endpoints

## Create Post

POST `/api/posts`

### Request Body

```json
{
  "authorId": 1,
  "authorType": "USER",
  "content": "Hello World"
}
```

---

## Add Comment

POST `/api/posts/{postId}/comments`

### Request Body

```json
{
  "authorId": 101,
  "authorType": "BOT",
  "content": "Automated reply",
  "depthLevel": 1
}
```

---

## Like Post

POST `/api/posts/{postId}/like?userId=5`

---

# Deliverables Included

- Spring Boot source code
- docker-compose.yml
- Postman collection JSON
- README documentation
