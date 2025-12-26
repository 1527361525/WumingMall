package com.wlyykf.mall.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("tb_category")
public class Category extends BaseEntity {

    @TableId(type = IdType.ASSIGN_ID)
    private Long categoryId;

    private String name;

    private Long parentId;

    private Integer sort;

}
