package com.malgn.configure.user.dto;

import com.malgn.configure.user.enums.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class RegisterRequestDto {

    @NotBlank(message = "아이디를 입력해주세요.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    private String password;

    @NotNull(message = "권한을 입력해주세요.")
    private Role role;

    @NotBlank(message = "닉네임을 입력해주세요.")
    private String nickname;
}