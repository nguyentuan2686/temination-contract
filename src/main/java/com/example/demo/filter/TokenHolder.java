package com.example.demo.filter;

import org.springframework.stereotype.Component;

@Component
public class TokenHolder {
    private static String token;
    private static String refreshToken;

    public static synchronized void setToken(String t) {
        token = t;
    }

    public static synchronized void setRefreshToken(String rt) {
        refreshToken = rt;
    }

    public static String getToken() {
        return token;
    }

    public static String getRefreshToken() {
        return refreshToken;
    }
}

