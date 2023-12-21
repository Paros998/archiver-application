package com.pd.archiver.application.security.jwt;

import lombok.Getter;

/**
 * The enum Jwt expire.
 */
@Getter
public enum JwtExpire {
    /**
     * Access token jwt expire.
     */
    ACCESS_TOKEN(60 * 60 * 1000);

    private final Integer amount;

    JwtExpire(Integer amount) {
        this.amount = amount;
    }
}
