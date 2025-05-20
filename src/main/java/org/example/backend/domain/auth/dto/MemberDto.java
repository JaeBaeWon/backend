package org.example.backend.domain.auth.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.example.backend.domain.user.entity.User;

@Builder
@Getter
@Setter
public class MemberDto {
    private Long id;
    private String email;
    private String userName;   // 유저 이름
    private String phone;      // 연락처
    private String zipCode;    // 우편번호
    private String streetAdr;  // 기본 주소
    private String detailAdr;  // 상세 주소

    public static MemberDto of(User user) {
        return MemberDto.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .userName(user.getUsername())
                .phone(user.getPhone())
                .zipCode(user.getZipCode())
                .streetAdr(user.getStreetAdr())
                .detailAdr(user.getDetailAdr())
                .build();
    }
}
