package org.example.backend.domain.performance.repository;

import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.custom.PerformanceCustomRepository;
import org.example.backend.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PerformanceRepository extends JpaRepository<Performance, Long>, PerformanceCustomRepository {
    List<Performance> findByUser(User user);

    // 조회수 기준 내림차순 정렬
    List<Performance> findAllByOrderByViewsDesc();
}
