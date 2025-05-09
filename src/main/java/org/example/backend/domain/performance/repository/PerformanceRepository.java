package org.example.backend.domain.performance.repository;

import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.performance.repository.custom.PerformanceCustomRepository;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PerformanceRepository extends JpaRepository<Performance, Long>, PerformanceCustomRepository {
}
