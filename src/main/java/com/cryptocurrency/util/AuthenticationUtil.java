package com.cryptocurrency.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

import java.util.Map;

public class AuthenticationUtil {

    public static String getUsernameFromAuthentication(Authentication authentication) {
        try {
            Map<String, Object> principal = ((OAuth2AuthenticationToken) authentication).getPrincipal().getAttributes();
            return (String) principal.get("email");
        } catch (Exception e) {
            return authentication.getName();
        }
    }
}
