package com.uhihi.diary.dao;

import com.uhihi.diary.dto.User;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM person")
    List<User> findAll();

    @Select("SELECT COUNT(email) FROM person WHERE email = #{userEmail}")
    int checkEmailRepeat(@Param("userEmail") String userEmail);

    @Insert("INSERT INTO auth_email(email, code) VALUES(#{userEmail}, #{code})")
    int insertCheckCode(@Param("userEmail") String userEmail, @Param("code") String code);

    @Delete("DELETE from auth_email where email = #{userEmail}");
    int deleteCode(@Param("userEmail") String userEmail);
}
