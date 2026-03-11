package com.malgn.configure.contents.repository;

import com.malgn.configure.contents.dto.ContentsSearchCondition;
import com.malgn.configure.contents.dto.ContentsListResponse;
import com.malgn.configure.contents.dto.QContentsListResponse;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;


import java.util.List;

import static com.malgn.configure.contents.entity.QContents.*;
import static com.malgn.configure.user.entity.QUser.*;

@Repository
@RequiredArgsConstructor
public class ContentsRepositoryImpl implements ContentsRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    /**
     * contents 목록 조회
     */
    @Override
    public Page<ContentsListResponse> findContentsWithCondition(ContentsSearchCondition searchCondition, Pageable pageable) {

        OrderSpecifier<?> orderSpecifier = orderSpecifier(searchCondition.getSortBy());
        BooleanExpression titleContains = titleContains(searchCondition.getTitle());

        List<ContentsListResponse> contentsList = queryFactory.select(new QContentsListResponse(
                        contents.id,
                        contents.title,
                        contents.viewCount,
                        contents.createdBy,
                        contents.createdDate,
                        user.id,
                        user.nickname,
                        contents.deletedAt
                ))
                .from(contents)
                .join(contents.user, user)
                .where(titleContains,notDeleted())
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long contentsCount = queryFactory.select(contents.count())
                .from(contents)
                .where(titleContains,notDeleted())
                .fetchOne();

        return new PageImpl<>(contentsList,pageable,contentsCount);
    }

    //orderBy 조건
    private OrderSpecifier<?> orderSpecifier(String sortBy){
        if (StringUtils.hasText(sortBy) && sortBy.equals("viewCount")) {
            return contents.viewCount.desc();
        }
        return contents.createdDate.desc(); // null 또는 다른 값이면 최신순
    }

    private BooleanExpression titleContains(String title){
        if(title != null){
            return contents.title.contains(title);
        }
        return null;
    }

    private BooleanExpression notDeleted() {
        return contents.deletedAt.isNull();
    }
}
