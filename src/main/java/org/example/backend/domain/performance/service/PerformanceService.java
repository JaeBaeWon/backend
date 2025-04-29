package org.example.backend.domain.performance.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.performance.dto.response.PerformDetailRes;
import org.example.backend.domain.performance.dto.response.PerformRes;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.PerformanceRepository;
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

    // 통합 공연 목록 조회
    public Page<PerformRes> searchPerformances(String keyword, String category, String status, int page) {
        PageRequest pageable = PageRequest.of(page, 6); // 한 페이지에 6개
        Page<Performance> performancePage = performanceRepository.searchPerformances(keyword, category, status, pageable);

        return performancePage.map(PerformRes::of);
    }

    // 키워드로 공연 목록 조회
    public Page<PerformRes> getPerformListByKeyword(String keyword, int page) {
        PageRequest pageable = PageRequest.of(page, 6); // 6개씩
        Page<Performance> performancePage = performanceRepository.findByKeyword(keyword, pageable);

        return performancePage.map(PerformRes::of);
    }

    // 카테고리로 공연 목록 조회
    public Page<PerformRes> getPerformListByCategory(String category, int page) {
        PageRequest pageable = PageRequest.of(page, 6);
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

        return PerformDetailRes.of(performance);
    }

    //공연 전체 조회
    private List<Performance> getAllPerform() {
        return performanceRepository.findAll();
    }

    //공연 단건 조회
    private Performance getPerformById(Long performId) {
        Performance performance = performanceRepository.findById(performId)
                .orElseThrow(() -> new CustomException(NOT_FOUND_PERFORMANCE));

        return performance;
    }
}
