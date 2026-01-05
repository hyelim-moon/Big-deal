# Big-deal Project
지역 화폐 이용 편의성 향상을 위한 웹 서비스 (Spring Boot)

---

## Overview
Together Local Currency Project는 지역 화폐를 사용하는 과정에서 느껴지는 불편함(접근성, 인증 절차, 이용 흐름)을 줄이기 위해 개발한 서비스입니다.  
사용자가 지역 화폐를 더 쉽고 안전하게 사용할 수 있도록 인증/보안 구조를 우선으로 설계하고, 데이터 관리(PostgreSQL)와 캐시/인증 보조(Redis), 외부 연동(메일·카카오)을 결합해 서비스 기반을 구성했습니다.

또한 OpenAPI(Swagger) 문서화를 통해 API를 명확히 관리하며, 서버 렌더링(Thymeleaf) 화면 구성을 포함해 단일 백엔드 프로젝트로 운영 가능한 형태를 지향했습니다.

---

## Key Features
- 회원가입/로그인 및 JWT 기반 인증/인가 (Spring Security)
- 이메일 인증(메일 발송) 기반 사용자 검증
- Kakao 소셜 로그인 연동
- PostgreSQL + Spring Data JPA 기반 데이터 관리
- Redis 기반 인증 관련 단기 데이터 관리(예: 인증 코드, 캐시)
- OpenAPI(Swagger) 기반 API 문서 제공
- Thymeleaf 기반 화면 구성

---

## Tech Stack

### Backend
- Java
- Spring Boot 3.0.2
- Spring Web (MVC)
- Spring WebFlux (WebClient 기반 외부 연동)
- Spring Data JPA
- Spring Security
- JWT (jjwt 0.11.5)
- PostgreSQL
- Redis (embedded-redis 포함)
- Spring Mail
- OpenAPI (springdoc)

### View
- Thymeleaf
- thymeleaf-layout-dialect

---

## API Documentation
로컬 실행 후 Swagger UI를 통해 API 명세를 확인할 수 있습니다.
- Swagger UI: http://localhost:8080/swagger-ui/index.html
- OpenAPI JSON: http://localhost:8080/v3/api-docs

---

## Architecture
- 인증/보안: Spring Security + JWT 기반 토큰 인증 구조로 API 접근 제어
- 데이터: PostgreSQL + JPA로 도메인 중심 데이터 설계 및 영속성 관리
- 캐시/단기 저장: Redis를 활용해 인증 및 단기 데이터를 효율적으로 처리
- 외부 연동: 메일 인증 및 Kakao OAuth를 통해 인증 편의성 강화
- 문서화: OpenAPI(Swagger) 기반 API 명세화

---

## Project Structure
```text
Big-deal
├─ build.gradle
└─ src
   └─ main
      ├─ java
      │  ├─ controller
      │  ├─ service
      │  ├─ repository
      │  ├─ domain
      │  └─ security
      └─ resources
         ├─ application.yml
         └─ templates

---

##Build & Run

###Backend (Dev)
```bash
./gradlew bootRun
```
