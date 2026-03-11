# CMS REST API

## 기술 스택

| 항목 | 내용 |
|---|---|
| Language | Java 25 |
| Framework | Spring Boot 4.0.3 |
| Security | Spring Security + JWT (JJWT 0.12.6) |
| ORM | Spring Data JPA + QueryDSL 7.1 |
| DB | H2 (In-Memory) |
| Build | Gradle |
| 기타 | Lombok, p6spy, Spring Validation, Spring Actuator |

---

## 프로젝트 실행 방법

### 1. 실행

```bash
./gradlew bootRun
```

- 서버 포트: `8080`
- 애플리케이션 기동 시 `h2-schema.sql`, `h2-data.sql`이 자동 실행되어 초기 데이터가 삽입됩니다.

### 2. H2 콘솔 접속

- URL: `http://localhost:8080/h2-console`
- JDBC URL: `jdbc:h2:mem:test`
- Username: `sa`
- Password: (없음)

### 3. 테스트 계정 (초기 데이터)

| username | password | role |
|---|---|---|
| admin | admin1234 | ADMIN |
| user1 | user1234 | USER |

---

## 구현 내용

### 로그인 방식

**JWT 기반 Stateless 인증**을 사용했습니다.

- 로그인 성공 시 `Access Token`과 `Refresh Token`을 발급합니다.
- 이후 모든 요청은 `Authorization: Bearer {accessToken}` 헤더를 포함해야 합니다.
- Access Token 만료 시 Refresh Token으로 재발급할 수 있습니다.
- 세션을 사용하지 않으며, 서버는 토큰만으로 인증합니다.

### 접근 권한

- 콘텐츠 수정 및 삭제는 **작성자 본인**과 **ADMIN**만 가능합니다.
- 인증 없이 콘텐츠 API 접근 시 `401 Unauthorized`를 반환합니다.
- 권한 없이 타인의 콘텐츠 수정/삭제 시 `403 Forbidden`을 반환합니다.

### 추가 구현 사항

- **Soft Delete**: 콘텐츠 삭제 시 `deleted_at` 필드에 삭제 시각을 기록하며 실제 데이터는 보존합니다. 이미 삭제된 콘텐츠 재삭제 시 `400`을 반환합니다.
- **조회수 증가**: 콘텐츠 상세 조회 시 `view_count`가 1씩 증가합니다.
- **제목 검색**: 콘텐츠 목록 조회 시 `title` 파라미터로 부분 일치 검색이 가능합니다.
- **QueryDSL**: 동적 쿼리 생성에 QueryDSL을 적용했습니다.
- **Refresh Token**: Access Token 만료 시 Refresh Token을 통한 재발급 API를 구현했습니다.
- **회원가입 API**: `POST /api/users`로 신규 계정 등록이 가능합니다.
- **입력값 유효성 검사**: 요청 DTO에 `@Valid`를 적용하여 빈 제목 등 유효하지 않은 요청을 `400`으로 처리합니다.
- **공통 응답 형식**: `ApiResponse<T>` 래퍼 클래스로 일관된 응답 구조를 제공합니다.
- **전역 예외 처리**: `GlobalExceptionHandler`와 `BusinessException`으로 예외를 일관되게 처리합니다.

---

## REST API 명세

### 공통

- Base URL: `http://localhost:8080`
- 인증이 필요한 API: `Authorization: Bearer {accessToken}` 헤더 필수
- 공통 성공 응답:
```json
{
  "success": true,
  "data": { }
}
```
- 공통 실패 응답:
```json
{
  "success": false,
  "message": "에러 메시지",
  "status": 404
}
```

---

### 인증 (Auth)

#### 로그인

```
POST /api/auth/login
```

Request Body:
```json
{
  "username": "admin",
  "password": "admin1234"
}
```

Response (`200 OK`):
```json
{
  "success": true,
  "data": {
    "accessToken": "eyJhbGci...",
    "refreshToken": "eyJhbGci..."
  }
}
```

| 에러 | HTTP Status | 설명 |
|---|---|---|
| - | 401 | 아이디 또는 비밀번호 불일치 |

---

#### Access Token 재발급

```
POST /api/auth/refresh
Refresh-Token: {refreshToken}
```

Response (`200 OK`):
```json
{
  "success": true,
  "data": "eyJhbGci..."
}
```

