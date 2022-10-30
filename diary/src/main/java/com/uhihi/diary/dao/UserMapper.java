package com.uhihi.diary.dao;

import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {
    /*
    @Select("SELECT * FROM person")
    List<User> findAll();
   */

    /*   /auth/emailcode   */
    @Select("SELECT COUNT(email) FROM person WHERE email = #{userEmail}")
    int countEmailRepeat(@Param("userEmail") String userEmail);

    @Insert("REPLACE INTO auth_email(email, code, timestamp) VALUES(#{userEmail}, #{code}, now())")
    int insertCheckCode(@Param("userEmail") String userEmail, @Param("code") String code);

    // delete auth code
    @Delete("DELETE from auth_email where email = #{userEmail}")
    int deleteCode(@Param("userEmail") String userEmail);

    /*   /auth/emailcheck   */
    @Select("SELECT COUNT(email) FROM person WHERE email = #{userEmail} AND code = #{userCode}")
    int countEmailCode(@Param("userEmail") String userEmail, @Param("userCode") String userCode);

    /*   /auth/register   */
    @Insert("INSERT INTO person(email, password, nickname, selfinfo) VALUES(#{userEmail}, #{password}, #{nickname}, #{selfInfo})")
    int insertRegisterUser(@Param("userEmail") String userEmail, @Param("password") String password, @Param("nickname") String nickname, @Param("selfInfo") String selfInfo);

    @Update("UPDATE person SET refreshtoken = #{'token'}, tokenenddate=DATE_ADD(NOW(), INTERVAL 30 DAY) WHERE email=#{userEmail}")
    int updateRefreshToken(@Param("token") String token, @Param("userEmail") String userEmail);
}
