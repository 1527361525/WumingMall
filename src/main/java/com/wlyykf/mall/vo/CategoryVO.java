package com.wlyykf.mall.vo;

import com.sun.org.apache.xpath.internal.operations.Bool;
import lombok.Data;

@Data
public class CategoryVO {

    private String categoryId;

    private String name;

    private Boolean hasChildren;

}
