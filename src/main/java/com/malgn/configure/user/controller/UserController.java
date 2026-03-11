package com.malgn.configure.user.controller;

import com.malgn.configure.global.common.ApiResponse;
import com.malgn.configure.user.dto.RegisterRequestDto;
import com.malgn.configure.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseEntity<ApiResponse<Long>> register(@RequestBody @Valid RegisterRequestDto registerRequestDto){

        Long userId = userService.register(registerRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(userId));
    }

}
