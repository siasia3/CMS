package com.malgn.configure.contents.dto;

import com.malgn.configure.contents.entity.Contents;
import com.malgn.configure.user.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;


@Getter
@AllArgsConstructor
public class ContentsCreateRequest {
    @NotBlank
    private String title;
    private String description;

    public Contents toEntity(User user) {
        return Contents.builder()
                .title(this.title)
                .description(this.description)
                .viewCount(0L)
                .createdBy(user.getUsername())
                .user(user)
                .build();

    }
}
