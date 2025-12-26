package com.wlyykf.mall.service;

import com.wlyykf.mall.dto.CategoryDTO;
import com.wlyykf.mall.vo.CategoryVO;
import com.wlyykf.mall.vo.ResponseVO;

import java.util.List;

public interface CategoryService {

    /**
     * 添加分类
     * @param categoryDTO 分类信息
     */
    ResponseVO<Void> addCategory(CategoryDTO categoryDTO);

    /**
     * 修改分类
     * @param categoryDTO 分类信息
     */
    ResponseVO<Void> updateCategory(CategoryDTO categoryDTO);

    /**
     * 删除分类
     * @param categoryId 分类id
     */
    ResponseVO<Void> deleteCategory(Long categoryId);

    /**
     * 根据父类查子分类
     * @param parentId 父类id
     * @return 子分类列表
     */
    ResponseVO<List<CategoryVO>> getChildren(Long parentId);
}
