package com.example.zipzip.Entity;

public enum Permitions {

    USER("USER"), ADMIN("ADMIN");

    String p;

    Permitions(String p) {
        this.p = p;
    }

    public String getP() {
        return p;
    }
}
