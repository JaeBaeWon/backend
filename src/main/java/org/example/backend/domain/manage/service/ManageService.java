package org.example.backend.domain.manage.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.manage.dto.ManageRequestDto;
import org.example.backend.domain.manage.entity.Manage;
import org.example.backend.domain.manage.repository.ManageRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ManageService {

    private final ManageRepository manageRepository;

    // 관리자별 공연 목록 조회
    public List<Manage> getMyPerformances(Long managerId) {
        return manageRepository.findByManagerId(managerId);
    }

    // 공연 등록
    public Manage createPerformance(ManageRequestDto dto, Long managerId) {
        Manage performance = Manage.builder()
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
                .managerId(managerId)
                .build();
        return manageRepository.save(performance);
    }

    // 공연 수정
    public void updatePerformance(Long id, ManageRequestDto dto, Long managerId) {
        Manage existing = manageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연이 존재하지 않습니다."));

        if (!existing.getManagerId().equals(managerId)) {
            throw new RuntimeException("수정 권한이 없습니다.");
        }

        Manage updated = Manage.builder()
                .performanceId(existing.getPerformanceId()) // 기존 ID 유지
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
                .managerId(managerId)
                .build();

        manageRepository.save(updated);
    }

    // 공연 삭제
    public void deletePerformance(Long id, Long managerId) {
        Manage performance = manageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공연이 존재하지 않습니다."));

        if (!performance.getManagerId().equals(managerId)) {
            throw new RuntimeException("삭제 권한이 없습니다.");
        }

        manageRepository.delete(performance);
    }
}
