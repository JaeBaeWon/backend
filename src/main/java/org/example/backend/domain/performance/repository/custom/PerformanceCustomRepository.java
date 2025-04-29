package org.example.backend.domain.performance.repository.custom;

import org.example.backend.domain.performance.entity.Performance;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PerformanceCustomRepository {
    Page<Performance> findByKeyword(String keyword, Pageable pageable);
    Page<Performance> findByCategory(String category, Pageable pageable);
    Page<Performance> searchPerformances(String keyword, String category, String status, Pageable pageable);
}
