package com.wlyykf.mall.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wlyykf.mall.entity.Category;
import com.wlyykf.mall.vo.CategoryVO;
import org.apache.ibatis.annotations.Param;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

public interface CategoryMapper extends BaseMapper<Category> {

    int deleteRelatedProducts(@Param("id") Long categoryId);

    int deleteCategoryRecursively(@Param("id") Long categoryId);

    List<CategoryVO> getChildren(@RequestParam("parentId") Long parentId);

    boolean hasChildren(@Param("id") Long categoryId);
}
