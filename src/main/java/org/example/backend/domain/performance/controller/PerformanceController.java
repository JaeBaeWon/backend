package org.example.backend.domain.performance.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.service.MemberService;
import org.example.backend.domain.performance.dto.PerformanceRankingDto;
import org.example.backend.domain.performance.dto.response.PerformDetailRes;
import org.example.backend.domain.performance.dto.response.PerformRes;
import org.example.backend.domain.performance.service.PerformanceService;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/performance")
public class PerformanceController {

    private final PerformanceService performanceService;
    private final MemberService memberService;

    // 통합 검색: 키워드 + 카테고리 + 상태
    @GetMapping("/search")
    public ResponseEntity<Page<PerformRes>> searchPerformances(@RequestParam(required = false) String keyword,
                                                               @RequestParam(required = false) String category,
                                                               @RequestParam(required = false) String status,
                                                               @RequestParam(defaultValue = "0") int page)
    {
        Page<PerformRes> performResPage = performanceService.searchPerformances(keyword, category, status, page);

        return ResponseEntity.ok(performResPage);
    }

    // 키워드로 공연 목록 조회
    @GetMapping("/keyword")
    public ResponseEntity<Page<PerformRes>> searchPerformancesByKeyword(@RequestParam String keyword,
                                                                        @RequestParam(defaultValue = "0") int page,
                                                                        Authentication auth)
    {

        boolean check = memberService.isAuthenticated(auth);
        if (!check) return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        Page<PerformRes> performResPage = performanceService.getPerformListByKeyword(keyword, page);

        return ResponseEntity.ok(performResPage);
    }

    // 카테고리로 공연 목록 조회
    @GetMapping("/category")
    public ResponseEntity<Page<PerformRes>> searchPerformancesByCategory(@RequestParam String category,
                                                                         @RequestParam(defaultValue = "0") int page) {

        Page<PerformRes> performResPage = performanceService.getPerformListByCategory(category, page);

        return ResponseEntity.ok(performResPage);
    }

    //공연 전체 목록 조회
    @GetMapping("/all")
    public ResponseEntity<List<PerformRes>> getAllPerform() {

        List<PerformRes> performResList = performanceService.getPerformList();

        return ResponseEntity.ok().body(performResList);
    }

    //공연 상세 조회
    @GetMapping("/{performId}")
    public ResponseEntity<PerformDetailRes> getPerform(@PathVariable Long performId) {
        PerformDetailRes performDetailRes = performanceService.getPerformDetail(performId);

        return ResponseEntity.ok().body(performDetailRes);
    }

    // 랭킹 조회
    @GetMapping("/ranking")
    public List<PerformanceRankingDto> getRanking() {
        return performanceService.getPerformanceRanking();
    }

}
