package com.uhihi.diary.dto;
import lombok.Data;

@Data
public class User {
    private int userId;
    private String email;
    private String password;
    private String nickname;
    private String selfInfo;

}
