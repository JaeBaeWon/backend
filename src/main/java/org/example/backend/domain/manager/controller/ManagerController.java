package org.example.backend.domain.manager.controller;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.auth.service.MemberService;
import org.example.backend.domain.manager.component.S3Uploader;
import org.example.backend.domain.manager.service.ManagerService;
import org.example.backend.domain.performance.dto.request.PerformanceRequestDto;
import org.example.backend.domain.performance.dto.response.PerformDetailRes;
import org.example.backend.domain.performance.dto.response.PerformRes;
import org.example.backend.domain.user.entity.User;
import org.example.backend.global.exception.CustomException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
public class ManagerController {

    private final S3Uploader s3Uploader;
    private final MemberService memberService;
    private final ManagerService managerService;

    // 공연 등록
    @PostMapping(value = "/manage/json", consumes = "application/json")
    public ResponseEntity<?> createPerformanceJson(@RequestBody PerformanceRequestDto dto, Authentication auth) {
        String email = auth.getName();
        User manager = memberService.getUserByEmail(email);

        // 기본 이미지 대체
        dto.setPerformanceImg("default.jpg");

        managerService.createPerformance(dto, manager);
        return ResponseEntity.ok("공연 등록 완료 (JSON)");
    }


    // 관리자별 공연 목록 조회
    @GetMapping("/manage/my")
    public ResponseEntity<?> getMyPerformances(Authentication auth) {
        try {
            String email = auth.getName();
            User manager = memberService.getUserByEmail(email);
            List<PerformRes> performances = managerService.getMyPerformances(manager);
            return ResponseEntity.ok(performances);
        } catch (CustomException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("서버 오류 발생");
        }
    }

    // 관리자 전용 공연 상세 조회
    @GetMapping("/manage/{id}")
    public ResponseEntity<PerformDetailRes> getMyPerformanceDetail(@PathVariable Long id,
                                                                   Authentication auth) {
        String email = auth.getName();
        User manager = memberService.getUserByEmail(email);
        PerformDetailRes detail = managerService.getMyPerformanceDetail(id, manager);
        return ResponseEntity.ok(detail);
    }

    // 공연 수정
    @PutMapping("/manage/{id}")
    public ResponseEntity<?> updatePerformance(@PathVariable Long id,
                                               @RequestBody PerformanceRequestDto dto,
                                               Authentication auth) {
        String email = auth.getName();
        User manager = memberService.getUserByEmail(email);
        managerService.updatePerformance(id, dto, manager);
        return ResponseEntity.ok("공연 수정 완료");
    }

    // 공연 삭제
    @DeleteMapping("/manage/{id}")
    public ResponseEntity<?> deletePerformance(@PathVariable Long id,
                                               Authentication auth) {
        String email = auth.getName();
        User manager = memberService.getUserByEmail(email);
        managerService.deletePerformance(id, manager);
        return ResponseEntity.ok("공연 삭제 완료");
    }

}
