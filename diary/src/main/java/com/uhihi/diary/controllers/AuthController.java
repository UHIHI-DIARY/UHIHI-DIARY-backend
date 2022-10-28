package com.uhihi.diary.controllers;

import com.uhihi.diary.model.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;


@RestController // controller의 기능을 수행한다.
@Slf4j
public class AuthController {
    /*
    @RequestMapping : URL과 method를 mapping한다.
      -- http:~/test url요청이 발생하면 testmethod가 실행된다.
    */

    private UserService userService;

    public AuthController(UserService userService){
        this.userService = userService;
    }

    @RequestMapping("/test")
    public String test() {
        return "TEST PAGE spring boot";
    }

    @PostMapping("/auth/emailcode")
    public ResponseEntity authEmailCode(@RequestBody HashMap<String, Object> map){
        /*
            check email repetition and receive code
         */
        String userEmail = "defaultMail";
        int reStatus;
        if(map.size() != 1){
            log.error(String.format("/auth/emailcode: can't get userEmail we get %d object", map.size()));
            return new ResponseEntity("WRONG_REQUEST",HttpStatus.BAD_REQUEST);
        }
        userEmail = (String)map.get("email");
        reStatus = userService.authEmailCode(userEmail);
        if (reStatus == 1) {
            return new ResponseEntity(HttpStatus.OK);
        }
        else if(reStatus == 3){ // fail
            log.error(String.format("/auth/emailcode: fail to insert code in db or send mail"));
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        log.error(String.format("/auth/emailcode: wrong email %s", userEmail));
        return new ResponseEntity("EMAIL_ERROR",HttpStatus.BAD_REQUEST);
    }

    @PostMapping("/auth/emailcheck")
    public ResponseEntity authEmailCheck(@RequestBody HashMap<String, Object> map){
        /*
            check email code
         */
        String userEmail = "defaultMail";
        String code="defaultCode";
        int reStatus = 0;
        if(map.size() != 2){
            log.error(String.format("/auth/emailcheck: can't get userEmail we get %d object", map.size()));
            return new ResponseEntity("WRONG_REQUEST",HttpStatus.BAD_REQUEST);
        }
        userEmail = (String) map.get("email");
        code = (String) map.get("code");
        reStatus = userService.checkEmailCode(userEmail, code);
        if (reStatus == 1) {
            return new ResponseEntity(HttpStatus.OK);
        }
        else if(reStatus == 2) return new ResponseEntity("CODE_ERROR",HttpStatus.BAD_REQUEST);

        return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @PostMapping("/auth/register")
    public ResponseEntity authRegister(@RequestBody HashMap<String, Object> map){
        /*
            Register
            - check request info
                - email repeat
                - password format
                - nickname format
                - authCode
            - register user
            - generate token
         */
        String userEmail, password, nickname, code, status;
        if(map.size() != 4){
            log.error(String.format("/auth/register: can't get userEmail we get %d object", map.size()));
            return new ResponseEntity("WRONG_REQUEST",HttpStatus.BAD_REQUEST);
        }
        userEmail = (String)map.get("email");
        password = (String)map.get("password");
        nickname = (String)map.get("nickname");
        code = (String)map.get("code");

        status = userService.checkRegisterInfo(userEmail,password,nickname,code);
        if(status == "OK"){
            if(!userService.registerUser(userEmail, password, nickname,"")) return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
            return new ResponseEntity("token", HttpStatus.OK);
        }
        else if(status == "INTERNAL_SERVER_ERROR"){
            return new ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        else{
            return new ResponseEntity(status,HttpStatus.BAD_REQUEST);
        }
    }
}
