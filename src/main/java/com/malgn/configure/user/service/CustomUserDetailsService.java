package com.malgn.configure.user.service;

import com.malgn.configure.user.dto.CustomUserDetails;
import com.malgn.configure.user.entity.User;
import com.malgn.configure.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    /**
     로그인 ID로 사용자 정보 조회
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("찾을 수 없는 사용자입니다: " + username));

        return new CustomUserDetails(user);
    }
}
