package com.wlyykf.mall.service;

import com.wlyykf.mall.dto.ProductListDTO;
import com.wlyykf.mall.dto.ProductUpdateDTO;
import com.wlyykf.mall.entity.Product;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ProductUpdateVO;
import com.wlyykf.mall.vo.ProductVO;
import com.wlyykf.mall.vo.ResponseVO;

public interface ProductService {
    /**
     * 新增商品
     * @param productDTO 商品信息
     */
    ResponseVO<Void> addProduct(ProductUpdateDTO productDTO);

    /**
     * 删除商品
     * @param productId 商品id
     */
    ResponseVO<Void> deleteProduct(Long productId);

    /**
     * 修改商品
     * @param productDTO 商品信息
     */
    ResponseVO<Void> updateProduct(ProductUpdateDTO productDTO);

    /**
     * 查询商品详情
     * @param productId 商品id
     * @return 商品详情
     */
    ResponseVO<ProductVO> getProduct(Long productId);

    /**
     * 分页查询商品列表
     * @param productListDTO 查询条件
     */
    PageResultVO<ProductVO> getProductList(ProductListDTO productListDTO);

    /**
     * 获取商品信息(更新用)
     * @param productId 商品id
     * @return 商品信息
     */
    ResponseVO<ProductUpdateVO> getForUpdate(Long productId);
}
