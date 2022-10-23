package com.uhihi.diary.controllers;

import com.uhihi.diary.model.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController // controller의 기능을 수행한다.
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

    @RequestMapping("/auth/emailcode")
    public ResponseEntity auth_email_code(@RequestParam(value="email") String userEmail) {
        /*
            check email repetition
         */
        if(userService.checkUserRepeat(userEmail) == true){
            return new ResponseEntity(HttpStatus.OK);
        }
        return new ResponseEntity(HttpStatus.BAD_REQUEST);
    }
}
