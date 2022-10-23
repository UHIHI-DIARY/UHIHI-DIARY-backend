package com.uhihi.diary.model;

import com.uhihi.diary.dao.UserMapper;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private UserMapper userMapper;

    public UserService(UserMapper userMapper){
        this.userMapper = userMapper;
    }

    public boolean checkUserRepeat(String userEmail){
        if(userMapper.checkEmailRepeat((userEmail)) == 0){
            return true; // no repeat
        }
        return false; // repeat email
    }
}