| 에러 | HTTP Status | 설명 |
|---|---|---|
| TOKEN_EXPIRED | 401 | Refresh Token 만료 |

---

### 회원 (User)

#### 회원가입

```
POST /api/users
```

Request Body:
```json
{
  "username": "newuser",
  "password": "password123",
  "role": "USER"
}
```

> `role` 값: `USER` 또는 `ADMIN`

Response (`201 Created`):
```json
{
  "success": true,
  "data": 3
}
```

| 에러 | HTTP Status | 설명 |
|---|---|---|
| DUPLICATE_USERNAME | 409 | 이미 존재하는 아이디 |

---

### 콘텐츠 (Contents)

> 모든 콘텐츠 API는 JWT 인증이 필요합니다.

#### 콘텐츠 목록 조회 (페이징)

```
GET /api/contents?title=&page=0&size=10&sort=createdDate,desc
Authorization: Bearer {accessToken}
```

| 파라미터 | 설명 | 기본값 |
|---|---|---|
| title | 제목 부분 일치 검색 | - |
| page | 페이지 번호 (0부터) | 0 |
| size | 페이지 크기 | 10 |
| sort | 정렬 기준 | createdDate,desc |

Response (`200 OK`):
```json
{
  "success": true,
  "data": {
    "content": [
      {
        "id": 1,
        "title": "첫 번째 콘텐츠",
        "viewCount": 0,
        "createdBy": "admin",
        "createdDate": "2026-03-11T10:00:00"
      }
    ],
    "totalElements": 3,
    "totalPages": 1,
    "size": 10,
    "number": 0
  }
}
```

---

#### 콘텐츠 상세 조회

```
GET /api/contents/{contentsId}
Authorization: Bearer {accessToken}
```

> 조회 시 `view_count`가 1 증가합니다.

Response (`200 OK`):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "첫 번째 콘텐츠",
    "description": "내용입니다.",
    "viewCount": 1,
    "createdBy": "admin",
    "createdDate": "2026-03-11T10:00:00",
    "lastModifiedBy": null,
    "lastModifiedDate": null
  }
}
```

| 에러 | HTTP Status | 설명 |
|---|---|---|
| CONTENT_NOT_FOUND | 404 | 콘텐츠 없음 |
| ALREADY_DELETED | 400 | 이미 삭제된 콘텐츠 |

---

#### 콘텐츠 생성

```
POST /api/contents
Authorization: Bearer {accessToken}
```

Request Body:
```json
{
  "title": "새 콘텐츠",
  "description": "내용을 입력하세요."
}
```

Response (`201 Created`):
```json
{
  "success": true,
  "data": 4
}
```

| 에러 | HTTP Status | 설명 |
|---|---|---|
| - | 400 | 제목이 비어 있음 |

---

#### 콘텐츠 수정

```
PATCH /api/contents/{contentsId}
Authorization: Bearer {accessToken}
```

> 본인 작성 콘텐츠 또는 ADMIN만 수정 가능

Request Body:
```json
{
  "title": "수정된 제목",
  "description": "수정된 내용"
}
```

Response (`200 OK`):
```json
{
  "success": true,
  "data": {
    "id": 1,
    "title": "수정된 제목",
    "description": "수정된 내용"
  }
}
```

| 에러 | HTTP Status | 설명 |
|---|---|---|
| CONTENT_NOT_FOUND | 404 | 콘텐츠 없음 |
| UNAUTHORIZED_ACCESS | 403 | 수정 권한 없음 |
| ALREADY_DELETED | 400 | 이미 삭제된 콘텐츠 |

---

#### 콘텐츠 삭제

```
DELETE /api/contents/{contentsId}
Authorization: Bearer {accessToken}
```

> 본인 작성 콘텐츠 또는 ADMIN만 삭제 가능
> Soft Delete 방식으로 처리 (`deleted_at` 기록)

Response: `204 No Content`

| 에러 | HTTP Status | 설명 |
|---|---|---|
| CONTENT_NOT_FOUND | 404 | 콘텐츠 없음 |
| UNAUTHORIZED_ACCESS | 403 | 삭제 권한 없음 |
| ALREADY_DELETED | 400 | 이미 삭제된 콘텐츠 |

---

## 사용한 AI 도구 및 참고 자료

- **Claude Code (claude-sonnet-4-6)**: 코드 디버깅 및 README 작성에 활용했습니다.