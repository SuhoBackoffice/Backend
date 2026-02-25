# CLAUDE.md

## 프로젝트 개요

**Suho**는 레일 부품(Branch Rail, Straight Rail) 생산을 위한 MES(Manufacturing Execution System) 성격의 프로젝트 관리 시스템이다.
인적 자원 관리는 제외하고, 프로젝트 단위의 자재 관리 / BOM / 생산 추적 / 작업 보고서 승인 워크플로우를 주로 다룬다.

---

## 빌드 및 실행

```bash
# 빌드 (QueryDSL Q클래스 생성 포함)
./gradlew build

# 실행 (기본 dev 프로파일)
./gradlew bootRun

# 특정 프로파일로 실행
./gradlew bootRun --args='--spring.profiles.active=prod'

# 클린
./gradlew clean

# 테스트
./gradlew test
./gradlew test --tests "baekgwa.suhoserver.ClassName"
```

> QueryDSL Q클래스는 `src/main/generated/`에 자동 생성된다. 직접 수정하지 않는다.

---

## 아키텍처

```
domain/   # Application layer - Controller, Facade, Service, DTO
model/    # Data layer - Entity, Repository
infra/    # Infrastructure - Event Listener, S3, Excel
global/   # Cross-cutting - Config, Security, Exception
```

**핵심 패턴**
- Facade: 여러 서비스를 조합하는 복잡한 흐름은 `*Facade`에서 처리
- Service 분리: `*ReadService` / `*WriteService` 로 조회/명령 분리
- 이벤트 기반 비동기: `ApplicationEventPublisher` → `@TransactionalEventListener(phase = AFTER_COMMIT)` + `@Async`

## 패키지 구조
```
src/main/java/baekgwa/suhoserver/
├── domain/                        # Application Layer
│   ├── authentication/
│   │   ├── controller/
│   │   ├── dto/
│   │   └── service/
│   ├── branch/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── facade/
│   │   └── service/
│   ├── file/
│   ├── material/
│   │   ├── controller/
│   │   ├── dto/
│   │   ├── facade/
│   │   ├── service/
│   │   └── type/
│   ├── notification/
│   ├── project/
│   ├── straight/
│   ├── user/
│   ├── version/
│   └── worker/
│
├── model/                         # Data Layer
│   ├── branch/
│   │   ├── bom/
│   │   └── type/
│   ├── material/
│   ├── notification/
│   │   ├── entity/
│   │   └── repository/
│   ├── project/
│   ├── straight/
│   ├── user/
│   ├── version/
│   └── work/
│
├── infra/                         # Infrastructure
│   ├── download/
│   ├── excel/
│   ├── history/
│   │   ├── event/
│   │   └── listener/
│   ├── notification/
│   │   ├── event/
│   │   └── store/
│   └── upload/
│
└── global/                        # Cross-cutting
    ├── config/
    ├── entity/                    # BaseEntity (TemporalEntity, SoftDeleteEntity)
    ├── environment/               # @ConfigurationProperties
    ├── exception/
    ├── factory/
    ├── response/                  # BaseResponse, ErrorCode, SuccessCode
    └── security/
```

---

## 코딩 컨벤션

기본은 **naver-coding-convention**을 따른다. 아래는 이 프로젝트에서 추가로 지키는 규칙이다.

---

### 네이밍 규칙

| 대상 | 규칙 | 예시 |
|---|---|---|
| 클래스 / 인터페이스 / 열거형 | PascalCase | `UserService`, `PaymentType` |
| 메서드 / 변수 / 파라미터 | camelCase | `getUserById`, `orderCount` |
| 상수 (`static final`) | UPPER_SNAKE_CASE | `MAX_RETRY_COUNT` |
| 패키지 | 소문자, 단수형 | `com.example.user` |
| 테스트 메서드 | `should_동작_when_조건` 또는 한글 허용 | `should_return_empty_when_not_found` |

---

### 클래스

- 클래스 하나당 파일 하나로 분리한다.
- 클래스 멤버 선언 순서는 `static 필드 → 인스턴스 필드 → 생성자 → 메서드` 순으로 작성한다.
- 인터페이스 구현 시 접두사(`I`)를 붙이지 않는다. (`IUserService` ❌ → `UserService` ✅)

---

### 메서드

- 메서드는 하나의 책임만 가진다.
- 파라미터는 최대 **3개** 이하로 유지하고, 초과 시 별도 객체(DTO/VO)로 묶는다.
- Boolean을 반환하는 메서드는 `is`, `has`, `can` 등의 접두사를 사용한다.
    - `isAvailable()`, `hasPermission()`

---

### 변수

