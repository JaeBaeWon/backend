package org.example.backend.domain.performance.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Entity
public class Performance {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long performId;

    private String title;

    private String description;

    @Enumerated(EnumType.STRING)
    private PerformanceCategory category;

    private String performCode;

    private Date performStartAt;

    private Date performEndAt;

    private String location;

    private String performImg;

    private int price;

    private Long views;

    private int totalSeats;

    private int remainSeats;

    @Enumerated(EnumType.STRING)
    private PerformanceStatus performanceStatus;
}
