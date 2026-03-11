package com.malgn.configure.contents.controller;

import com.malgn.configure.contents.dto.*;
import com.malgn.configure.contents.service.ContentsService;
import com.malgn.configure.global.common.ApiResponse;
import com.malgn.configure.user.dto.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
public class ContentsController {

    private final ContentsService contentsService;


    @GetMapping("/contents")
    public ResponseEntity<ApiResponse<Page<ContentsListResponse>>> getContentsList(@ModelAttribute ContentsSearchCondition searchCondition,
                                @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable){
        if(pageable.getPageSize() > 10) {
            pageable = PageRequest.of(pageable.getPageNumber(), 10, pageable.getSort());
        }
        Page<ContentsListResponse> contentsList = contentsService.findContentsList(searchCondition, pageable);
        return ResponseEntity.ok(ApiResponse.success(contentsList));
    }

    @GetMapping("/contents/{contentsId}")
    public ResponseEntity<ApiResponse<ContentsDetailResponse>> getContents(@PathVariable Long contentsId){
        ContentsDetailResponse contentsDetail = contentsService.findContentsDetail(contentsId);
        return ResponseEntity.ok(ApiResponse.success(contentsDetail));
    }

    @PostMapping("/contents")
    public ResponseEntity<ApiResponse<Long>> createContents(@RequestBody @Valid ContentsCreateRequest contentsCreateRequest,
                                                            @AuthenticationPrincipal CustomUserDetails userDetails){
        Long createdContentsId = contentsService.saveContent(contentsCreateRequest, userDetails.getUserId());
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.success(createdContentsId));
    }

    @PatchMapping("/contents/{contentsId}")
    public ResponseEntity<ApiResponse<ContentsUpdateResponse>> updateContents(@RequestBody @Valid ContentsUpdateRequest contentsUpdateRequest,
                               @PathVariable Long contentsId,
                               @AuthenticationPrincipal CustomUserDetails userDetails){
        ContentsUpdateResponse contentsUpdateResponse = contentsService.modifyContent(contentsUpdateRequest, contentsId, userDetails);
        return ResponseEntity.ok(ApiResponse.success(contentsUpdateResponse));
    }

    @DeleteMapping("/contents/{contentsId}")
    public ResponseEntity<Void> deleteContents(@PathVariable Long contentsId,
                                               @AuthenticationPrincipal CustomUserDetails userDetails){
        contentsService.removeContent(contentsId,userDetails);
        return ResponseEntity.noContent().build();
    }
}
