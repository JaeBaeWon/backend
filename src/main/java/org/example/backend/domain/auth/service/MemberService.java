package org.example.backend.domain.auth.service;

import org.example.backend.domain.payment.entity.Payment;
import org.example.backend.domain.payment.repository.PaymentRepository;
import org.example.backend.domain.reservation.dto.MyPageReservationDto;
import org.example.backend.domain.reservation.dto.ReservationDetailsDto;
import org.example.backend.domain.user.entity.Gender;
import org.example.backend.domain.user.entity.User;
import org.example.backend.domain.auth.dto.*;
import org.example.backend.domain.auth.entity.Certification;
import org.example.backend.domain.auth.entity.RefreshToken;
import org.example.backend.domain.reservation.entity.Reservation;
import org.example.backend.domain.auth.repository.CertificationRepository;
import org.example.backend.domain.user.repository.UserRepository;
import org.example.backend.domain.auth.repository.RefreshTokenRepository;
import org.example.backend.domain.reservation.repository.ReservationRepository;
import org.example.backend.domain.auth.util.JWTUtil;
import org.example.backend.domain.auth.util.SmsUtil;
import lombok.RequiredArgsConstructor;
import org.example.backend.domain.user.dto.MemberProfileResponse;
import org.example.backend.domain.user.repository.UpdateProfileRequest;
import org.example.backend.global.exception.CustomException;
import org.example.backend.global.exception.ExceptionContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberService {

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);
    private static final long CERTIFICATION_EXPIRATION_MINUTES = 3;

    private final CertificationRepository certificationRepository;
    private final SmsUtil smsUtil;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final ReservationRepository reservationRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JWTUtil jwtUtil;
    private final PaymentRepository paymentRepository;

    public boolean isAuthenticated(Authentication auth) {
        return auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName());
    }

    public boolean checkLoginIdDuplicate(String email) {
        return userRepository.existsByEmail(email);
    }

    public void join(JoinRequest joinRequest) {
        if (checkLoginIdDuplicate(joinRequest.getEmail())) {
            throw new IllegalArgumentException("이미 존재하는 ID입니다.");
        }
        User user = joinRequest.toEntity();
        user.setPassword(bCryptPasswordEncoder.encode(joinRequest.getPassword()));
        userRepository.save(user);
    }

    public RefreshToken getRefreshTokenByEmail(String email) {
        return refreshTokenRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionContent.EXPIRED_TOKEN));
    }


    public LoginResponseDto login(LoginRequest loginRequest) {
        User user = userRepository.findByEmail(loginRequest.getEmail()).filter(m -> bCryptPasswordEncoder.matches(loginRequest.getPassword(), m.getPassword())).orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_MEMBER));

        String refreshToken = jwtUtil.createRefreshToken(user.getEmail());
        refreshTokenRepository.findByEmail(user.getEmail()).ifPresentOrElse(existing -> {
            existing.setToken(refreshToken);
            existing.setExpiration(LocalDateTime.now().plusDays(14));
            refreshTokenRepository.save(existing);
        }, () -> refreshTokenRepository.save(new RefreshToken(null, user.getEmail(), refreshToken, LocalDateTime.now().plusDays(14))));

        String accessToken = jwtUtil.createAccessToken(user.getUserId(), user.getEmail(), user.getRole().name());

        return LoginResponseDto.builder().email(user.getEmail()).userName(user.getUsername()).role(user.getRole().name()).accessToken(accessToken).refreshToken(refreshToken).build();
    }


    public ReservationDetailsDto
    getReservationByIdAndLoginId(Long reservationId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_USER));

        Reservation reservation = reservationRepository.findByReservationIdAndUserEmail(reservationId, email)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_RESERVATION));

        Payment payment = paymentRepository.findByReservation(reservation)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_PAYMENT));

        return ReservationDetailsDto.of(reservation, payment);
    }


    public MemberDto getLoginMemberByEmail(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_MEMBER));
        return MemberDto.of(user);
    }

    public boolean isOnboardingComplete(String email) {
        return userRepository.findByEmail(email).map(this::isOnboardingComplete).orElse(false);
    }

    public boolean isOnboardingComplete(User user) {
        return user.isOnboardingCompleted();
    }


    @Transactional
    public void updateOnboardingInfo(String email, String gender, String zip, String street,
                                     String detail, String phone, LocalDate birthDate) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_MEMBER));

        user.setGender(Gender.valueOf(gender));
        user.setZipCode(zip);
        user.setStreetAdr(street);
        user.setDetailAdr(detail);
        user.setPhone(phone);
        user.setBirthday(birthDate);

        // ✅ 온보딩 완료로 처리
        user.setOnboardingCompleted(true);
    }

    @Transactional
    public void updateMemberProfile(String email, UpdateProfileRequest request) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_MEMBER));

        if (request.getGender() != null) {
            user.setGender(request.getGender());
        }
        if (request.getZipCode() != null) {
            user.setZipCode(request.getZipCode());
        }
        if (request.getStreetAdr() != null) {
            user.setStreetAdr(request.getStreetAdr());
        }
        if (request.getDetailAdr() != null) {
            user.setDetailAdr(request.getDetailAdr());
        }
        if (request.getPhone() != null) {
            user.setPhone(request.getPhone());
        }
        if (request.getBirthDate() != null) {
            user.setBirthday(request.getBirthDate());
        }
    }

    public MemberProfileResponse getProfile(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_MEMBER));

        return MemberProfileResponse.builder()
                .gender(user.getGender())
                .zipCode(user.getZipCode())
                .streetAdr(user.getStreetAdr())
                .detailAdr(user.getDetailAdr())
                .phone(user.getPhone())
                .birthDate(user.getBirthday())
                .build();
    }


    public String sendCertificationNumberForReset(String email, String phone, LocalDate birthday) {
        final String normalizedPhone = phone.replace("-", "");

        Optional<User> memberOpt = userRepository.findByEmail(email).filter(m -> normalizedPhone.equals(m.getPhone().replace("-", "")) && birthday.equals(m.getBirthday()));

        if (memberOpt.isEmpty()) {
            return "입력하신 정보로 가입된 사용자가 없습니다.";
        }

        return sendCertificationCode(normalizedPhone, email, true);
    }

    public String sendCertificationNumberForIdFind(FindIdRequest request) {
        Optional<User> memberOpt = userRepository.findByPhoneAndBirthday(request.getPhone(), request.getBirthday());
        if (memberOpt.isEmpty()) {
            return "입력하신 정보로 가입된 사용자가 없습니다.";
        }

        return sendCertificationCode(request.getPhone(), memberOpt.get().getEmail(), false);
    }

    private String sendCertificationCode(String phone, String email, boolean encryptCode) {
        String code = generateCertificationCode();
        String saveCode = encryptCode ? bCryptPasswordEncoder.encode(code) : code;

        certificationRepository.save(Certification.builder().email(email).phone(phone).certificationNumber(saveCode).createdAt(LocalDateTime.now()).build());

        try {
            smsUtil.sendOne(phone, code);
        } catch (Exception e) {
            log.error("SMS 전송 실패: {}", e.getMessage());
            return "\u26D4 SMS 전송 중 오류가 발생했습니다.";
        }

        return "인증번호를 전송했습니다.";
    }

    public FindIdResponseDto verifyCodeAndFindId(SmsVerifyIdRequest request) {
        return certificationRepository.findTopByPhoneOrderByCreatedAtDesc(request.getPhone()).filter(cert -> isValidCertification(cert, request.getCode())).flatMap(cert -> userRepository.findByEmail(cert.getEmail())).filter(member -> request.getBirthday().equals(member.getBirthday())).map(member -> FindIdResponseDto.builder().email(member.getEmail()).message("✅ 인증번호가 확인되었습니다.").build()).orElse(FindIdResponseDto.builder().email(null).message("❌ 인증번호가 일치하지 않거나, 만료되었습니다.").build());
    }


    public List<MyPageReservationDto> getReservationsForUser(String email) {
        List<Reservation> reservations = reservationRepository.findByUserEmailOrderByPaymentDateDesc(email);

        return reservations.stream()
                .map(reservation -> MyPageReservationDto.of(reservation, reservation.getPerformance()))
                .toList(); // Java 16+
        // Java 8 사용 시
        // .collect(Collectors.toList());
    }

    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new CustomException(ExceptionContent.NOT_FOUND_USER));
    }

    public PasswordResetResponseDto verifyResetCodeAndChangePassword(ResetPasswordRequest request) {
        final String normalizedPhone = request.getPhone().replace("-", "");

        Certification cert = certificationRepository.findTopByEmailAndPhoneOrderByCreatedAtDesc(request.getEmail(), normalizedPhone).orElse(null);

        if (cert == null || !bCryptPasswordEncoder.matches(request.getCode(), cert.getCertificationNumber())) {
            return PasswordResetResponseDto.builder().message("❌ 인증번호가 일치하지 않습니다.").build();
        }

        if (cert.getCreatedAt().isBefore(LocalDateTime.now().minusMinutes(CERTIFICATION_EXPIRATION_MINUTES))) {
            return PasswordResetResponseDto.builder().success(false).message("❌ 인증번호가 만료되었습니다.").build();
        }

        User user = userRepository.findByEmail(request.getEmail()).filter(m -> normalizedPhone.equals(m.getPhone().replace("-", ""))).filter(m -> request.getBirthday().equals(m.getBirthday())).orElse(null);

        if (user == null) {
            return PasswordResetResponseDto.builder().success(false).message("❌ 가입된 사용자를 찾을 수 없습니다.").build();
        }

        if (!request.getNewPassword().equals(request.getNewPasswordCheck())) {
            return PasswordResetResponseDto.builder().success(false).message("❌ 새 비밀번호가 일치하지 않습니다.").build();
        }

        user.setPassword(bCryptPasswordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        return PasswordResetResponseDto.builder().success(true).message("✅ 비밀번호가 성공적으로 변경되었습니다.").build();
    }


    private String generateCertificationCode() {
        return String.format("%06d", (int) (Math.random() * 1_000_000));
    }

    private boolean isValidCertification(Certification cert, String inputCode) {
        return bCryptPasswordEncoder.matches(inputCode, cert.getCertificationNumber()) && cert.getCreatedAt().isAfter(LocalDateTime.now().minusMinutes(CERTIFICATION_EXPIRATION_MINUTES));
    }

}
