package com.malgn.configure.auth.service;

import com.malgn.configure.auth.dto.LoginResponse;
import com.malgn.configure.global.exception.custom.BusinessException;
import com.malgn.configure.global.exception.errorcode.ErrorCode;
import com.malgn.configure.global.jwt.JwtUtil;
import com.malgn.configure.user.dto.CustomUserDetails;
import com.malgn.configure.user.entity.User;
import com.malgn.configure.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtUtil jwtUtil;

    /**
     로그인 처리 후 JWT 토큰 반환
     */
    @Transactional(readOnly = true)
    public LoginResponse login(String username, String password){
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(username, password)
        );
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        Long userId = userDetails.getUserId();

        String accessToken = jwtUtil.createJwt(username, userDetails.getRole(),userId, 1000 * 60 * 60L);
        String refreshToken = jwtUtil.createRefreshToken(userId, 1000 * 60 * 60 * 24L);
        return new LoginResponse(accessToken,refreshToken);
    }

    public String refreshAccessToken(String refreshToken){
        if (!jwtUtil.isExpired(refreshToken)) {
            User user = userService.findUser(Long.parseLong(jwtUtil.getUserId(refreshToken)));
            String newAccessToken = jwtUtil.createJwt(user.getUsername(), user.getRole().name(),user.getId(), 60 * 60 * 1000L);
            return newAccessToken;
        } else {
            throw new BusinessException(ErrorCode.TOKEN_EXPIRED);
        }
    }

}
