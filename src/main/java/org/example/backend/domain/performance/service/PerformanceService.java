package org.example.backend.domain.performance.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.performance.dto.PerformanceRankingDto;
import org.example.backend.domain.performance.dto.response.PerformDetailRes;
import org.example.backend.domain.performance.dto.response.PerformRes;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.PerformanceRepository;
import org.example.backend.domain.seat.entity.Seat;
import org.example.backend.domain.seat.entity.SeatStatus;
import org.example.backend.domain.seat.repository.SeatRepository;
import org.example.backend.global.exception.CustomException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.example.backend.global.exception.ExceptionContent.NOT_FOUND_PERFORMANCE;

@RequiredArgsConstructor
@Service
public class PerformanceService {

    private final PerformanceRepository performanceRepository;
    private final SeatRepository seatRepository;

    // 통합 공연 목록 조회
    public Page<PerformRes> searchPerformances(String keyword, String category, String status, int page) {
        PageRequest pageable = PageRequest.of(page, 8); // 한 페이지에 8개
        Page<Performance> performancePage = performanceRepository.searchPerformances(keyword, category, status, pageable);

        return performancePage.map(PerformRes::of);
    }

    // 키워드로 공연 목록 조회
    public Page<PerformRes> getPerformListByKeyword(String keyword, int page) {
        PageRequest pageable = PageRequest.of(page, 8); // 8개씩
        Page<Performance> performancePage = performanceRepository.findByKeyword(keyword, pageable);

        return performancePage.map(PerformRes::of);
    }

    // 카테고리로 공연 목록 조회
    public Page<PerformRes> getPerformListByCategory(String category, int page) {
        PageRequest pageable = PageRequest.of(page, 8);
        Page<Performance> performancePage = performanceRepository.findByCategory(category, pageable);

        return performancePage.map(PerformRes::of);
    }


    //공연 전체 조회 dto화
    public List<PerformRes> getPerformList() {
        List<Performance> performList = performanceRepository.findAll();

        List<PerformRes> performResList = performList.stream()
                .map(PerformRes::of)
                .toList();

        return performResList;
    }

    //공연 단건 조회 dto화
    public PerformDetailRes getPerformDetail(Long performId) {
        Performance performance = getPerformById(performId);

        // 공연에 관련된 좌석 조회
        List<Seat> seats = seatRepository.findAllByPerformance(performance);

        // AVAILABLE 상태인 좌석 개수 계산
        long remainingSeats = seats.stream()
                .filter(seat -> seat.getSeatStatus() == SeatStatus.AVAILABLE)
                .count();

        // PerformDetailRes 생성하여 반환
        return PerformDetailRes.of(performance, remainingSeats);
    }

    //공연 단건 조회
    private Performance getPerformById(Long performId) {
        Performance performance = performanceRepository.findById(performId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PERFORMANCE));

        return performance;
    }

    // 랭킹 데이터 가져오기
    public List<PerformanceRankingDto> getPerformanceRanking() {
        return performanceRepository.findAllByOrderByViewsDesc().stream()
                .map(p -> new PerformanceRankingDto(
                        p.getPerformanceId(),
                        p.getTitle(),
                        p.getCategory(),
                        p.getPerformanceStartAt(),
                        p.getPerformanceEndAt(),
                        p.getPerformanceOpenAt(),
                        p.getLocation(),
                        p.getPerformanceImg(),
                        p.getPrice(),
                        p.getViews(),
                        p.getPerformanceStatus()
                ))
                .toList();
    }

}