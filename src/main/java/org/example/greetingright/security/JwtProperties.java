package org.example.greetingright.security;

public class JwtProperties {

    // 30 minutes
    public static final int EXPIRATION_TIME = 1800_000;
    public static final int REFRESH_EXPIRATION_TIME = 2000_000;

    // The TOKEN_PREFIX constant is used to prefix the JWT in the Authorization header
    public static final String TOKEN_PREFIX = "Bearer ";

    // The HEADER_STRING constant is used to set the key of the Authorization header
    public static final String HEADER_STRING = "Authorization";
}
