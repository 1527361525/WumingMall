package com.wlyykf.mall.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlyykf.mall.entity.CartItem;
import com.wlyykf.mall.vo.CartItemVO;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface CartItemMapper extends BaseMapper<CartItem> {
    BigDecimal getTotalPrice(@Param("userId") Long userId);

    Page<CartItemVO> getAll(Page<CartItemVO> page, @Param("userId") Long userId);

    List<CartItemVO> getAllList(@Param("userId") Long userId);
}
