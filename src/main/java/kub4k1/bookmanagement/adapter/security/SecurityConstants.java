package kub4k1.bookmanagement.adapter.security;

import lombok.Getter;

public enum SecurityConstants {

    ACCESS_TOKEN_HEADER("Authorization"),

    REFRESH_TOKEN_HEADER("Refresh_token"),

    TOKEN_PREFIX("Bearer "),

    TOKEN_ISSUER("book-management-user"),

    TOKEN_AUDIENCE("book-management-app");

    SecurityConstants(String constant) {
        this.constant = constant;
    }

    @Getter
    final String constant;
}
