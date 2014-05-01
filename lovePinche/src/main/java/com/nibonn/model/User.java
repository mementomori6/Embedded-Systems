package com.nibonn.model;

import android.os.Bundle;

/**
 * Created by GuYifan on 2014/4/28.
 */
public class User {

    private String userid;
    private String username;
    private String password;
    private String realname;
    private String idcard;
    private String phonenumber;

    public User() {
    }

    public User(Bundle data) {
        setUsername(data.getString("username"));
        setPassword(data.getString("password"));
        setRealname(data.getString("realname"));
        setUserid(data.getString("userid"));
        setPhonenumber(data.getString("phonenumber"));
        setIdcard(data.getString("idcard"));
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getIdcard() {
        return idcard;
    }

    public void setIdcard(String idcard) {
        this.idcard = idcard;
    }

    public String getPhonenumber() {
        return phonenumber;
    }

    public void setPhonenumber(String phonenumber) {
        this.phonenumber = phonenumber;
    }
}