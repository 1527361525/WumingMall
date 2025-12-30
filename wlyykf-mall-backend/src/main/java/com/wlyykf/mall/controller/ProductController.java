package com.wlyykf.mall.controller;

import com.wlyykf.mall.dto.ProductListDTO;
import com.wlyykf.mall.dto.ProductUpdateDTO;
import com.wlyykf.mall.entity.Product;
import com.wlyykf.mall.service.ProductService;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ProductUpdateVO;
import com.wlyykf.mall.vo.ProductVO;
import com.wlyykf.mall.vo.ResponseVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/product")
public class ProductController {

    @Resource
    private ProductService productService;

    /**
     * 新增商品
     * @param productDTO 商品信息
     */
    @PostMapping
    public ResponseVO<Void> addProduct(@Valid @RequestBody ProductUpdateDTO productDTO) {
        return productService.addProduct(productDTO);
    }

    /**
     * 删除商品
     * @param productId 商品id
     */
    @PutMapping("/{productId}")
    public ResponseVO<Void> deleteProduct(@PathVariable @NotNull Long productId) {
        return productService.deleteProduct(productId);
    }

    /**
     * 修改商品
     * @param productDTO 商品信息
     */
    @PutMapping
    public ResponseVO<Void> updateProduct(@Valid @RequestBody ProductUpdateDTO productDTO) {
        return productService.updateProduct(productDTO);
    }

    /**
     * 查询商品详情
     * @param productId 商品id
     * @return 商品详情
     */
    @GetMapping("/{productId}")
    public ResponseVO<ProductVO> getProduct(@PathVariable @NotNull Long productId) {
        return productService.getProduct(productId);
    }

    /**
     * 分页查询商品列表
     * @param productListDTO 查询条件
     */
    @PostMapping("/getProductList")
    public PageResultVO<ProductVO> getProductList(@Valid @RequestBody ProductListDTO productListDTO) {
        return productService.getProductList(productListDTO);
    }

    /**
     * 获取商品信息(更新用)
     * @param productId 商品id
     * @return 商品信息
     */
    @GetMapping("/getForUpdate/{productId}")
    public ResponseVO<ProductUpdateVO> getForUpdate(@PathVariable @NotNull Long productId) {
        return productService.getForUpdate(productId);
    }

}
