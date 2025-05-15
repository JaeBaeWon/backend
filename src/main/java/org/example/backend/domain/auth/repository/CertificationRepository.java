package org.example.backend.domain.auth.repository;

import org.example.backend.domain.auth.entity.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {
    Optional<Certification> findTopByEmailAndPhoneOrderByCreatedAtDesc(String email, String phone);

    Optional<Certification> findTopByPhoneOrderByCreatedAtDesc(String phone);
}
