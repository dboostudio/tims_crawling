package com.example.timsCrawler.domain;

import jakarta.servlet.http.Cookie;
import lombok.Getter;
import lombok.Setter;

import java.util.Map;
import java.util.Set;

@Getter
@Setter
public class Member {
    private String username;
    private String password;
    private String company;
    Map<String, String> loginCookie;
    Cookie cookie;

    public void nullifyLoginData(){
        this.username = null;
        this.password = null;
    }

    public void setResponseCookie(){
        Set<String> keySet = loginCookie.keySet();
//        cookie = new Cookie();
        for (String key : keySet) {
            cookie = new Cookie(key, loginCookie.get(key));
        }
    }
}
