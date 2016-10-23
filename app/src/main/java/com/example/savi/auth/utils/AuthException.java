package com.example.savi.auth.utils;

public class AuthException extends Exception {
    private int code;
    private String message ;

    public AuthException(int code) {
        this(code, "Something wrong.Please try after sometime.");
    }

    public AuthException(String message) {
        this(-1, message);
    }

    public AuthException(int code, String message) {
        this.code = code;
        this.message = message != null ? message : this.message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

}
