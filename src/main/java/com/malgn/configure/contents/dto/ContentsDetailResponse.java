package com.malgn.configure.contents.dto;

import com.malgn.configure.contents.entity.Contents;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ContentsDetailResponse {

    private Long id;
    private String title;
    private String description;
    private Long viewCount;
    private String createdBy;
    private String lastModifiedBy;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;

    public static ContentsDetailResponse of(Contents contents) {
        return new ContentsDetailResponse(
                contents.getId(),
                contents.getTitle(),
                contents.getDescription(),
                contents.getViewCount(),
                contents.getCreatedBy(),
                contents.getLastModifiedBy(),
                contents.getCreatedDate(),
                contents.getLastModifiedDate()
        );
    }
}
