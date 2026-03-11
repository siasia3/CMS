package com.malgn.configure.contents.entity;

import com.malgn.configure.global.common.BaseTimeEntity;
import com.malgn.configure.user.entity.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;


@Getter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Table(name = "Contents")
public class Contents extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String title;
    @Column(columnDefinition = "TEXT")
    private String description;
    @Column(nullable = false)
    private Long viewCount = 0L;
    @NotBlank
    @Column(nullable = false, length = 50)
    private String createdBy;
    @Column(length = 50)
    private String lastModifiedBy;
    private LocalDateTime deletedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    public void increaseViewCount() {
        this.viewCount++;
    }

    public void deleteContents() {
        this.deletedAt = LocalDateTime.now();
    }

    public void updateContents(String title, String description, String lastModifiedBy) {
        this.title = title;
        this.description = description;
        this.lastModifiedBy = lastModifiedBy;
    }
}
