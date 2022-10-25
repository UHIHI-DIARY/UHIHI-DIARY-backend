package com.uhihi.diary.model;

import com.uhihi.diary.dao.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.internet.MimeMessage;

@Service
@Slf4j
public class UserService {
    private final UserMapper userMapper;
    private final JavaMailSender mailSender = null;
    private static final String FROM_ADDRESS = "uhihidiary@gmail.com";

    public UserService(UserMapper userMapper){
        this.userMapper = userMapper;
    }

    public boolean checkUserRepeat(String userEmail){
        if(userMapper.checkEmailRepeat((userEmail)) == 0){
            /*
                no repeat
                - make check code and save data
                - send mail
             */
            log.info("checkUserRepeat: count 0");
            // make check code
            int reStatus;
            int random_number = (int) ((Math.random() * (999999 - 100000)) + 100000);
            String code = String.format("%06d",random_number);
            reStatus = userMapper.insertCheckCode(userEmail, code);
            if(reStatus == 0){
                log.error("checkUserRepeat: fail to insert code");
                return false;
            }
            else {
                log.info(String.format("checkUserRepeat: [code: %s]", code));
                if(!mailSend(userEmail, code)){
                    log.error("fail to send email");
                    userMapper.deleteCode(userEmail);
                    return false;
                }
            }

            return true;
        }
        log.info("checkUserRepeat: count more than 1");
        return false; // repeat email
    }

    public boolean mailSend(String userEmail, String code){

        String text = "\"<head><meta charset=\"utf-8\"></head><body><div class=\"mailbox\" style=\"background-color: antiquewhite;width:100%;text-align:center;\"><div class=\"centerbox\" style=\"margin-top:30px;margin-bottom:30px;width:450px;padding-top:10px;padding-bottom:10px;text-align: center;display:inline-block;\"><div class=\"title\" style=\"margin-bottom:3px;\"><h2 style=\"margin:0;font-size:1rem;color:#F28482;letter-spacing:-0.5px\"><b>UHIHI DIARY</b></h2></div><div class=\"content\" style=\"padding-top:20px;padding-bottom:50px;background-color:white;\"><h2 style=\"font-size:1.5rem; margin:20px 0px;padding-bottom:10px;width:300px;margin-left:75px;border-bottom:2px solid #eb9290\">회원가입 인증코드 안내</h2><h5 style=\"margin:40px 0px; font-size: 0.9rem\">안녕하세요! UHIHI DIARY입니다^-^<br/>아래 인증코드 6자리를 입력하고 인증을 완료해주세요<br/></h5><h2 style=\"font-size:1.2rem\">인증코드</h2><div class=\"codebox\" style=\"width:300px;padding-top:5px;padding-bottom:5px;border-radius: 5px;margin:0 auto;color: white;background-color: #F28482;\"><h1 style=\"margin:17px 0px; font-size:2.5rem; letter-spacing:2.5px\">"+code+"</h1></div><h6 style=\"color:gray;margin-top:25px;margin-bottom:25px;font-size:0.5rem\">인증코드는 <b>이메일 발송시점으로부터 10분동안</b> 유효합니다.<br/>그 전에 회원가입을 완료해주세요!</h6></div></div></div></body>\"";
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
