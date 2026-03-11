package com.malgn.configure.user.dto;

import com.malgn.configure.user.entity.User;
import com.malgn.configure.user.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails{

    private final Long userId;
    private final String username;
    private final String password;
    private final String role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(()-> this.role);
    }

    @Override
    public @Nullable String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    public Long getUserId() {
        return this.userId;
    }

    public CustomUserDetails(User user) {
        this.userId = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.role = user.getRole().name();
    }

    public CustomUserDetails(Long userId, String username, String role) {
        this.role = role;
        this.username = username;
        this.userId = userId;
        this.password = null;
    }
}
