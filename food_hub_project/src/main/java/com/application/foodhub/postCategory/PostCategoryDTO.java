package com.application.foodhub.postCategory;

import lombok.Data;

@Data
public class PostCategoryDTO {
    private Long categoryId;
    private String categoryNm;
    private Long subCateId;
}