- 의미 없는 약어 사용을 금지한다. (`cnt` ❌ → `count` ✅)
- 루프 변수는 `i`, `j`, `k`를 허용하지만 중첩 2단계를 초과하면 의미 있는 이름을 사용한다.
- `var` 사용 시 우변에서 타입이 명확히 드러날 때만 허용한다.

---

### 애너테이션

- 클래스/메서드 선언부의 애너테이션은 각 줄에 분리하여 작성한다.
```java
  @Getter
  @NoArgsConstructor
  public class User { ... }
```

---

### 기타

- `null` 반환 대신 `Optional` 또는 빈 컬렉션(`Collections.emptyList()`)을 반환한다.
- 예외는 구체적인 타입으로 던지며, `Exception`을 직접 `catch`하지 않는다.
- 매직 넘버는 상수로 추출한다. (`if (age > 19)` ❌ → `if (age > ADULT_AGE)` ✅)


### 공통

- Lombok: `@Getter` 사용, **`@Setter` 사용 금지** → 상태 변경은 명시적인 메서드로
- 생성자 대신 **static factory method** (`of`, `create`, `from`) 또는 빌더 패턴 사용 

### DTO

```java
// static factory method 패턴 필수
@Getter
public static class SearchMaterialInfo {
	private final Long id;
	private final String drawingNumber;
	private final String itemName;
	private final Long needInboundQuantity;

	@Builder(access = AccessLevel.PRIVATE)
	private SearchMaterialInfo(Long id, String drawingNumber, String itemName, Long needInboundQuantity) {
		this.id = id;
		this.drawingNumber = drawingNumber;
		this.itemName = itemName;
		this.needInboundQuantity = needInboundQuantity;
	}

	public static SearchMaterialInfo from(ProjectMaterialStockEntity stock) {

		Long needInboundQuantity = stock.getTotalPlanQuantity() - stock.getTotalInboundQuantity();

		return SearchMaterialInfo
			.builder()
			.id(stock.getId())
			.drawingNumber(stock.getMaterialCode())
			.itemName(stock.getItemName())
			.needInboundQuantity(needInboundQuantity)
			.build();
	}
}
```

### 예외 처리

```java
// GlobalException + ErrorCode 패턴만 사용
throw new GlobalException(ErrorCode.NOT_FOUND);
```

- 직접 `RuntimeException` 등을 던지지 않는다
- 새로운 에러 케이스는 `ErrorCode`에 추가 후 사용
- 에러 케이스는 도메인별로 구분 
  - 도메인 : 에러 코드 범위
  - Auth : 1000 ~ 1999
  - User : 2000 ~ 2999
  - Version : 3000 ~ 3999
  - Branch : 4000 ~ 4999
  - Project : 5000 ~ 5999
  - Straight : 6000 ~ 6999
  - Material : 7000 ~ 7999
  - Work : 8000 ~ 8999
  - Notification: 9000 ~ 9499
  - Common: 9500 ~ 9999

### API 응답

```java
// 모든 API 응답은 BaseResponse<T> 래퍼 사용
return BaseResponse.success(data);
```

### 트랜잭션 경계

| 상황 | 규칙 |
|------|------|
| 쓰기 서비스 메서드 | `@Transactional` 필수 |
| 조회 서비스 메서드 | `@Transactional` 불필요 (open-in-view: false) |
| Facade | `@Transactional` 으로 여러 서비스 묶기 |
| 이벤트 리스너 | 별도 트랜잭션에서 실행 (AFTER_COMMIT) |

### 페이지네이션

```java
// 목록 조회는 항상 Pageable 사용
public Page<SomeDto> getList(Pageable pageable) {
    return repository.findAll(pageable).map(SomeDto::from);
}
```

---

## 보안

- JWT 기반 Stateless 인증 (`userId`, `userRole` 클레임)
- 역할 계층: `ADMIN → STAFF → WORKER`
- 엔드포인트 인가: `.requestMatchers(GET, "/auth/admin").hasRole(ADMIN.name())` 방식 사용
- 설정 위치: `global/security/SecurityConfig.java`

---

## 환경 설정

- 시크릿은 `.env` 파일로 관리 (git 미포함)
- 프로파일: `dev` (H2 또는 MySQL) / `prod` (MySQL)
- DB 마이그레이션: Flyway 자동 적용 (`src/main/resources/db/migration/`)

---

## 기술 스택

| 항목 | 버전 |
|------|------|
| Java | 21 |
| Spring Boot | 3.5.4 |
| QueryDSL | 5.1.0 |
| JJWT | 0.12.3 |
| Apache POI | 5.4.0 |
| SpringDoc OpenAPI | 3.0 |
| DB | MySQL + Flyway |
| Storage | AWS S3 |

---