package org.example.backend.domain.reservation.service;

import lombok.RequiredArgsConstructor;
import org.example.backend.domain.reservation.dto.ReservationDto;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReservationService {
    private final UserRepository userRepository;

    @Transactional(readOnly = true)
    public ReservationDto getOrderSummary(ReservationDto dto) {
        User user = userRepository.findById(dto.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("해당 사용자가 존재하지 않습니다."));

        return ReservationDto.builder()
                .userId(user.getUserId())
                .paymentDto(dto.getPaymentDto())  // PaymentDto 설정
                .build();
    }
}
