package com.wlyykf.mall.service;

import com.wlyykf.mall.dto.LoginDTO;
import com.wlyykf.mall.dto.RegisterDTO;
import com.wlyykf.mall.dto.TokenUserInfoDTO;
import com.wlyykf.mall.dto.UserUpdateDTO;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ResponseVO;
import com.wlyykf.mall.vo.UserVO;

import javax.servlet.http.HttpServletRequest;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface UserService {

    /**
     * 登录
     * @param loginDTO 登录信息
     */
    ResponseVO<TokenUserInfoDTO> login(LoginDTO loginDTO);

    /**
     * 登出
     */
    ResponseVO<Void> logout();

    /**
     * 发送验证码
     * @param email 邮箱
     * @return 验证码发送结果
     */
    ResponseVO<Void> sendCode(String email);

    ResponseVO<Void> verifyCode(String email, String code);

    /**
     *  注册
     * @param registerDTO 注册信息
     */
    ResponseVO<Void> register(RegisterDTO registerDTO);

    /**
     * 自动登录
     */
    ResponseVO<TokenUserInfoDTO> autoLogin();

    /**
     * 当前用户是否为管理员
     * @return 是否为管理员
     */
    Map<String, Object> isAdmin();

    /**
     * 修改用户信息
     * @param userUpdateDTO 修改信息
     * @return 修改结果
     */
    ResponseVO<UserVO> updateUser(UserUpdateDTO userUpdateDTO);

    /**
     * 获取用户信息
     * @param userId 用户id
     * @return 用户信息
     */
    ResponseVO<UserVO> getUserInfo(Long userId);

    /**
     * 充值
     * @param rechargeMoney 充值金额
     * @return 充值后的用户金额
     */
    ResponseVO<Map<String, Object>> recharge(BigDecimal rechargeMoney);

    /**
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    ResponseVO<Void> updatePassword(String oldPassword, String newPassword);

    /**
     * 根据id列表批量获取用户信息
     * @param userIdList 用户id列表
     * @return 用户信息列表
     */
    List<UserVO> getUsersByIds(List<Long> userIdList);

    /**
     * 根据id列表分页批量获取用户信息
     * @param pageNum 页码
     * @param pageSize 页大小
     * @param userIdList 用户id列表
     * @return 用户信息列表
     */
    PageResultVO<UserVO> getUsersPageByIds(Integer pageNum, Integer pageSize, List<Long> userIdList);

    /**
     * 删除用户
     * @param userId 用户id
     * @return 删除结果
     */
    ResponseVO<Void> deleteUser(Long userId);
}
