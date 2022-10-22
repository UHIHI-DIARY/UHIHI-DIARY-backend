package com.uhihi.diary;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;


@Controller // controller의 기능을 수행한다.
public class TestController {
    /*
        @RequestMapping : URL과 method를 mapping한다.
          -- http:~/test url요청이 발생하면 testmethod가 실행된다.
     */
    @RequestMapping("/test")
    @ResponseBody
    public String test() {
        return "TEST PAGE spring boot";
    }
}