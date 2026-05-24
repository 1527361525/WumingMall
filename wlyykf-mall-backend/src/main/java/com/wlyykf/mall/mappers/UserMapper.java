package com.wlyykf.mall.mappers;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlyykf.mall.entity.User;
import com.wlyykf.mall.vo.UserVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface UserMapper extends BaseMapper<User> {

    List<UserVO> getUsersByIds(@Param("userIdList") List<Long> userIdList);

    Page<UserVO> getUsersPageByIds(Page<UserVO> page, @Param("userIdList") List<Long> userIdList);

    /**
     * 查询销售人员列表（role=1）
     * @param page 分页参数
     * @return 销售人员列表
     */
    Page<UserVO> getSalesPersonList(Page<UserVO> page);
}
