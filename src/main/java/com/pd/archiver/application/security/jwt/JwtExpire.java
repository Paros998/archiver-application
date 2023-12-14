package com.pd.archiver.application.security.jwt;

import lombok.Getter;

@Getter
public enum JwtExpire {
    ACCESS_TOKEN(20 * 60 * 1000);

    private final Integer amount;

    JwtExpire(Integer amount) {
        this.amount = amount;
    }
}
