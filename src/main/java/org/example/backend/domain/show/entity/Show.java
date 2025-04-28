package org.example.backend.domain.show.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
public class Show {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long showId;

    private String title;

    private String description;

    private ShowCategory category;

    private String showCode;

    private Date showStartAt;

    private Date showEndAt;

    private String location;

    private String showImg;

    private int price;

    private Long views;

    private int totalSeats;

    private int remainSeats;

    private ShowStatus showStatus;
}
