package com.wlyykf.mall.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.wlyykf.mall.dto.ProductListDTO;
import com.wlyykf.mall.dto.ProductUpdateDTO;
import com.wlyykf.mall.entity.Category;
import com.wlyykf.mall.entity.Product;
import com.wlyykf.mall.enums.OrderTypeEnum;
import com.wlyykf.mall.enums.SortDirectionEnum;
import com.wlyykf.mall.exception.BusinessException;
import com.wlyykf.mall.mappers.CategoryMapper;
import com.wlyykf.mall.mappers.ProductMapper;
import com.wlyykf.mall.service.ProductService;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ProductUpdateVO;
import com.wlyykf.mall.vo.ProductVO;
import com.wlyykf.mall.vo.ResponseVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class ProductServiceImpl implements ProductService {
    @Resource
    private ProductMapper productMapper;

    @Resource
    private CategoryMapper categoryMapper;

    @Override
    public ResponseVO<Void> addProduct(ProductUpdateDTO productDTO) {
        Product product = new Product();
        product.setName(productDTO.getName());
        product.setCategoryId(productDTO.getCategoryId());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setDescription(productDTO.getDescription());
        product.setProductImage(productDTO.getProductImage());
        if (productMapper.insert(product) > 0) {
            return ResponseVO.success("添加成功", null);
        }
        return ResponseVO.fail("添加失败", null);
    }

    @Override
    public ResponseVO<Void> deleteProduct(Long productId) {
        if (productMapper.deleteById(productId) > 0) {
            return ResponseVO.success("删除成功", null);
        }
        return ResponseVO.fail("删除失败", null);
    }

    @Override
    public ResponseVO<Void> updateProduct(ProductUpdateDTO productDTO) {
        if (productDTO.getProductId() == null) {
            return ResponseVO.fail("参数错误", null);
        }
        Product product = new Product();
        product.setProductId(productDTO.getProductId());
        product.setName(productDTO.getName());
        product.setCategoryId(productDTO.getCategoryId());
        product.setPrice(productDTO.getPrice());
        product.setStock(productDTO.getStock());
        product.setDescription(productDTO.getDescription());
        product.setProductImage(productDTO.getProductImage());
        if (productMapper.updateById(product) > 0) {
            return ResponseVO.success("修改成功", null);
        }
        throw new BusinessException(500, "修改失败");
    }

    @Override
    public ResponseVO<ProductVO> getProduct(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product != null) {
            ProductVO productVO = new ProductVO();
            productVO.setProductId(String.valueOf(product.getProductId()));
            productVO.setName(product.getName());
            productVO.setPrice(product.getPrice());
            productVO.setDescription(product.getDescription());
            productVO.setProductImage(product.getProductImage());
            productVO.setTotalSales(product.getTotalSales());
            return ResponseVO.success("查询成功", productVO);
        }
        return ResponseVO.fail("商品不存在", null);
    }

    @Override
    public PageResultVO<ProductVO> getProductList(ProductListDTO productListDTO) {
        Page<ProductVO> page = new Page<>(productListDTO.getPageNum(), productListDTO.getPageSize());

        OrderTypeEnum orderTypeEnum = OrderTypeEnum.getByCode(productListDTO.getOrderType());
        SortDirectionEnum sortDirectionEnum = SortDirectionEnum.getByCode(productListDTO.getSortDirection());
        if (orderTypeEnum == null || sortDirectionEnum == null) {
            throw new BusinessException(500, "参数错误");
        }

        Page<ProductVO> productVOPage = productMapper.getProductList(page,
                productListDTO.getCategoryId(),
                orderTypeEnum.getName(),
                sortDirectionEnum.getName());

        return PageResultVO.build(productVOPage);
    }

    @Override
    public ResponseVO<ProductUpdateVO> getForUpdate(Long productId) {
        Product product = productMapper.selectById(productId);
        if (product == null) {
            return ResponseVO.fail("商品不存在", null);
        }
        ProductUpdateVO productUpdateVO = new ProductUpdateVO();
        productUpdateVO.setProductId(String.valueOf(product.getProductId()));
        productUpdateVO.setName(product.getName());
        productUpdateVO.setCategoryId(String.valueOf(product.getCategoryId()));
        productUpdateVO.setPrice(product.getPrice());
        productUpdateVO.setStock(product.getStock());
        productUpdateVO.setDescription(product.getDescription());
        productUpdateVO.setProductImage(product.getProductImage());

        Category category = categoryMapper.selectById(product.getCategoryId());
        if (category != null) {
            productUpdateVO.setCategoryName(category.getName());
        }
        return ResponseVO.success("查询成功", productUpdateVO);
    }
}
