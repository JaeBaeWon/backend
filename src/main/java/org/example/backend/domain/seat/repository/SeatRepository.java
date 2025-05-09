package org.example.backend.domain.seat.repository;

import java.util.List;
import org.example.backend.domain.performance.entity.Performance;
import org.example.backend.domain.seat.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findAllByPerformance(Performance perform);
}
