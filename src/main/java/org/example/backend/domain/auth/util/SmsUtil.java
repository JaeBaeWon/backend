package org.example.backend.domain.auth.util;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import net.nurigo.sdk.NurigoApp;
import net.nurigo.sdk.message.model.Message;
import net.nurigo.sdk.message.request.SingleMessageSendingRequest;
import net.nurigo.sdk.message.response.SingleMessageSentResponse;
import net.nurigo.sdk.message.service.DefaultMessageService;
import org.springframework.stereotype.Component;

@Component
public class SmsUtil {

    @Value("${coolsms.api.key}")
    private String apiKey;

    @Value("${coolsms.api.secret}")
    private String apiSecretKey;

    @Value("${petharmony.phone.number}")
    private String phoneNumber;

    private DefaultMessageService messageService;

    @PostConstruct
    public void init() {
        this.messageService = NurigoApp.INSTANCE.initialize(apiKey, apiSecretKey, "https://api.coolsms.co.kr");
    }

    public SingleMessageSentResponse sendOne(String to, String certificationNumber) {
        Message message = new Message();
        message.setFrom(phoneNumber);
        message.setTo(to);
        message.setText(String.format("[Podopicker] 인증번호 : %s를 입력해주세요.", certificationNumber));

        return this.messageService.sendOne(new SingleMessageSendingRequest(message));
    }
}
