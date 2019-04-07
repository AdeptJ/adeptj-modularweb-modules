package com.adeptj.modules.security.core.credential;

import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;

import static com.adeptj.modules.security.core.SecurityConstants.AUTH_SCHEME_BEARER;
import static com.adeptj.modules.security.core.SecurityConstants.HEADER_AUTHORIZATION;

public class BearerTokenCredential implements Credential {

    private static final int TOKEN_START_POS = 7;

    private String token;

    private BearerTokenCredential(String token) {
        this.token = token;
    }

    public static Credential from(HttpServletRequest request) {
        String token = request.getHeader(HEADER_AUTHORIZATION);
        if (StringUtils.isNotEmpty(token) && StringUtils.startsWith(token, AUTH_SCHEME_BEARER)) {
            return new BearerTokenCredential(StringUtils.substring(token, TOKEN_START_POS));
        }
        return null;
    }

    public String getToken() {
        return token;
    }
}