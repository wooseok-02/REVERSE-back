# REVERSE 백엔드 API 명세서

- **Base URL**: `http://localhost:8080`
- **응답 형식**: JSON (이미지 업로드 응답은 plain text)
- **작성일**: 2026.04.09
- **최종 수정일**: 2026.04.13

---

## 목차

1. [서버 상태](#1-서버-상태)
2. [인증 (Auth)](#2-인증-auth)
3. [동아리 소개 (Club Intro)](#3-동아리-소개-club-intro)
4. [프로젝트 소개 (Club Project)](#4-프로젝트-소개-club-project)
5. [임원진 소개 (Officer)](#5-임원진-소개-officer)
6. [약관 (Terms)](#6-약관-terms)
7. [R2 스토리지 테스트 (R2)](#7-r2-스토리지-테스트-r2)

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
