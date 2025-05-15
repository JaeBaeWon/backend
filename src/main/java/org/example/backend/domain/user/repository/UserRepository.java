package org.example.backend.domain.user.repository;

import org.example.backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 기본 ID 조회
    Optional<User> findById(Long userId);

    // 이메일로 사용자 조회
    Optional<User> findByEmail(String email);

    // 이메일 중복 확인
    boolean existsByEmail(String email);

    // 전화번호 + 생년월일로 사용자 조회 (ID 찾기 등)
    Optional<User> findByPhoneAndBirthday(String phone, LocalDate birthday);
}
