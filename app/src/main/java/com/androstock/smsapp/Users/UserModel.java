package com.androstock.smsapp.Users;



public class UserModel {

    private String functionsassigned;
    private String userkey;
    private String userphone;
    private String name;

    public UserModel(String key,String userphone,String name) {

      //  this.functionsassigned=functionsassigned;
        this.userphone=userphone;
        this.userkey=key;
        this.name=name;


    }

    public String getName() {
        return name;
    }

    public String getUserkey() {
        return userkey;
    }

    public void setUserkey(String userkey) {
        this.userkey = userkey;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserid() {
        return userkey;
    }

    public void setUserid(String userid) {
        this.userkey = userid;
    }

    public String getUserphone() {
        return userphone;
    }

    public void setUserphone(String userphone) {
        this.userphone = userphone;
    }

    public String getFunctionsassigned() {
        return functionsassigned;
    }

    public void setFunctionsassigned(String functionsassigned) {
        this.functionsassigned = functionsassigned;
    }
}
