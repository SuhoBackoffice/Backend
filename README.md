# SuhoServer

레일 부품(Branch Rail, Straight Rail) 생산을 위한 MES(Manufacturing Execution System) 성격의 프로젝트 관리 백엔드 서버.


---

## 기술 스택

| 항목 | 버전 |
|---|---|
| Java | 21 |
| Spring Boot | 3.5.4 |
| QueryDSL | 5.1.0 |
| JJWT | 0.12.3 |
| Apache POI | 5.4.0 |
| SpringDoc OpenAPI | 2.8.8 |
| Flyway | 11.5.0 |
| AWS SDK S3 | 2.31.11 |
| DB | MySQL |

---

## ERD Diagram
[ERD Cloud](https://www.erdcloud.com/d/BdBpNtukjEQ8bjLfC)


---

## 아키텍처

```
domain/   # Application layer - Controller, Facade, Service, DTO
model/    # Data layer - Entity, Repository
infra/    # Infrastructure - EventListener, S3, Excel
global/   # Cross-cutting - Config, Security, Exception, Response
```

### 핵심 패턴

- **Facade**: 여러 서비스를 조합하는 복잡한 흐름은 `*Facade`에서 처리
- **Service 분리**: `*ReadService` / `*WriteService` 로 조회/명령 분리
- **이벤트 기반 비동기**: `ApplicationEventPublisher` → `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Async`

---

## 패키지 구조

```
src/main/java/baekgwa/suhoserver/
├── domain/                        # Application Layer
│   ├── authentication/            # JWT 인증 (login/logout)
│   ├── branch/                    # 분기 레일 관리
│   ├── file/                      # 파일 업로드/다운로드
│   ├── material/                  # 자재 입출고 관리
│   ├── notification/              # 알림 시스템
│   ├── project/                   # 프로젝트 관리
│   ├── straight/                  # 직선 레일 관리
│   ├── user/                      # 사용자 관리
│   ├── version/                   # 버전 정보
│   └── worker/                    # 작업 보고 승인 워크플로우
│
├── model/                         # Data Layer
│   ├── branch/                    # BranchTypeEntity, BranchBomEntity
│   ├── material/                  # ProjectMaterialStockEntity, MaterialHistoryEntity
│   ├── notification/              # NotificationEntity, UserNotificationEntity
│   ├── project/                   # ProjectEntity, ProjectBranch/Straight 관련
│   ├── straight/                  # StraightTypeEntity, StraightBomStandardEntity
│   ├── user/                      # UserEntity
│   ├── version/                   # VersionInfoEntity
│   └── work/                      # WorkReport 관련 엔티티
│
├── infra/
│   ├── download/                  # 파일 다운로드 처리
│   ├── excel/                     # Excel 생성/파싱 (Apache POI)
│   ├── history/                   # 이력 이벤트 리스너
│   ├── notification/              # 알림 이벤트 처리
│   └── upload/                    # S3 업로드 처리
│
└── global/
    ├── config/                    # Spring 설정
    ├── entity/                    # BaseEntity (TemporalEntity, SoftDeleteEntity)
    ├── environment/               # @ConfigurationProperties
    ├── exception/                 # GlobalException
    ├── response/                  # BaseResponse, ErrorCode, SuccessCode
    └── security/                  # SecurityConfig, JWT 필터
```

---

## 빌드 및 실행

### 사전 조건

프로젝트 루트에 `.env` 파일을 생성하고 아래 항목을 설정한다.

```properties
RDBMS_URL=jdbc:mysql://localhost:3306/suho
RDBMS_USERNAME=root
RDBMS_PASSWORD=password

JWT_SECRET_KEY=your-secret-key
JWT_TOKEN_EXPIRATION_MIN=60

S3_BUCKET=your-bucket
S3_REGION=ap-northeast-2
S3_ACCESS_KEY=your-access-key
S3_SECRET_KEY=your-secret-key

FRONTEND_URL=http://localhost:3000
API_SERVER_URL=http://localhost:8080
```

### 명령어

```bash
# 빌드 (QueryDSL Q클래스 생성 포함)
./gradlew build

# 실행 (기본 dev 프로파일)
./gradlew bootRun

# 특정 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=prod'

# 클린
./gradlew clean

# 테스트 전체
./gradlew test

# 특정 클래스 테스트
./gradlew test --tests "baekgwa.suhoserver.ClassName"
```

> QueryDSL Q클래스는 `src/main/generated/`에 자동 생성된다. 직접 수정하지 않는다.

---

## 프로파일

| 프로파일 | 설명 |
|---|---|
| `dev` | 개발 환경 (MySQL, seed 데이터 포함, SQL 로그 출력) |
| `prod` | 운영 환경 (MySQL, seed 데이터 제외, 로그 레벨 warn) |

프로파일 그룹 구성:

```yaml
dev:  dev_db + dev_server + dev_auth
prod: prod_db + prod_server + prod_auth
```

---

## DB 마이그레이션

Flyway를 사용하며 서버 기동 시 자동 적용된다.

| 버전 | 파일 | 내용 |
|---|---|---|
| V1.0.0 | V1.0.0__init.sql | 초기 테이블: users, version_info, branch, project, straight 등 |
| V2.0.0 | V2.0.0__create_material_inbound.sql | 자재 관리 테이블 |
| V3.0.0 | V3.0.0__create_product_serial_domain.sql | 제품 시리얼 테이블 |
| V3.0.1 | V3.0.1__create_product_history_table.sql | 제품 이력 테이블 |
| V3.0.2 | V3.0.2__create_work_report_domain.sql | 작업 보고 테이블 |
| V3.1.0 | V3.1.0__create_notification_domain.sql | 알림 시스템 테이블 |
| V3.2.0 | V3.2.0__create_straight_material_domain.sql | 직선레일 자재 테이블 |

---

## 보안

- JWT 기반 Stateless 인증 (`userId`, `userRole` 클레임)
- 역할 계층: `ADMIN → STAFF → WORKER`
- 엔드포인트 인가: `SecurityConfig.java` 에서 관리

---

## API 문서

서버 실행 후 아래 주소에서 Swagger UI 확인 가능.

```
http://localhost:8080/swagger-ui/index.html
```

---

## 주요 도메인

### Project

프로젝트 등록 및 관리. 분기/직선 레일을 등록하고 물량리스트 Excel을 다운로드할 수 있다.

### Material (자재)

프로젝트별 자재 입고 등록, 재고 현황, 입출고 이력을 조회한다.

### Branch Rail (분기 레일)

BOM 업로드, 타입 관리, 생산 현황 조회 및 분석.

### Straight Rail (직선 레일)

타입 관리, BOM 표준 관리, 생산 현황 조회 및 분석.

### Work Report (작업 보고)

작업자가 생산 실적을 보고하고 관리자가 승인하는 워크플로우.

### Notification (알림)

승인/반려 등 이벤트 발생 시 사용자에게 알림 전송.

---

## 예외 처리

`GlobalException` + `ErrorCode` 패턴을 사용한다.

```java
throw new GlobalException(ErrorCode.NOT_FOUND_PROJECT);
```

에러 코드 범위:

| 도메인 | 범위 |
|---|---|
| Auth | 1000 ~ 1999 |
| User | 2000 ~ 2999 |
| Version | 3000 ~ 3999 |
| Branch | 4000 ~ 4999 |
| Project | 5000 ~ 5999 |
| Straight | 6000 ~ 6999 |
| Material | 7000 ~ 7999 |
| Work | 8000 ~ 8999 |
| Notification | 9000 ~ 9499 |
| Common | 9500 ~ 9999 |

---

## API 응답 형식

모든 API 응답은 `BaseResponse<T>` 래퍼를 사용한다.

```json
{
  "httpStatus": 200,
  "isSuccess": true,
  "code": "200",
  "message": "성공",
  "data": { ... }
}
```
