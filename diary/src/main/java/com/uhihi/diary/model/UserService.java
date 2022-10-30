package com.uhihi.diary.model;

import com.uhihi.diary.dao.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
@Slf4j
public class UserService {
    private final UserMapper userMapper;
    private MailSender mailSender;

    public UserService(UserMapper userMapper){
        this.userMapper = userMapper;
    }

    public int authEmailCode(String userEmail){
        if(!checkFormat(0, userEmail)){
            log.error(String.format("/UserService/authEmailCode: checkFormat wrong[%s]", userEmail));
            return 2; // bad request wrong email format
        }
        try{
            int status = checkEmailRepeat(userEmail);
            if(status == 1){
            /*
                no repeat
                - make check code and save data
                - send mail
             */
                log.info("checkUserRepeat: count 0");
                // make and save code
                int reStatus;
                String code = generateCode();
                reStatus = userMapper.insertCheckCode(userEmail, code);
                if(reStatus == 0){
                    log.error("checkUserRepeat: fail to insert code");
                    return 3; //  status: 500
                }
                else {
                    log.info(String.format("checkUserRepeat: [code: %s]", code));
                    if(!mailSendAuthCode(userEmail, code)){
                        log.error("fail to send email");
                        userMapper.deleteCode(userEmail);
                        return 3; // status 500
                    }
                    return 1; // no error
                }
            }

            log.info(String.format("authEmailCode: wrong email(2), fail to check(3) >> %d", status));
            return status;
        }
        catch (Exception e){
//            log.error(e.printStackTrace());
            e.printStackTrace();
            log.error("/UserService/authEmailCode: fail to insert or delete authCode");
            return 3;
        }
    }

    public boolean checkFormat(int dataType, String data){
        if(dataType == 0){ // check email
            return  Pattern.matches("^[0-9a-zA-Z]([-_]?[0-9a-zA-Z])*@[0-9a-zA-Z.]*\\.[a-zA-Z]{2,3}$", data);
        }
        else if(dataType == 1){ // check password
            return  Pattern.matches("^(?=.*[0-9])(?=.*[a-zA-Z]).{8,20}$",data) && !Pattern.matches("(.*)[^0-9a-zA-Z`~!@#$%^&*()-=_+](.*)",data);
        }
        else if(dataType == 2){ // check nickname
            return  Pattern.matches("^[a-zA-Z가-힇0-9]{2,8}$", data);
        }
        return true;
    }

    private int checkEmailRepeat(String userEmail){
        if(userEmail == null) {
            log.error(String.format("/UserService/checkEmailRepeat: wrong parameter[userEmail=%s]"), userEmail);
            return 2;
        }
        try{
            if(userMapper.countEmailRepeat((userEmail)) == 0) return 1; // no repeat
            return 2; // repeat
        }
        catch (Exception e){
            return 3; // 500
        }
    }

    private  String generateCode(){
        int random_number = (int) ((Math.random() * (999999 - 100000)) + 100000);
        return String.format("%06d",random_number);
    }
    private boolean mailSendAuthCode(String userEmail, String code) {
        if(userEmail == null || code == null ) {
            log.error("/UserService/mailSendAuthCode: wrong parameter: ");
            return false;
        }
        String text = "\"<head><meta charset=\"utf-8\"></head><body><div class=\"mailbox\" style=\"background-color: antiquewhite;width:100%;text-align:center;\"><div class=\"centerbox\" style=\"margin-top:30px;margin-bottom:30px;width:450px;padding-top:10px;padding-bottom:10px;text-align: center;display:inline-block;\"><div class=\"title\" style=\"margin-bottom:3px;\"><h2 style=\"margin:0;font-size:1rem;color:#F28482;letter-spacing:-0.5px\"><b>UHIHI DIARY</b></h2></div><div class=\"content\" style=\"padding-top:20px;padding-bottom:50px;background-color:white;\"><h2 style=\"font-size:1.5rem; margin:20px 0px;padding-bottom:10px;width:300px;margin-left:75px;border-bottom:2px solid #eb9290\">회원가입 인증코드 안내</h2><h5 style=\"margin:40px 0px; font-size: 0.9rem\">안녕하세요! UHIHI DIARY입니다^-^<br/>아래 인증코드 6자리를 입력하고 인증을 완료해주세요<br/></h5><h2 style=\"font-size:1.2rem\">인증코드</h2><div class=\"codebox\" style=\"width:300px;padding-top:5px;padding-bottom:5px;border-radius: 5px;margin:0 auto;color: white;background-color: #F28482;\"><h1 style=\"margin:17px 0px; font-size:2.5rem; letter-spacing:2.5px\">"+code+"</h1></div><h6 style=\"color:gray;margin-top:25px;margin-bottom:25px;font-size:0.5rem\">인증코드는 <b>이메일 발송시점으로부터 10분동안</b> 유효합니다.<br/>그 전에 회원가입을 완료해주세요!</h6></div></div></div></body>\"";
        return mailSender.mailSend(userEmail, text);
    }

    public int checkEmailCode(String userEmail, String code) {
        if(userEmail == null || code == null){
            log.error(String.format("/UserService/checkEmailCode: wrong parameter[userEmail=%s, code=%s]:"), userEmail, code);
            return 2; // 400
        }
        try {
            if(!checkFormat(0, userEmail)) return 2; // 400 wrong request
            if(userMapper.countEmailCode(userEmail, code) == 1) return 1; // success
            return 2; // 400
        }
        catch(Exception e){
            log.error("UserService/checkEmailCode: fail to check Emailcode");
            return 3; // 500
        }

    }

    public String checkRegisterInfo(String userEmail, String password, String nickname, String code){
        int status=0;
        // email repeat - error[4]
        status = checkEmailRepeat(userEmail);
        if(status == 2) return "EMAIL_ERROR";
        else if(status == 3) return "INTERNAL_SERVER_ERROR";

        // password format - error[5]
        if(!checkFormat(1, password)) return "PASSWORD_ERROR";

        // nickname format - error[6]
        if(!checkFormat(2, nickname)) return "NICKNAME_ERROR";

        // authCode - error[7]
        status = checkEmailCode(userEmail, code);
        if(status == 1){
            try{
                userMapper.deleteCode(userEmail);
                return "OK";
            }
            catch (Exception e){
                return "INTERNAL_SERVER_ERROR";
            }
        }
        else if(status == 2) return "CODE_ERROR";
        else return "INTERNAL_SERVER_ERROR"; // 500
    }

    public boolean registerUser(String userEmail, String password, String nickname, String selfInfo){
        try{
            userMapper.insertRegisterUser(userEmail, password, nickname, selfInfo);
            return true;
        }
        catch(Exception e){
            log.error("/UserService/registerUser: fail to insert user in person");
            return false;
        }
    }


}
