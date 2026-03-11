package com.malgn.configure.global.util;

import org.springframework.security.core.userdetails.UserDetails;

public class SecurityUtils {
    public static boolean isAdmin(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ADMIN"));
    }
}
