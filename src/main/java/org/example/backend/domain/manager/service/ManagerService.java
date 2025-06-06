package org.example.backend.domain.manager.service;

import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.performance.dto.request.PerformanceRequestDto;
import org.example.backend.domain.performance.dto.response.PerformDetailRes;
import org.example.backend.domain.performance.dto.response.PerformRes;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.user.entity.User;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ManagerService {

    private final PerformanceRepository performanceRepository;

    // 관리자별 공연 목록 조회
    public List<PerformRes> getMyPerformances(User manager) {
        List<Performance> list = performanceRepository.findByUser(manager);
        return list.stream().map(PerformRes::of).toList();
    }

    // 공연 등록
    public Performance createPerformance(PerformanceRequestDto dto, User manager) {
        Performance performance = Performance.builder()
                .title(dto.getTitle())
                .description(dto.getDescription())
                .category(dto.getCategory())
                .performanceCode(dto.getPerformanceCode())
                .performanceStartAt(dto.getPerformanceStartAt())
                .performanceEndAt(dto.getPerformanceEndAt())
                .performanceOpenAt(dto.getPerformanceOpenAt())
                .location(dto.getLocation())
                .performanceImg(dto.getPerformanceImg())
                .price(dto.getPrice())
                .totalSeats(dto.getTotalSeats())
                .remainSeats(dto.getTotalSeats())
                .performanceStatus(dto.getPerformanceStatus())
                .reservationDay(LocalDateTime.now())
                .user(manager)
                .build();
        return performanceRepository.save(performance);
    }

    // 공연 상세조회
    public PerformDetailRes getMyPerformanceDetail(Long id, User manager) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연이 존재하지 않습니다."));

        if (!performance.getUser().getUserId().equals(manager.getUserId())) {
            throw new RuntimeException("접근 권한이 없습니다.");
        }

        return PerformDetailRes.of(performance, performance.getRemainSeats());
    }

    // 공연 수정
    public void updatePerformance(Long id, PerformanceRequestDto dto, User manager) {
        Performance existing = performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연이 존재하지 않습니다."));

        if (!existing.getUser().getUserId().equals(manager.getUserId())) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        existing.updateFromDto(dto);
        performanceRepository.save(existing);
    }

    // 공연 삭제
    public void deletePerformance(Long id, User manager) {
        Performance performance = performanceRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연이 존재하지 않습니다."));

        if (!performance.getUser().getUserId().equals(manager.getUserId())) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        performanceRepository.delete(performance);
    }

}
