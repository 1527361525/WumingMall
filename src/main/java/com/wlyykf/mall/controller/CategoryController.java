package com.wlyykf.mall.controller;

import com.wlyykf.mall.dto.CategoryDTO;
import com.wlyykf.mall.service.CategoryService;
import com.wlyykf.mall.vo.CategoryVO;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/category")
public class CategoryController {

    @Resource
    private CategoryService categoryService;

    /**
     * 添加分类
     * @param categoryDTO 分类信息
     */
    @PostMapping
    public ResponseVO<Void> addCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.addCategory(categoryDTO);
    }

    /**
     * 修改分类
     * @param categoryDTO 分类信息
     */
    @PutMapping
    public ResponseVO<Void> updateCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        return categoryService.updateCategory(categoryDTO);
    }

    /**
     * 删除分类
     * @param categoryId 分类id
     */
    @PutMapping("/{categoryId}")
    public ResponseVO<Void> deleteCategory(@PathVariable @NotNull Long categoryId) {
        return categoryService.deleteCategory(categoryId);
    }

    /**
     * 根据父类查子分类
     * @param parentId 父类id
     * @return 子分类列表
     */
    @GetMapping("/getChildren")
    public ResponseVO<List<CategoryVO>> getChildren(@RequestParam @NotNull Long parentId) {
        return categoryService.getChildren(parentId);
    }
}
