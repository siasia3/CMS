package com.malgn.configure.contents.service;

import com.malgn.configure.contents.dto.*;
import com.malgn.configure.contents.entity.Contents;
import com.malgn.configure.contents.repository.ContentsRepository;
import com.malgn.configure.global.exception.custom.BusinessException;
import com.malgn.configure.global.exception.errorcode.ErrorCode;
import com.malgn.configure.user.dto.CustomUserDetails;
import com.malgn.configure.user.entity.User;
import com.malgn.configure.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ContentsService {

    private final ContentsRepository contentsRepository;
    private final UserService userService;

    //컨텐츠 목록 조회
    @Transactional(readOnly = true)
    public Page<ContentsListResponse> findContentsList(ContentsSearchCondition searchCondition, Pageable pageable){
        return contentsRepository.findContentsWithCondition(searchCondition, pageable);
    }

    //컨텐츠 상세 조회
    @Transactional
    public ContentsDetailResponse findContentsDetail(Long contentsId){
        Contents findContent = contentsRepository.findById(contentsId).orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));
        if (findContent.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.ALREADY_DELETED);
        }
        findContent.increaseViewCount();
        return ContentsDetailResponse.of(findContent);
    }

    //컨텐츠 추가
    @Transactional
    public Long saveContent(ContentsCreateRequest contentsCreateRequest, Long userId){
        User user = userService.findUser(userId);
        Contents contents = contentsCreateRequest.toEntity(user);
        Contents savedContent = contentsRepository.save(contents);
        return savedContent.getId();
    }

    //컨텐츠 수정
    @Transactional
    public ContentsUpdateResponse modifyContent(ContentsUpdateRequest contentsUpdateRequest, Long contentsId, CustomUserDetails userDetails) {
        Contents findContents = contentsRepository.findById(contentsId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        if (!userDetails.getRole().equals("ADMIN") && !findContents.getUser().getId().equals(userDetails.getUserId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        if (findContents.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.ALREADY_DELETED);
        }

        findContents.updateContents(contentsUpdateRequest.getTitle(),contentsUpdateRequest.getDescription(), userDetails.getUsername());

        return ContentsUpdateResponse.of(findContents);
    }

    //컨텐츠 soft delete
    @Transactional
    public void removeContent(Long contentsId, CustomUserDetails userDetails){

        Contents findContents = contentsRepository.findById(contentsId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONTENT_NOT_FOUND));

        if (!userDetails.getRole().equals("ADMIN") && !findContents.getUser().getId().equals(userDetails.getUserId())) {
            throw new BusinessException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
        if (findContents.getDeletedAt() != null) {
            throw new BusinessException(ErrorCode.ALREADY_DELETED);
        }
        findContents.deleteContents();
    }
}
