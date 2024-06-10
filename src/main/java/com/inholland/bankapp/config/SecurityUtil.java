package com.inholland.bankapp.config;

import org.apache.coyote.BadRequestException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;

import java.util.Objects;

public class SecurityUtil {

    private static String getCurrentUserRole() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getAuthorities() != null) {
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                return authority.getAuthority();
            }
        }
        return null;
    }
    
    public static void checkIfCustomer() throws BadRequestException {
        String getRole = getGetRole();
        if (!Objects.equals(getRole, "ROLE_CUSTOMER")) {
            throw new BadRequestException("Only customer can access this endpoint.");
        }
    }

    public static void checkIfEmployee() throws BadRequestException {
        getGetRole();
        if (!Objects.equals(getCurrentUserRole(), "ROLE_EMPLOYEE")) {
            throw new BadRequestException("Only employee can access this endpoint.");
        }
    }

    private static String getGetRole() {
        String getRole = getCurrentUserRole();
        System.out.println("getCurrentUserRole(): " + getRole);
        return getRole;
    }
    
}
