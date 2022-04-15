package com.webserver.entity;

import java.io.Serializable;

public class User implements Serializable {
    static final long serialVersionUID = 42L;
    private String username;
    private String password;
    private int age;
    private String nickname;


    public User(String username, String password) {
    }

    public User(String username, String password, int age, String nickname) {
        this.username = username;
        this.password = password;
        this.age = age;
        this.nickname = nickname;

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

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }




    @Override
    public String toString() {
        return "User{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", age='" + age + '\'' +
                ", nickname='" + nickname +
                '}';
    }
}
