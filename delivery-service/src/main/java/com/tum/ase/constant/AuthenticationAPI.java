package com.tum.ase.constant;

public enum AuthenticationAPI {
    PEM("/auth/pem"),

    CSRF("/auth/csrf"),
    SAVE("/credential/save"),
    UPDATE("/credential/update/"),
    DELETE("/credential/delete");

    private String api;
    AuthenticationAPI(String endpoint) {
        this.api = endpoint;
    }

    public String getAPI() {
        return api;
    }
}
