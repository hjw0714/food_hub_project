package com.application.foodhub.user;

import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import lombok.Data;

@Data
public class UserDTO {
    private String userId;
    private String profileOriginal;
    private String profileUUID;
    private String nickname;
    private String passwd;
    private String email;
    private String tel;
    private String gender;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date birthday;
    private String membershipType;
    @DateTimeFormat(pattern="yyyy-MM-dd")
    private Date foundingAt;
    private String businessType;
    private String smsYn = "N";
    private String emailYn = "N";
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date joinAt;
    @DateTimeFormat(pattern="yyyy-MM-dd-hh-mm")
    private Date modifyAt;
}
