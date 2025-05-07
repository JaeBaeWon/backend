package org.example.backend.domain.seat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.backend.domain.performance.entity.Performance;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Seat {

    @Id  @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long seatId;

    private String seatNum;

    private String seatSection;

    private boolean seatReserved;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "performance_id")
    private Performance performance;

    public void reserve() {
        this.seatReserved = true;
    }

}
