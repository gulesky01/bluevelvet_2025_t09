package com.musicstore.bluevelvet.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class CustomSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication)
            throws IOException {

        String redirectURL = "/";

        for (GrantedAuthority auth : authentication.getAuthorities()) {
            String role = auth.getAuthority();

            if (role.equals("ROLE_Editor")){
                redirectURL = "/categories";
                break;
            }

            if (role.equals("ROLE_Administrator") ||
                    role.equals("ROLE_SalesManager") ||
                    role.equals("ROLE_ShippingManager")) {

                redirectURL = "/dashboard";
                break;
            }
        }

        response.sendRedirect(redirectURL);
    }
}
