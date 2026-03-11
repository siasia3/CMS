package com.malgn.configure.contents.dto;

import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
public class ContentsListResponse {

    private final Long id;
    private final String title;
    private final Long viewCount;
    private final String createdBy;
    private final LocalDateTime createdDate;
    private final Long userId;
    private final String nickname;
    private final LocalDateTime deletedAt;

    @QueryProjection
    public ContentsListResponse(Long id, String title, Long viewCount, String createdBy,
                                LocalDateTime createdDate, Long userId, String nickname,LocalDateTime deletedAt) {
        this.id = id;
        this.title = title;
        this.viewCount = viewCount;
        this.createdBy = createdBy;
        this.createdDate = createdDate;
        this.userId = userId;
        this.nickname = nickname;
        this.deletedAt = deletedAt;
    }
}
