## 🎟️ 서비스 개요

클라우드 환경 기반 고가용성 티켓팅 플랫폼
대규모 동시 접속 환경에서 안정적인 좌석 선점, 예매, 결제 기능을 제공.

## 📌 기획 배경
### 🎭 공연 산업 성장

- 전년도 동분기 대비 306억 원 증가

- 대형 공연뿐 아니라 중소형 공연 시장도 확대 중

### 🚀 신규 플랫폼의 등장

- 우리은행 티켓팅 플랫폼 진출 예정

- 중소형 공연과의 상생 가치 실현 가능

## 🔎 기존 서비스와 차별점
### 1. 피싱 사이트 피해 방지

공연 알림 예약 시, 공식 예매 사이트 주소를 메일로 발송

사용자가 안전하게 바로 접속 → 피싱 위험 최소화

### 2. 잔여석 확인의 어려움 해결

- 기존 사이트: 잔여석 확인 불편

- 본 플랫폼: 상세 페이지 UI에서 즉시 확인 가능

### 3. 결제 단계 이전의 이선좌 문제 해결

- 기존: 결제 단계에서 이선좌 발생 → 다시 좌석 선택 필요

- 개선: 좌석 선택 시 즉시 이선좌 여부 확인 가능

## 🛠️ 주요 기능
### ✅ 피싱 방지 프로세스

<img width="2022" height="870" alt="image" src="https://github.com/user-attachments/assets/6c24bd0d-c40e-40be-8e31-b0364edbd762" />

<img width="1660" height="968" alt="image" src="https://github.com/user-attachments/assets/8133774d-1b72-4011-93e3-2afc15751fae" />



### ✅ 잔여석 확인 UI

<img width="2048" height="983" alt="image" src="https://github.com/user-attachments/assets/e7ea7ef7-8efb-4066-969c-ef159b096141" />


### ✅ 실시간 이선좌 방지

<img width="2048" height="990" alt="image" src="https://github.com/user-attachments/assets/540521ba-6a89-47f7-834a-64186248cfc5" />


## ⚙️ 핵심 기능 구현
### 좌석 선점 동시성 제어

- 인기 티켓팅 환경에서 한 좌석에 다수 사용자가 동시에 요청 → 동시성 이슈 발생
→ Redis 분산락(Redisson) 으로 해결

### 🔑 핵심 코드
```
// Redis Lock으로 좌석 이선좌 관리
public boolean tryLockSeat(Long seatId) {
    RLock lock = redissonClient.getLock(SEAT_LOCK_PREFIX + seatId);
    String seatKey = SEAT_STATE_PREFIX + seatId;

    try {
        if (lock.tryLock(1, 10, TimeUnit.SECONDS)) {
            String seatStatus = redisTemplate.opsForValue().get(seatKey);

            if (!"RESERVED".equals(seatStatus)) {
                // Redis에 임시 선점 상태 저장 (TTL: 5분)
                redisTemplate.opsForValue().set(seatKey, "RESERVED", 5, TimeUnit.MINUTES);
                return true;
            }
        }
    } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
    } finally {
        if (lock.isHeldByCurrentThread()) {
            lock.unlock();
        }
    }
    return false;
}
```

### 🧪 JUnit 테스트 결과

<img width="1866" height="440" alt="image" src="https://github.com/user-attachments/assets/514baf34-b4d8-4ada-845b-7da487b799a6" />

<img width="1880" height="1098" alt="image" src="https://github.com/user-attachments/assets/7380b381-2f6c-4f59-b0e6-f1aae9174a27" />

<img width="1840" height="310" alt="image" src="https://github.com/user-attachments/assets/bef725ff-5bd1-4934-b529-9af51ec665cd" />

<img width="726" height="320" alt="image" src="https://github.com/user-attachments/assets/d2ec7f15-ec12-40e6-ac25-bbe9bbe9f423" />



- 동시 요청 테스트 (100좌석 × 100명 요청)

<img width="1816" height="548" alt="image" src="https://github.com/user-attachments/assets/6efacdcb-299d-42c2-84fe-44b080ec25f4" />

<img width="1812" height="500" alt="image" src="https://github.com/user-attachments/assets/c40159e5-8155-4954-b565-e97a73009f4c" />

- 결과: 모든 좌석이 단 한 명만 선점 성공

**⚠️ 추후 고려사항**

- 현재 Redis Lock TTL = 5분

- 문제 상황:

  - 사용자가 선점 성공 후 결제 전에 오류로 이탈 → 유령락(ghost lock) 발생

  - 해당 좌석은 5분간 선점 불가 → 매출 손실 위험

➡ 대응 방안 필요

- 장애 감지 시 락 강제 해제 로직

- 결제 흐름과 선점 상태를 실시간 동기화

- 일정 주기 점검(batch)으로 유령락 복구
