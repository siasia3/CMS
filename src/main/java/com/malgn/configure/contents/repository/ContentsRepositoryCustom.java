package com.malgn.configure.contents.repository;

import com.malgn.configure.contents.dto.ContentsSearchCondition;
import com.malgn.configure.contents.dto.ContentsListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ContentsRepositoryCustom {

    /**
     * contents 목록 조회 ( 검색 + 정렬 + 페이징)
     * @param searchCondition 검색 조건
     * @param pageable 페이징 정보
     * @return 페이징 콘텐츠 목록
     */
    public Page<ContentsListResponse> findContentsWithCondition(ContentsSearchCondition searchCondition, Pageable pageable);

}
