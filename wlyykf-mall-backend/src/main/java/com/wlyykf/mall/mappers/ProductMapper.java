package com.wlyykf.mall.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlyykf.mall.dto.ProductListDTO;
import com.wlyykf.mall.entity.Product;
import com.wlyykf.mall.vo.ProductVO;
import org.apache.ibatis.annotations.Param;

public interface ProductMapper extends BaseMapper<Product> {

    Page<ProductVO> getProductList(Page<ProductVO> page,
                                    @Param("categoryId") Long categoryId,
                                    @Param("orderField") String orderField,
                                    @Param("sortDirection") String sortDirection);
}
