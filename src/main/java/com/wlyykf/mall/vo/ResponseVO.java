package com.wlyykf.mall.vo;

import lombok.Data;

@Data
public class ResponseVO<T> {

    public static final int SUCCESS = 200;
    public static final int FAIL = 500;

    private Integer code;
    private String info;
    private T data;

    public static <T> ResponseVO<T> success(T data) {
    	return generateResponseVO(SUCCESS, "success", data);
    }

    public static <T> ResponseVO<T> fail(T data) {
    	return generateResponseVO(FAIL, "fail", data);
    }

    public static <T> ResponseVO<T> success(String info, T data) {
        return generateResponseVO(SUCCESS, info, data);
    }

    public static <T> ResponseVO<T> fail(String info, T data) {
        return generateResponseVO(FAIL, info, data);
    }

    private static <T> ResponseVO<T> generateResponseVO(Integer code, String info, T data) {
    	ResponseVO<T> responseVO = new ResponseVO<>();
    	responseVO.setCode(code);
    	responseVO.setInfo(info);
    	responseVO.setData(data);
    	return responseVO;
    }

}