package com.malgn.configure.contents.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ContentsSearchCondition {
    private String title;
    private String sortBy;
}
