# umc-10th-springboot-Sockbee

UMC 10기 Spring Boot 워크북 미션 레포지토리입니다.
위치 기반 미션 수행·리뷰·포인트 적립 서비스를 주제로, 도메인 중심 패키지 구조 위에서 JPA·Spring Security·JWT 인증을 단계적으로 구현합니다.

---

## 🛠 기술 스택

| 구분 | 사용 기술 |
|------|-----------|
| Language | Java 21 |
| Framework | Spring Boot 4.0.5 |
| Persistence | Spring Data JPA (Hibernate), MySQL |
| Security | Spring Security, JWT (jjwt 0.12.3) |
| OAuth | 카카오 소셜 로그인 (Authorization Code Grant) |
| API Docs | springdoc-openapi (Swagger UI) |
| Build | Gradle |

---

## 📂 프로젝트 구조

도메인별로 패키지를 나누고, 각 도메인은 `controller → service → converter → repository → entity` 계층으로 구성합니다.

```
src/main/java/com/example/umc10th
├── domain
│   ├── auth        # 회원가입 / 로그인 / 카카오 OAuth
│   ├── user        # 사용자, 마이페이지, 유저 미션
│   ├── mission     # 미션
│   ├── store       # 가게
│   ├── review      # 리뷰
│   ├── inquiry     # 문의
│   ├── term        # 약관
│   └── food        # 음식 카테고리
└── global
    ├── apiPayload  # 공통 응답(ApiResponse), 성공/에러 코드, 페이지네이션
    ├── config      # SecurityConfig, SwaggerConfig, KakaoOAuthProperties
    ├── exception   # 전역 예외 처리
    └── security    # JWT 유틸/필터, 인증 객체, 인증·인가 실패 핸들러
```

### 공통 응답 형식

모든 API는 아래 `ApiResponse` 형식으로 응답합니다.

```json
{
  "isSuccess": true,
  "code": "COMMON200_1",
  "message": "성공적으로 요청을 처리했습니다.",
  "result": { }
}
```

---

## 🔐 인증 / 인가

JWT 기반 무상태(stateless) 인증을 사용합니다. 회원가입을 제외한 모든 API는 `Authorization: Bearer {ACCESS_TOKEN}` 헤더가 필요합니다.

- **자체 로그인** — `POST /api/auth/login` 으로 이메일·비밀번호 검증 후 access token 발급
- **카카오 소셜 로그인** — `POST /api/auth/oauth/kakao` 로 인가 코드를 전달
  - 등록된 사용자면 즉시 로그인(`LOGGED_IN`)
  - 미등록 사용자면 임시 가입 토큰 발급(`NEEDS_SIGNUP`) → `POST /api/auth/oauth/kakao/signup` 으로 추가 정보와 함께 가입 완료
- access token과 임시 가입 토큰은 `type` 클레임으로 격리되어 서로 다른 용도로 사용될 수 없습니다.
- 인증/인가 실패 시 통일된 JSON(`COMMON401_1` / `COMMON403_1`)으로 응답합니다.

> 인증 시스템 상세 변경 내역은 [`week-9.md`](./week-9.md), 전체 API 명세는 [`API_SPEC.md`](./API_SPEC.md)를 참고하세요.

---

## ⚙️ 환경 변수

프로젝트 루트에 `.env` 파일을 생성하고 아래 값을 채웁니다. (`.env.example` 참고)

| 변수 | 설명 |
|------|------|
| `DB_URL` | MySQL JDBC URL |
| `DB_USER` | MySQL 사용자 이름 |
| `DB_PW` | MySQL 비밀번호 |
| `JWT_SECRET_KEY` | JWT 서명 키 (HS256, 최소 32바이트 권장) |
| `KAKAO_CLIENT_ID` | 카카오 디벨로퍼스 REST API 키 |
| `KAKAO_CLIENT_SECRET` | 카카오 디벨로퍼스 Client Secret |
| `KAKAO_REDIRECT_URI` | 카카오 로그인 Redirect URI (콘솔 등록 값과 일치) |

---

## 🚀 빌드 & 실행

```bash
# 컴파일
./gradlew compileJava

# 실행
./gradlew bootRun
```

- API 문서: `http://localhost:8080/swagger-ui/index.html`
