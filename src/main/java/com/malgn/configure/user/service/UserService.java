package com.malgn.configure.user.service;

import com.malgn.configure.global.exception.custom.BusinessException;
import com.malgn.configure.global.exception.errorcode.ErrorCode;
import com.malgn.configure.user.dto.RegisterRequestDto;
import com.malgn.configure.user.entity.User;
import com.malgn.configure.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final com.malgn.configure.user.repository.UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     사용자 등록
     */
    public Long register(RegisterRequestDto registerRequestDto) {
        if (userRepository.existsByUsername(registerRequestDto.getUsername())) {
            throw new BusinessException(ErrorCode.DUPLICATE_USERNAME);
        }

        User user = User.of(registerRequestDto.getUsername(), passwordEncoder.encode(registerRequestDto.getPassword()),
                registerRequestDto.getNickname(), registerRequestDto.getRole());

        User savedUser = userRepository.save(user);
        return savedUser.getId();
    }

    /**
     * 사용자 조회
     */
    public User findUser(Long userId){
        return userRepository.findById(userId).orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }
}