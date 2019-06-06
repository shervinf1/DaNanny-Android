package com.example.dananny;

public class Users {

    private String name;
    private String phone;
    private  String email;

    Users(){
        name="";
        phone="";
        email="";
    }

    Users(String n, String p, String e){
        name=n;
        phone=p;
        email=e;
    }

    public void setName(String n){
        name=n;
    }
    public void setPhone(String p){
        phone = p;
    }
    public void setEmail(String e){
        email=e;
    }
    public String getName(){
        return name;
    }
    public String getPhone(){
        return phone;
    }
    public String getEmail(){
        return email;
    }
}
