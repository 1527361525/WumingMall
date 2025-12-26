package com.wlyykf.mall.service.impl;

import com.wlyykf.mall.dto.CategoryDTO;
import com.wlyykf.mall.entity.Category;
import com.wlyykf.mall.mappers.CategoryMapper;
import com.wlyykf.mall.service.CategoryService;
import com.wlyykf.mall.vo.CategoryVO;
import com.wlyykf.mall.vo.ResponseVO;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public ResponseVO<Void> addCategory(CategoryDTO categoryDTO) {
        Category category = new Category();
        category.setName(categoryDTO.getName());
        category.setParentId(categoryDTO.getParentId());
        category.setSort(categoryDTO.getSort());
        if (categoryMapper.insert(category) > 0) {
            return ResponseVO.success("添加成功", null);
        }
        return ResponseVO.fail("添加失败", null);
    }

    @Override
    public ResponseVO<Void> updateCategory(CategoryDTO categoryDTO) {
        if (categoryDTO.getCategoryId() == null) {
            return ResponseVO.fail("参数错误", null);
        }
        Category category = new Category();
        category.setCategoryId(categoryDTO.getCategoryId());
        category.setName(categoryDTO.getName());
        category.setParentId(categoryDTO.getParentId());
        category.setSort(categoryDTO.getSort());
        categoryMapper.updateById(category);
        return ResponseVO.success("更新成功", null);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ResponseVO<Void> deleteCategory(Long categoryId) {
        // 递归删除所有分类以及子类下的商品
        categoryMapper.deleteRelatedProducts(categoryId);

        // 递归删除分类以及所有子分类
        categoryMapper.deleteCategoryRecursively(categoryId);

        return ResponseVO.success("删除成功", null);
    }

    @Override
    public ResponseVO<List<CategoryVO>> getChildren(Long parentId) {
        List<CategoryVO> categoryVOList = categoryMapper.getChildren(parentId);

        // 判断是否有子分类
        for (CategoryVO categoryVO : categoryVOList) {
            boolean hasChildren = categoryMapper.hasChildren(Long.valueOf(categoryVO.getCategoryId()));
            categoryVO.setHasChildren(hasChildren);
        }

        return ResponseVO.success("查询成功", categoryVOList);
    }
}
