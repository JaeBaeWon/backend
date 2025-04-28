package org.example.backend.domain.seat.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.example.backend.domain.show.entity.Show;

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
    @JoinColumn(name = "show_id")
    private Show showId;
}
