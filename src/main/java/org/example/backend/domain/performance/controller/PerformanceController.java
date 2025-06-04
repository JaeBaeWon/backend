package org.example.backend.domain.performance.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.service.MemberService;
import org.example.backend.domain.performance.dto.PerformanceRequestDto;
import org.example.backend.domain.performance.dto.response.PerformDetailRes;
import org.example.backend.domain.performance.dto.response.PerformRes;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.service.PerformanceService;
import org.example.backend.domain.user.entity.User;
import org.example.backend.global.exception.CustomException;
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


    // ---------- MANAGER가 performance CRUD

    // 공연 등록
    @PostMapping("/manage")
    public ResponseEntity<?> createPerformance(@RequestBody PerformanceRequestDto dto,
                                               Authentication auth) {
        String email = auth.getName();
        User manager = memberService.getUserByEmail(email); // 또는 userRepository.findByEmail
        performanceService.createPerformance(dto, manager);
        return ResponseEntity.ok("공연 등록 완료");
    }

    // 관리자별 공연 목록 조회
    @GetMapping("/manage/my")
    public ResponseEntity<?> getMyPerformances(Authentication auth) {
        try {
            String email = auth.getName();
            User manager = memberService.getUserByEmail(email); // 여기서 에러 터짐
            List<Performance> performances = performanceService.getMyPerformances(manager);
            return ResponseEntity.ok(performances);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    // 공연 수정
    @PutMapping("/manage/{id}")
    public ResponseEntity<?> updatePerformance(@PathVariable Long id,
                                               @RequestBody PerformanceRequestDto dto,
                                               Authentication auth) {
        String email = auth.getName();
        User manager = memberService.getUserByEmail(email);
        performanceService.updatePerformance(id, dto, manager);
        return ResponseEntity.ok("공연 수정 완료");
    }

    // 공연 삭제
    @DeleteMapping("/manage/{id}")
    public ResponseEntity<?> deletePerformance(@PathVariable Long id,
                                               Authentication auth) {
        String email = auth.getName();
        User manager = memberService.getUserByEmail(email);
        performanceService.deletePerformance(id, manager);
        return ResponseEntity.ok("공연 삭제 완료");
    }
}
