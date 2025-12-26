package com.wlyykf.mall.vo;

import com.baomidou.mybatisplus.core.metadata.IPage;
import lombok.Data;

import java.util.List;

@Data
public class PageResultVO<T> {
    public static final int SUCCESS = 200;
    public static final int FAIL = 500;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> data;

    /**
     * 消息状态码
     */
    private int code;

    /**
     * 消息内容
     */
    private String info;

    public static <T> PageResultVO<T> build(IPage<T> page) {
        PageResultVO<T> result = new PageResultVO<>();
        result.setTotal(page.getTotal());
        result.setData(page.getRecords());
        result.setCode(SUCCESS);
        result.setInfo("查询成功");
        return result;
    }
}
