package com.uhihi.diary.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;

@Slf4j
public class MailSender {
    private final JavaMailSender mailSender;
    private static final String FROM_ADDRESS = "uhihidiary@gmail.com";

    public MailSender(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public boolean mailSend(String userEmail, String text){
        MimeMessage message = mailSender.createMimeMessage();
        try{
            MimeMessageHelper messageHelper = new MimeMessageHelper(message, true, "UTF-8");
            messageHelper.setTo(userEmail);
            messageHelper.setFrom(FROM_ADDRESS, "UHIHI DIARY");
            messageHelper.setSubject("UHIHI DIARY 인증 메일");
            messageHelper.setText(text, true);
            mailSender.send(message);
            return true;
        }catch(Exception e) {
            log.error("fail to send check code for userEmail");
            return false;
        }
    }
}
