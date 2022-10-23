package com.uhihi.diary.model;

import com.uhihi.diary.dao.UserMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class UserService {
    private UserMapper userMapper;

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
            return true;
        }
        log.info("checkUserRepeat: count more than 1");
        return false; // repeat email
    }
}
