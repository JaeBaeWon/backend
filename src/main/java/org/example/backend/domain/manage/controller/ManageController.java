package org.example.backend.domain.manage.controller;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.manage.dto.ManageRequestDto;
import org.example.backend.domain.manage.entity.Manage;
import org.example.backend.domain.manage.service.ManageService;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/manager/performance")
@RequiredArgsConstructor
public class ManageController {

    private final ManageService manageService;
    private final UserRepository userRepository;

    // 공연 목록 조회
    @GetMapping
    public ResponseEntity<List<Manage>> list(Authentication auth) {
        String email = auth.getName();
        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Long managerId = manager.getUserId();
        return ResponseEntity.ok(manageService.getMyPerformances(managerId));
    }

    // 공연 등록
    @PostMapping
    public ResponseEntity<?> create(@RequestBody ManageRequestDto dto, Authentication auth) {
        String email = auth.getName();
        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Long managerId = manager.getUserId();
        return ResponseEntity.ok(manageService.createPerformance(dto, managerId));
    }

    // 공연 수정
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody ManageRequestDto dto,
                                    Authentication auth) {
        String email = auth.getName();
        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Long managerId = manager.getUserId();
        manageService.updatePerformance(id, dto, managerId);
        return ResponseEntity.ok("수정 완료");
    }

    // 공연 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, Authentication auth) {
        String email = auth.getName();
        User manager = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자 없음"));
        Long managerId = manager.getUserId();
        manageService.deletePerformance(id, managerId);
        return ResponseEntity.ok("삭제 완료");
    }
}
