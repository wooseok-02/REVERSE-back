# REVERSE 백엔드 API 명세서

- **Base URL**: `http://localhost:8080`
- **응답 형식**: JSON (이미지 업로드 응답은 plain text)
- **작성일**: 2026.04.09
- **최종 수정일**: 2026.04.21 (회원가입 이메일 인증 및 약관동의 추가)

---

## 목차

1. [서버 상태](#1-서버-상태)
2. [인증 (Auth)](#2-인증-auth)
3. [동아리 소개 (Club Intro)](#3-동아리-소개-club-intro)
4. [프로젝트 소개 (Club Project)](#4-프로젝트-소개-club-project)
5. [임원진 소개 (Officer)](#5-임원진-소개-officer)
6. [약관 (Terms)](#6-약관-terms)
7. [R2 스토리지 테스트 (R2)](#7-r2-스토리지-테스트-r2)
8. [모집 공고 (Recruitment)](#8-모집-공고-recruitment)
9. [일정 (Schedule)](#9-일정-schedule)
10. [공휴일 (Holiday)](#10-공휴일-holiday)

---

## 1. 서버 상태

### GET /test
서버 동작 여부를 확인한다.

- **응답**: `200 OK` — HTML 페이지 반환

---

## 2. 인증 (Auth)

Base Path: `/api/auth`

> 액세스 토큰 유효기간: **5분** / 리프레시 토큰 유효기간: **20분**
> 서명 알고리즘: **HS256** (secret + salt SHA-256 파생 키)

---

### POST /api/auth/email/send
인증번호를 이메일로 전송한다. 기존 미인증 코드는 자동으로 폐기된다.
인증번호 유효시간은 **5분**이다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `email` | String | Y | 인증번호를 받을 이메일 주소 |

```json
{ "email": "hong@example.com" }
```

**응답 `200 OK`** (plain text)
```
인증번호가 전송되었습니다.
```

---

### POST /api/auth/email/resend
인증번호를 재전송한다. 이전 코드는 폐기되고 새 코드가 발급된다.

**요청 Body** — `POST /api/auth/email/send`와 동일

**응답 `200 OK`** (plain text)
```
인증번호가 재전송되었습니다.
```

---

### POST /api/auth/email/verify
입력한 인증번호가 맞는지 확인한다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `email` | String | Y | 인증 대상 이메일 |
| `code` | String | Y | 수신한 6자리 인증번호 |

```json
{
  "email": "hong@example.com",
  "code": "382910"
}
```

**응답 `200 OK`** (plain text)
```
이메일 인증이 완료되었습니다.
```

**에러 응답**
| 상황 | HTTP 상태 |
|---|---|
| 인증번호 없음 | `500` — 인증번호를 찾을 수 없습니다. |
| 인증번호 만료 | `500` — 인증번호가 만료되었습니다. |
| 인증번호 불일치 | `500` — 인증번호가 올바르지 않습니다. |

---

### POST /api/auth/register
이메일 인증 완료 후 신규 계정을 생성한다.
가입 즉시 `GUEST` 역할이 부여된다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `userId` | String | Y | 사용자 ID (최대 15자, 중복 불가) |
| `userName` | String | Y | 사용자 이름 (최대 34자) |
| `userEmail` | String | Y | 인증 완료된 이메일 |
| `userPassword` | String | Y | 비밀번호 (BCrypt 해시로 저장) |
| `userIntroduce` | String | N | 자기소개 (최대 100자) |
| `userMbti` | String | N | MBTI (4자) |
| `consents` | List | Y | 약관 동의 목록 |
| `consents[].consentItemId` | Integer | Y | 약관 항목 ID |
| `consents[].isAgreed` | Boolean | Y | 동의 여부 |

```json
{
  "userId": "user01",
  "userName": "홍길동",
  "userEmail": "hong@example.com",
  "userPassword": "plainPassword",
  "userIntroduce": "안녕하세요!",
  "userMbti": "INFP",
  "consents": [
    { "consentItemId": 1, "isAgreed": true },
    { "consentItemId": 2, "isAgreed": true },
    { "consentItemId": 3, "isAgreed": false },
    { "consentItemId": 4, "isAgreed": false }
  ]
}
```

> 약관 항목 ID는 `CONSENT_ITEM` 테이블 기준 (1: 이용약관, 2: 개인정보 수집 및 이용, 3: 마케팅 수신, 4: 이벤트 알림)

**응답 `200 OK`** (plain text)
```
회원가입이 완료되었습니다.
```

**에러 응답**
| 상황 | HTTP 상태 |
|---|---|
| userId 중복 | `500` — 이미 사용 중인 아이디입니다. |
| 이메일 미인증 | `500` — 이메일 인증이 완료되지 않았습니다. |
| 필수 약관 미동의 | `500` — 필수 약관에 동의해야 합니다: {약관명} |

---

### POST /api/auth/login
아이디와 비밀번호로 로그인하여 액세스 토큰과 리프레시 토큰을 발급한다.
로그인마다 새 세션이 `USER_TOKEN` 테이블에 저장된다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `userId` | String | Y | 사용자 ID (최대 15자) |
| `userPassword` | String | Y | 비밀번호 (BCrypt 해시 비교) |

```json
{
  "userId": "admin01",
  "userPassword": "plainPassword"
}
```

**응답 `200 OK`**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...",
  "accessTokenExpiry": "2026-04-13T12:05:00",
  "refreshTokenExpiry": "2026-04-13T12:20:00"
}
```

**에러 응답**
| 상황 | HTTP 상태 |
|---|---|
| 존재하지 않는 userId | `500` — 사용자를 찾을 수 없습니다. |
| 비밀번호 불일치 | `500` — 비밀번호가 올바르지 않습니다. |

---

### POST /api/auth/refresh
리프레시 토큰으로 새 액세스 토큰을 재발급한다.
리프레시 토큰이 유효하고 폐기되지 않은 경우에만 성공한다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `refreshToken` | String | Y | 로그인 시 발급받은 리프레시 토큰 |

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**응답 `200 OK`**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9...(새 토큰)",
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9...(기존 토큰 유지)",
  "accessTokenExpiry": "2026-04-13T12:10:00",
  "refreshTokenExpiry": "2026-04-13T12:20:00"
}
```

**에러 응답**
| 상황 | HTTP 상태 |
|---|---|
| 폐기된 토큰 또는 존재하지 않는 토큰 | `500` — 유효하지 않은 리프레시 토큰입니다. |
| 리프레시 토큰 만료 | `500` — 리프레시 토큰이 만료되었습니다. |

---

### POST /api/auth/logout
리프레시 토큰을 폐기하여 로그아웃 처리한다.
폐기된 토큰은 이후 재발급에 사용할 수 없다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `refreshToken` | String | Y | 폐기할 리프레시 토큰 |

```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiJ9..."
}
```

**응답 `200 OK`** (plain text)
```
로그아웃 완료
```

---

## 3. 동아리 소개 (Club Intro)

Base Path: `/api/club-intro`

---

### GET /api/club-intro
동아리 소개 전체 목록을 조회한다.

**응답 `200 OK`**
```json
[
  {
    "clubIntroId": 1,
    "title": "REVERSE",
    "subTitle": "남서울대학교 컴퓨터소프트웨어학과 동아리",
    "bannerUrl": "https://cdn.example.com/intro/banner.png",
    "isActive": true,
    "updatedBy": "admin01",
    "createdDate": "2026-04-01T12:00:00",
    "modifiedDate": "2026-04-01T12:00:00"
  }
]
```

---

### POST /api/club-intro/image
배너 이미지를 Cloudflare R2에 업로드하고 URL을 반환한다.
이미지 저장 후 반환된 URL을 `POST /api/club-intro` 요청에 사용한다.

**요청** `multipart/form-data`

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `file` | MultipartFile | Y | 업로드할 이미지 파일 |

**응답 `200 OK`** (plain text)
```
https://cdn.example.com/intro/uuid-filename.png
```

---

### POST /api/club-intro
동아리 소개 데이터를 저장한다.
이미지가 있는 경우 `POST /api/club-intro/image`를 먼저 호출해 URL을 얻은 뒤 요청한다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `title` | String | Y | 동아리 이름 |
| `subTitle` | String | Y | 부제목 |
| `bannerUrl` | String | N | 이미지 업로드 후 받은 URL |
| `isActive` | Boolean | N | 노출 여부 (기본값: `true`) |
| `updatedBy` | String | Y | 등록 관리자 ID |

```json
{
  "title": "REVERSE",
  "subTitle": "남서울대학교 컴퓨터소프트웨어학과 동아리",
  "bannerUrl": "https://cdn.example.com/intro/uuid-filename.png",
  "isActive": true,
  "updatedBy": "admin01"
}
```

**응답 `200 OK`**
```json
{
  "clubIntroId": 1,
  "title": "REVERSE",
  "subTitle": "남서울대학교 컴퓨터소프트웨어학과 동아리",
  "bannerUrl": "https://cdn.example.com/intro/uuid-filename.png",
  "isActive": true,
  "updatedBy": "admin01",
  "createdDate": "2026-04-01T12:00:00",
  "modifiedDate": "2026-04-01T12:00:00"
}
```

---

### PUT /api/club-intro/{id}/image
수정할 배너 이미지를 Cloudflare R2에 업로드하고 URL을 반환한다.
이미지를 변경하는 경우에만 호출하며, 반환된 URL을 `PUT /api/club-intro/{id}` 요청에 사용한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 수정할 동아리 소개 ID |

**요청** `multipart/form-data`

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `file` | MultipartFile | Y | 업로드할 이미지 파일 |

**응답 `200 OK`** (plain text)
```
https://cdn.example.com/intro/uuid-filename.png
```

---

### PUT /api/club-intro/{id}
동아리 소개 데이터를 수정한다.
이미지를 변경한 경우 기존 R2 이미지는 자동으로 삭제된다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 수정할 동아리 소개 ID |

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `title` | String | Y | 동아리 이름 |
| `subTitle` | String | Y | 부제목 |
| `bannerUrl` | String | N | 이미지 업로드 후 받은 URL (변경 없으면 기존 URL 그대로 전달) |
| `isActive` | Boolean | N | 노출 여부 |
| `updatedBy` | String | Y | 수정 관리자 ID |

```json
{
  "title": "REVERSE",
  "subTitle": "남서울대학교 컴퓨터소프트웨어학과 동아리 (수정)",
  "bannerUrl": "https://cdn.example.com/intro/uuid-filename.png",
  "isActive": true,
  "updatedBy": "admin01"
}
```

**응답 `200 OK`**
```json
{
  "clubIntroId": 1,
  "title": "REVERSE",
  "subTitle": "남서울대학교 컴퓨터소프트웨어학과 동아리 (수정)",
  "bannerUrl": "https://cdn.example.com/intro/uuid-filename.png",
  "isActive": true,
  "updatedBy": "admin01",
  "createdDate": "2026-04-01T12:00:00",
  "modifiedDate": "2026-04-09T12:00:00"
}
```

---

### DELETE /api/club-intro/{id}
동아리 소개 데이터를 삭제한다. R2에 저장된 이미지도 함께 삭제된다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 삭제할 동아리 소개 ID |

**응답 `200 OK`** (plain text)
```
삭제 완료: 1
```

---

## 4. 프로젝트 소개 (Club Project)

Base Path: `/api/club-project`

---

### GET /api/club-project
프로젝트 목록을 `sortOrder` 오름차순으로 전체 조회한다.

**응답 `200 OK`**
```json
[
  {
    "projectId": 1,
    "sortOrder": 0,
    "projectName": "REVERSE 웹사이트",
    "thumbnailUrl": "https://cdn.example.com/project/uuid-filename.png",
    "projectUrl": "https://github.com/example/reverse",
    "updatedBy": "admin01",
    "createdDate": "2026-04-01T12:00:00",
    "modifiedDate": "2026-04-01T12:00:00"
  }
]
```

---

### POST /api/club-project/image
프로젝트 썸네일 이미지를 Cloudflare R2에 업로드하고 URL을 반환한다.
이미지 저장 후 반환된 URL을 `POST /api/club-project` 요청에 사용한다.

**요청** `multipart/form-data`

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `file` | MultipartFile | Y | 업로드할 이미지 파일 |

**응답 `200 OK`** (plain text)
```
https://cdn.example.com/project/uuid-filename.png
```

---

### POST /api/club-project
프로젝트 데이터를 저장한다.
이미지가 있는 경우 `POST /api/club-project/image`를 먼저 호출해 URL을 얻은 뒤 요청한다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `projectName` | String | Y | 프로젝트 이름 |
| `thumbnailUrl` | String | N | 이미지 업로드 후 받은 URL |
| `projectUrl` | String | N | 외부 링크 (GitHub, 배포 URL 등) |
| `sortOrder` | Integer | N | 노출 순서 (기본값: `0`, 낮을수록 상단) |
| `updatedBy` | String | Y | 등록 관리자 ID |

```json
{
  "projectName": "REVERSE 웹사이트",
  "thumbnailUrl": "https://cdn.example.com/project/uuid-filename.png",
  "projectUrl": "https://github.com/example/reverse",
  "sortOrder": 0,
  "updatedBy": "admin01"
}
```

**응답 `200 OK`**
```json
{
  "projectId": 1,
  "sortOrder": 0,
  "projectName": "REVERSE 웹사이트",
  "thumbnailUrl": "https://cdn.example.com/project/uuid-filename.png",
  "projectUrl": "https://github.com/example/reverse",
  "updatedBy": "admin01",
  "createdDate": "2026-04-01T12:00:00",
  "modifiedDate": "2026-04-01T12:00:00"
}
```

---

### PUT /api/club-project/{id}/image
수정할 썸네일 이미지를 Cloudflare R2에 업로드하고 URL을 반환한다.
이미지를 변경하는 경우에만 호출하며, 반환된 URL을 `PUT /api/club-project/{id}` 요청에 사용한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 수정할 프로젝트 ID |

**요청** `multipart/form-data`

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `file` | MultipartFile | Y | 업로드할 이미지 파일 |

**응답 `200 OK`** (plain text)
```
https://cdn.example.com/project/uuid-filename.png
```

---

### PUT /api/club-project/{id}
프로젝트 데이터를 수정한다.
이미지를 변경한 경우 기존 R2 이미지는 자동으로 삭제된다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 수정할 프로젝트 ID |

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `projectName` | String | Y | 프로젝트 이름 |
| `thumbnailUrl` | String | N | 이미지 업로드 후 받은 URL (변경 없으면 기존 URL 그대로 전달) |
| `projectUrl` | String | N | 외부 링크 (GitHub, 배포 URL 등) |
| `sortOrder` | Integer | N | 노출 순서 |
| `updatedBy` | String | Y | 수정 관리자 ID |

```json
{
  "projectName": "REVERSE 웹사이트 (수정)",
  "thumbnailUrl": "https://cdn.example.com/project/uuid-filename.png",
  "projectUrl": "https://github.com/example/reverse",
  "sortOrder": 0,
  "updatedBy": "admin01"
}
```

**응답 `200 OK`**
```json
{
  "projectId": 1,
  "sortOrder": 0,
  "projectName": "REVERSE 웹사이트 (수정)",
  "thumbnailUrl": "https://cdn.example.com/project/uuid-filename.png",
  "projectUrl": "https://github.com/example/reverse",
  "updatedBy": "admin01",
  "createdDate": "2026-04-01T12:00:00",
  "modifiedDate": "2026-04-09T12:00:00"
}
```

---

### DELETE /api/club-project/{id}
프로젝트 데이터를 삭제한다. R2에 저장된 썸네일 이미지도 함께 삭제된다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 삭제할 프로젝트 ID |

**응답 `200 OK`** (plain text)
```
삭제 완료: 1
```

---

## 5. 임원진 소개 (Officer)

Base Path: `/api/officer`

---

### GET /api/officer
임원진 전체 목록을 조회한다.

**응답 `200 OK`**
```json
[
  {
    "officerId": 1,
    "name": "홍길동",
    "generation": 5,
    "role": "회장",
    "department": "개발",
    "email": "hong@example.com",
    "photoUrl": "https://cdn.example.com/officer/uuid-filename.png",
    "sortOrder": 0,
    "isVisible": true,
    "updatedBy": "admin01",
    "createdDate": "2026-04-01T12:00:00",
    "modifiedDate": "2026-04-01T12:00:00"
  }
]
```

---

### POST /api/officer/image
임원진 프로필 사진을 Cloudflare R2에 업로드하고 URL을 반환한다.
이미지 저장 후 반환된 URL을 `POST /api/officer` 요청에 사용한다.

**요청** `multipart/form-data`

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `file` | MultipartFile | Y | 업로드할 이미지 파일 |

**응답 `200 OK`** (plain text)
```
https://cdn.example.com/officer/uuid-filename.png
```

---

### POST /api/officer
임원진 데이터를 저장한다.
이미지가 있는 경우 `POST /api/officer/image`를 먼저 호출해 URL을 얻은 뒤 요청한다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `name` | String | Y | 이름 |
| `generation` | Integer | Y | 기수 (예: `5`) |
| `role` | String | Y | 직책 (예: `회장`, `개발팀장`) |
| `department` | String | N | 소속 파트 (예: `개발`, `디자인`) |
| `email` | String | N | 연락용 이메일 |
| `photoUrl` | String | N | 이미지 업로드 후 받은 URL |
| `sortOrder` | Integer | N | 노출 순서 (기본값: `0`, 낮을수록 상단) |
| `isVisible` | Boolean | N | 노출 여부 (기본값: `true`) |
| `updatedBy` | String | Y | 등록 관리자 ID |

```json
{
  "name": "홍길동",
  "generation": 5,
  "role": "회장",
  "department": "개발",
  "email": "hong@example.com",
  "photoUrl": "https://cdn.example.com/officer/uuid-filename.png",
  "sortOrder": 0,
  "isVisible": true,
  "updatedBy": "admin01"
}
```

**응답 `200 OK`**
```json
{
  "officerId": 1,
  "name": "홍길동",
  "generation": 5,
  "role": "회장",
  "department": "개발",
  "email": "hong@example.com",
  "photoUrl": "https://cdn.example.com/officer/uuid-filename.png",
  "sortOrder": 0,
  "isVisible": true,
  "updatedBy": "admin01",
  "createdDate": "2026-04-01T12:00:00",
  "modifiedDate": "2026-04-01T12:00:00"
}
```

---

### PUT /api/officer/{id}/image
수정할 프로필 사진을 Cloudflare R2에 업로드하고 URL을 반환한다.
이미지를 변경하는 경우에만 호출하며, 반환된 URL을 `PUT /api/officer/{id}` 요청에 사용한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 수정할 임원진 ID |

**요청** `multipart/form-data`

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `file` | MultipartFile | Y | 업로드할 이미지 파일 |

**응답 `200 OK`** (plain text)
```
https://cdn.example.com/officer/uuid-filename.png
```

---

### PUT /api/officer/{id}
임원진 데이터를 수정한다.
이미지를 변경한 경우 기존 R2 이미지는 자동으로 삭제된다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 수정할 임원진 ID |

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `name` | String | Y | 이름 |
| `generation` | Integer | Y | 기수 |
| `role` | String | Y | 직책 |
| `department` | String | N | 소속 파트 |
| `email` | String | N | 연락용 이메일 |
| `photoUrl` | String | N | 이미지 업로드 후 받은 URL (변경 없으면 기존 URL 그대로 전달) |
| `sortOrder` | Integer | N | 노출 순서 |
| `isVisible` | Boolean | N | 노출 여부 |
| `updatedBy` | String | Y | 수정 관리자 ID |

```json
{
  "name": "홍길동",
  "generation": 5,
  "role": "부회장",
  "department": "개발",
  "email": "hong@example.com",
  "photoUrl": "https://cdn.example.com/officer/uuid-filename.png",
  "sortOrder": 1,
  "isVisible": true,
  "updatedBy": "admin01"
}
```

**응답 `200 OK`**
```json
{
  "officerId": 1,
  "name": "홍길동",
  "generation": 5,
  "role": "부회장",
  "department": "개발",
  "email": "hong@example.com",
  "photoUrl": "https://cdn.example.com/officer/uuid-filename.png",
  "sortOrder": 1,
  "isVisible": true,
  "updatedBy": "admin01",
  "createdDate": "2026-04-01T12:00:00",
  "modifiedDate": "2026-04-09T12:00:00"
}
```

---

### DELETE /api/officer/{id}
임원진 데이터를 삭제한다. R2에 저장된 프로필 사진도 함께 삭제된다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 삭제할 임원진 ID |

**응답 `200 OK`** (plain text)
```
삭제 완료: 1
```

---

## 6. 약관 (Terms)

Base Path: `/api/terms`

---

### GET /api/terms
약관 전체 목록을 조회한다.

**응답 `200 OK`**
```json
[
  {
    "termsId": 1,
    "sortOrder": 0,
    "version": "v1.0",
    "title": "개인정보 처리방침",
    "contents": "...",
    "isCurrent": true,
    "updatedBy": "admin01",
    "createdDate": "2026-04-01T12:00:00"
  }
]
```

---

### POST /api/terms
새 약관을 저장한다. 약관은 수정 없이 새 버전을 등록하는 방식으로 관리한다.
`version` 값은 중복 불가 (UNIQUE 제약).

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `sortOrder` | Integer | N | 조항 노출 순서 |
| `version` | String | Y | 약관 버전 (예: `v1.0`) — 중복 불가 |
| `title` | String | Y | 약관 제목 |
| `contents` | String | Y | 약관 본문 |
| `isCurrent` | Boolean | N | 현재 적용 버전 여부 (기본값: `false`) |
| `updatedBy` | String | Y | 등록 관리자 ID |

```json
{
  "sortOrder": 0,
  "version": "v1.0",
  "title": "개인정보 처리방침",
  "contents": "...",
  "isCurrent": true,
  "updatedBy": "admin01"
}
```

**응답 `200 OK`**
```json
{
  "termsId": 1,
  "sortOrder": 0,
  "version": "v1.0",
  "title": "개인정보 처리방침",
  "contents": "...",
  "isCurrent": true,
  "updatedBy": "admin01",
  "createdDate": "2026-04-01T12:00:00"
}
```

---

### PUT /api/terms/{id}
약관 내용을 수정한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 수정할 약관 ID |

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `sortOrder` | Integer | N | 조항 노출 순서 |
| `version` | String | Y | 약관 버전 |
| `title` | String | Y | 약관 제목 |
| `contents` | String | Y | 약관 본문 |
| `isCurrent` | Boolean | N | 현재 적용 버전 여부 |

```json
{
  "sortOrder": 0,
  "version": "v1.0",
  "title": "개인정보 처리방침 (수정)",
  "contents": "...",
  "isCurrent": true
}
```

**응답 `200 OK`**
```json
{
  "termsId": 1,
  "sortOrder": 0,
  "version": "v1.0",
  "title": "개인정보 처리방침 (수정)",
  "contents": "...",
  "isCurrent": true,
  "updatedBy": "admin01",
  "createdDate": "2026-04-01T12:00:00"
}
```

---

### DELETE /api/terms/{id}
약관을 삭제한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Long | 삭제할 약관 ID |

**응답 `200 OK`** (plain text)
```
1번 약관이 성공적으로 삭제되었습니다.
```

---

## 7. R2 스토리지 테스트 (R2)

Base Path: `/api/r2`

> 개발/테스트 용도의 엔드포인트입니다. 운영 환경에서는 사용하지 않습니다.

---

### POST /api/r2/upload
이미지를 Cloudflare R2의 지정 폴더에 업로드한다.

**요청** `multipart/form-data`

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `file` | MultipartFile | Y | 업로드할 파일 |
| `folder` | String | N | 저장할 폴더명 (기본값: `test`) |

**응답 `200 OK`** (plain text)
```
https://cdn.example.com/test/uuid-filename.png
```

---

### DELETE /api/r2/delete
R2에 저장된 파일을 URL로 삭제한다.

**Query Parameter**

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `fileUrl` | String | Y | 삭제할 파일의 전체 URL |

**응답 `200 OK`** (plain text)
```
삭제 완료: https://cdn.example.com/test/uuid-filename.png
```

---

## 8. 모집 공고 (Recruitment)

Base Path: `/api/recruit`

---

### GET /api/recruit
모집 공고 전체 목록을 조회한다.

**응답 `200 OK`**
```json
[
  {
    "id": 1,
    "title": "2026년 1학기 신입 부원 모집",
    "description": "REVERSE 동아리 신입 부원을 모집합니다.",
    "isActive": true,
    "createdAt": "2026-04-01T12:00:00",
    "updatedAt": "2026-04-01T12:00:00"
  }
]
```

---

### GET /api/recruit/{id}
모집 공고 단건을 상세 조회한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Integer | 조회할 공고 ID |

**응답 `200 OK`**
```json
{
  "id": 1,
  "title": "2026년 1학기 신입 부원 모집",
  "description": "REVERSE 동아리 신입 부원을 모집합니다.",
  "isActive": true,
  "createdAt": "2026-04-01T12:00:00",
  "updatedAt": "2026-04-01T12:00:00"
}
```

---

### POST /api/recruit
모집 공고를 등록한다. 등록 시 `isActive`는 항상 `true`로 저장된다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `title` | String | Y | 공고 제목 (최대 100자) |
| `description` | String | N | 공고 본문 |
| `applyStartDate` | LocalDateTime | Y | 지원 시작일시 |
| `applyEndDate` | LocalDateTime | Y | 지원 마감일시 |
| `updatedBy` | String | Y | 등록 관리자 ID (최대 15자) |

```json
{
  "title": "2026년 1학기 신입 부원 모집",
  "description": "REVERSE 동아리 신입 부원을 모집합니다.",
  "applyStartDate": "2026-04-01T00:00:00",
  "applyEndDate": "2026-04-30T23:59:59",
  "updatedBy": "admin01"
}
```

**응답 `200 OK`**
```json
{
  "id": 1,
  "title": "2026년 1학기 신입 부원 모집",
  "description": "REVERSE 동아리 신입 부원을 모집합니다.",
  "isActive": true,
  "createdAt": "2026-04-01T12:00:00",
  "updatedAt": "2026-04-01T12:00:00"
}
```

---

### PUT /api/recruit/{id}
모집 공고 내용을 수정한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Integer | 수정할 공고 ID |

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `title` | String | Y | 공고 제목 |
| `description` | String | N | 공고 본문 |
| `applyStartDate` | LocalDateTime | Y | 지원 시작일시 |
| `applyEndDate` | LocalDateTime | Y | 지원 마감일시 |
| `updatedBy` | String | Y | 수정 관리자 ID |

**응답 `200 OK`**
```json
{
  "id": 1,
  "title": "2026년 1학기 신입 부원 모집 (수정)",
  "description": "REVERSE 동아리 신입 부원을 모집합니다.",
  "isActive": true,
  "createdAt": "2026-04-01T12:00:00",
  "updatedAt": "2026-04-13T12:00:00"
}
```

---

### DELETE /api/recruit/{id}
모집 공고를 삭제한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Integer | 삭제할 공고 ID |

**응답 `200 OK`** (plain text)
```
성공적으로 삭제되었습니다. ID: 1
```

---

### PATCH /api/recruit/admin/{id}/status
모집 공고의 활성화 상태(`isActive`)를 변경한다. 관리자 권한이 필요하다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Integer | 상태를 변경할 공고 ID |

**Query Parameter**

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `roleId` | Integer | Y | 요청자의 역할 ID (권한 검증에 사용) |

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `isActive` | Boolean | Y | 변경할 활성화 상태 |

```json
{
  "isActive": false
}
```

**응답 `200 OK`**
```json
{
  "status": "success",
  "message": "모집 상태가 변경되었습니다.",
  "data": {
    "id": 1,
    "isActive": false
  }
}
```

---

### POST /api/recruit/apply
모집 공고에 지원서를 제출한다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `recruitmentId` | Integer | Y | 지원할 공고 ID |
| `applicantName` | String | Y | 지원자 이름 |
| `department` | String | Y | 학과 |
| `studentNumber` | String | Y | 학번 |
| `phoneNumber` | String | Y | 전화번호 |
| `grade` | Byte | Y | 학년 |
| `email` | String | Y | 이메일 |
| `termsAgreed` | Boolean | Y | 약관 동의 여부 |
| `applyFields` | List\<String\> | Y | 지원 분야 목록 (예: `["메인프로젝트", "스터디"]`) |

```json
{
  "recruitmentId": 1,
  "applicantName": "홍길동",
  "department": "컴퓨터소프트웨어학과",
  "studentNumber": "20210001",
  "phoneNumber": "010-1234-5678",
  "grade": 3,
  "email": "hong@example.com",
  "termsAgreed": true,
  "applyFields": ["메인프로젝트", "스터디"]
}
```

**응답 `200 OK`** (plain text)
```
지원서가 성공적으로 제출되었습니다.
```

---

### POST /api/recruit/notify
새 모집 공고 알림 이메일을 구독한다.

**Query Parameter**

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `email` | String | Y | 알림을 받을 이메일 주소 |

**응답 `200 OK`** (plain text)
```
공고 알림 구독이 완료되었습니다.
```

---

---

## 9. 일정 (Schedule)

Base Path: `/api/schedule`

---

### GET /api/schedule/category
노출 중인 카테고리 목록을 `sortOrder` 오름차순으로 조회한다.

**응답 `200 OK`**
```json
[
  {
    "id": 1,
    "categoryName": "동아리",
    "colorCode": "#FF5733",
    "sortOrder": 0,
    "isVisible": true,
    "updatedBy": "admin01",
    "createdAt": "2026-04-01T12:00:00",
    "updatedAt": "2026-04-01T12:00:00"
  }
]
```

---

### GET /api/schedule
월별 공개 일정 목록을 조회한다. `year`, `month` 파라미터 생략 시 현재 연월로 조회한다.

**Query Parameter**

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `year` | Integer | N | 조회 연도 (기본값: 현재 연도) |
| `month` | Integer | N | 조회 월 (기본값: 현재 월) |

**응답 `200 OK`**
```json
[
  {
    "id": 1,
    "categoryId": 1,
    "categoryName": "동아리",
    "colorCode": "#FF5733",
    "title": "동아리 OT",
    "description": "2026년 1학기 OT",
    "startDate": "2026-04-05",
    "endDate": "2026-04-05",
    "startTime": "14:00:00",
    "endTime": "17:00:00",
    "isAllDay": false,
    "isVisible": true,
    "updatedBy": "admin01",
    "createdAt": "2026-04-01T12:00:00",
    "updatedAt": "2026-04-01T12:00:00"
  }
]
```

---

### GET /api/schedule/{id}
일정 단건을 조회한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Integer | 조회할 일정 ID |

**응답 `200 OK`** — 위 목록 응답의 단건 형태와 동일

---

### GET /api/schedule/admin/category
카테고리 전체 목록을 조회한다 (숨김 포함).

**응답 `200 OK`** — GET /api/schedule/category 응답과 동일한 구조

---

### POST /api/schedule/admin/category
카테고리를 생성한다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `categoryName` | String | Y | 카테고리 이름 (최대 30자) |
| `colorCode` | String | N | HEX 색상코드 (기본값: `#FFFFFF`) |
| `sortOrder` | Integer | N | 노출 순서 (기본값: `0`) |
| `isVisible` | Boolean | N | 노출 여부 (기본값: `true`) |
| `updatedBy` | String | Y | 등록 관리자 ID |

```json
{
  "categoryName": "동아리",
  "colorCode": "#FF5733",
  "sortOrder": 0,
  "isVisible": true,
  "updatedBy": "admin01"
}
```

**응답 `200 OK`** — 생성된 카테고리 단건 반환

---

### PUT /api/schedule/admin/category/{id}
카테고리를 수정한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Integer | 수정할 카테고리 ID |

**요청 Body** `application/json` — POST 요청과 동일한 구조

**응답 `200 OK`** — 수정된 카테고리 단건 반환

---

### DELETE /api/schedule/admin/category/{id}
카테고리를 삭제한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Integer | 삭제할 카테고리 ID |

**응답 `200 OK`** (plain text)
```
카테고리가 삭제되었습니다. ID: 1
```

---

### GET /api/schedule/admin
월별 전체 일정을 조회한다 (숨김 포함).

**Query Parameter**

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `year` | Integer | Y | 조회 연도 |
| `month` | Integer | Y | 조회 월 |

**응답 `200 OK`** — GET /api/schedule 응답과 동일한 구조

---

### POST /api/schedule/admin
일정을 생성한다.

**요청 Body** `application/json`

| 필드 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `categoryId` | Integer | Y | 카테고리 ID |
| `title` | String | Y | 일정 제목 (최대 100자) |
| `description` | String | N | 일정 상세 설명 |
| `startDate` | LocalDate | Y | 시작일 (`yyyy-MM-dd`) |
| `endDate` | LocalDate | Y | 종료일 (`yyyy-MM-dd`, 단일 일정이면 startDate와 동일) |
| `startTime` | LocalTime | N | 시작 시간 (`HH:mm:ss`, 종일이면 생략) |
| `endTime` | LocalTime | N | 종료 시간 (`HH:mm:ss`, 종일이면 생략) |
| `isAllDay` | Boolean | N | 종일 일정 여부 (기본값: `true`) |
| `isVisible` | Boolean | N | 노출 여부 (기본값: `true`) |
| `updatedBy` | String | Y | 등록 관리자 ID |

```json
{
  "categoryId": 1,
  "title": "동아리 OT",
  "description": "2026년 1학기 OT",
  "startDate": "2026-04-05",
  "endDate": "2026-04-05",
  "startTime": "14:00:00",
  "endTime": "17:00:00",
  "isAllDay": false,
  "isVisible": true,
  "updatedBy": "admin01"
}
```

**응답 `200 OK`** — 생성된 일정 단건 반환

---

### PUT /api/schedule/admin/{id}
일정을 수정한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Integer | 수정할 일정 ID |

**요청 Body** `application/json` — POST 요청과 동일한 구조

**응답 `200 OK`** — 수정된 일정 단건 반환

---

### DELETE /api/schedule/admin/{id}
일정을 삭제한다.

**Path Variable**

| 파라미터 | 타입 | 설명 |
|---|---|---|
| `id` | Integer | 삭제할 일정 ID |

**응답 `200 OK`** (plain text)
```
일정이 삭제되었습니다. ID: 1
```

---

## 10. 공휴일 (Holiday)

Base Path: `/api/holiday`

> 공공데이터포털 "한국천문연구원 특일 정보" API 연동.
> 매년 1월 1일 00:05 자동 갱신되며, 수동 동기화도 가능하다.

---

### GET /api/holiday
월별 공휴일 목록을 조회한다.

**Query Parameter**

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `year` | Integer | Y | 조회 연도 |
| `month` | Integer | Y | 조회 월 |

**응답 `200 OK`**
```json
[
  {
    "id": 1,
    "holidayDate": "2026-03-01",
    "holidayName": "삼일절",
    "isHoliday": true,
    "year": 2026
  }
]
```

---

### POST /api/holiday/admin/sync
특정 연도의 공휴일을 공공 API에서 수동으로 동기화한다.
기존 데이터를 삭제 후 재저장한다.

**Query Parameter**

| 파라미터 | 타입 | 필수 | 설명 |
|---|---|---|---|
| `year` | Integer | Y | 동기화할 연도 |

**응답 `200 OK`**
```json
{
  "status": "success",
  "message": "2026년 공휴일 동기화가 완료되었습니다."
}
```

---

## 이미지 포함 데이터 등록/수정 플로우

이미지가 있는 도메인(동아리 소개, 프로젝트, 임원진)은 2단계로 등록 및 수정한다.

**등록**
```
1. POST /api/{domain}/image      → 이미지 업로드 → URL 반환
2. POST /api/{domain}            → URL 포함한 전체 데이터 저장
```

**수정**
```
1. PUT /api/{domain}/{id}/image  → 새 이미지 업로드 → URL 반환  (이미지 변경 시에만)
2. PUT /api/{domain}/{id}        → URL 포함한 전체 데이터 수정
                                    (이미지 미변경 시 기존 URL 그대로 전달)
```

> 이미지 URL이 변경된 경우, 기존 R2 이미지는 수정 요청 처리 시 자동으로 삭제된다.
