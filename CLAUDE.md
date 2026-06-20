# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Commands

**Backend (Spring Boot + Maven)**
```bash
cd backend
mvn spring-boot:run          # Start dev server on :8080
mvn test                      # Run all tests
mvn test -Dtest=ClassName     # Run a single test class
mvn package                   # Build JAR
```
/
**Frontend (Vue 2 + Vue CLI)**
```bash
cd frontend
npm run serve                 # Start dev server on :3000 (proxies /api to :8080)
npm run build                 # Production build
npm run lint                  # Lint
```

## Architecture

### Authentication (two layers)

1. **JWT authentication** (`JwtInterceptor`) — protects all `/api/**` routes except `/api/auth/login` and `/api/openapi/**`. Token is passed via `Authorization: Bearer <token>` header. On validation, sets `userId` and `username` request attributes.

2. **AppKey authentication** (`AppAuthInterceptor`) — protects `/api/openapi/**` routes for external API access. Clients pass `X-App-Id` and `X-App-Key` headers. The interceptor looks up the `sms_application` table for a matching, enabled app and sets it as a request attribute.

Interceptor registration order and path patterns are defined in `WebMvcConfig`.

### Backend layer pattern

Standard Spring Boot MVC layering:
- `controller/` — REST endpoints, all return `Result<T>` (a unified `{code, msg, data}` wrapper)
- `service/` — interfaces extending MyBatis-Plus `IService<T>`
- `service/impl/` — implementations with business logic
- `mapper/` — MyBatis-Plus `BaseMapper<T>` interfaces (no XML)
- `entity/` — `@Data` POJOs with `@TableName`, using MyBatis-Plus auto-ID generation
- `config/` — CORS, MyBatis-Plus pagination plugin, interceptor registration
- `common/` — `Result<T>` response wrapper, `GlobalExceptionHandler` (`@RestControllerAdvice`), `PageParam`
- `util/` — `JwtUtil` (JWT create/validate/parse), `SmsCountUtil` (Unicode-aware SMS segment counting and fee calculation)

### Frontend structure

- `api/` — Axios request wrappers per domain (app, auth, record, sms, stat)
- `utils/request.js` — Shared Axios instance with base URL `/api`, auto-attaches `Authorization` header from Vuex, handles 401 by redirecting to `/login`
- `store/` — Vuex with `token` and `userInfo` persisted to `localStorage`
- `router/` — `mode: 'history'`, route guard redirects unauthenticated users to `/login`
- `views/` — Five pages: Login, AppList, SmsSend, RecordList, StatReport, Profile

### SMS billing calculation

**字数构成：** 通知短信字数 = 短信签名 + 短信内容；营销短信字数 = 短信签名 + 短信内容 + 退订信息。

**计费规则（运营商标准）：** 长短信每条需使用 3 个字符的拼接符在终端做拼接，因此：

| 短信条数 | 字符数 | 计算公式 |
|---------|--------|---------|
| 1 条 | ≤70 字 | 1 × 70 |
| 2 条 | 71–134 字 | 2 × 67 |
| 3 条 | 135–201 字 | 3 × 67 |
| n 条 (n>1) | — | n × 67 |

即：70 字以内按 1 条计费，超出后每 67 字为 1 条计费。

`SmsCountUtil` implements this: `countChars()` counts Unicode code points (supplementary chars count as 2), `countBillingSegments()` returns 1 for ≤70 chars, otherwise `ceil(chars / 67)`. Fee = segments × app's unit price.

### API endpoint groups

| Path prefix | Auth | Purpose |
|---|---|---|
| `/api/auth/**` | None | Login |
| `/api/callback/**` | None | JD Cloud status report / upstream push callback |
| `/api/openapi/**` | AppKey (`X-App-Id` + `X-App-Key`) | External SMS send API |
| `/api/app/**` | JWT | Application CRUD |
| `/api/sms/**` | JWT | SMS send/batch |
| `/api/record/**` | JWT | Send records + Excel export |
| `/api/stat/**` | JWT | Statistics |

### SMS gateway (京东云)

SMS sending delegates to JD Cloud SMS API via Retrofit (`gateway/` package).

- **`SmsGatewayProperties`** — `@ConfigurationProperties(prefix = "sms.gateway")`, reads account credentials and base URL from `application.yml`
- **`JdCloudSmsApi`** — Retrofit interface for `POST /HttpSmsMt` (form-encoded), annotated with `@RetrofitClient`
- **`SmsGatewayService`** — encrypts password as `MD5(password + mttime)` (mttime = `yyyyMMddHHmmss`), calls the API, returns `JdCloudSmsResponse`

**SmsRecord status flow:**
| Status | Meaning |
|---|---|
| 0 | Submit failed (API returned non-00 ReqCode or network error) |
| 1 | Submitted (API returned ReqCode=00, awaiting status report) |
| 2 | Delivered (status report with state=0) |
| 3 | Delivery failed (status report with state≠0) |

**Callback:** `SmsCallbackController` at `GET /api/callback/sms` receives status reports (`msgtype=2`) and upstream messages (`msgtype=0`). On status report, updates `SmsRecord.status` via `reqId + phone` matching. Must return plain `"ok"`.

**Batch send:** phones grouped by 100 per API request; all phones in a batch share the same `reqId`.

### Database

MySQL 8.0, schema initialization script at `doc/sms_billing.sql`. MyBatis-Plus handles ORM with `map-underscore-to-camel-case: true` and auto-increment IDs. Default login: `admin` / `123456`.
