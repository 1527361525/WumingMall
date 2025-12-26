package com.wlyykf.mall.controller;

import com.wlyykf.mall.component.RedisComponent;
import com.wlyykf.mall.constants.Constants;
import com.wlyykf.mall.dto.LoginDTO;
import com.wlyykf.mall.dto.RegisterDTO;
import com.wlyykf.mall.dto.TokenUserInfoDTO;
import com.wlyykf.mall.dto.UserUpdateDTO;
import com.wlyykf.mall.service.UserService;
import com.wlyykf.mall.utils.CurrentUserUtil;
import com.wlyykf.mall.vo.PageResultVO;
import com.wlyykf.mall.vo.ResponseVO;
import com.wlyykf.mall.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@Validated
@RequestMapping("/user")
public class UserController {

    @Resource
    private UserService userService;

    @Resource
    private CurrentUserUtil currentUserUtil;


    /**
     * 登录
     * @param loginDTO 登录信息
     */
    @PostMapping("/login")
    public ResponseVO<TokenUserInfoDTO> login(@Valid @RequestBody LoginDTO loginDTO) {
        return userService.login(loginDTO);
    }

    /**
     * 登出
     */
    @PostMapping("/logout")
    public ResponseVO<Void> logout() {
        return userService.logout();
    }

    /**
     * 发送验证码
     * @param email 邮箱
     * @return 验证码发送结果
     */
    @PostMapping("/sendCode")
    public ResponseVO<Void> sendCode(@RequestParam @NotBlank(message = "邮箱不能为空") String email) {
        return userService.sendCode(email);
    }

    /**
     *  注册
     * @param registerDTO 注册信息
     */
    @PostMapping("/register")
    public ResponseVO<Void> register(@Valid @RequestBody RegisterDTO registerDTO) {
        return userService.register(registerDTO);
    }

    /**
     * 自动登录
     */
    @RequestMapping("/autoLogin")
    public ResponseVO<TokenUserInfoDTO> autoLogin() {
        return userService.autoLogin();
    }

    /**
     * 当前用户是否为管理员
     * @return 是否为管理员
     */
    @GetMapping("/isAdmin")
    public ResponseVO<Map<String, Object>> isAdmin() {
        return ResponseVO.success(userService.isAdmin());
    }

    /**
     * 修改用户信息
     * @param userUpdateDTO 修改信息
     * @return 修改结果
     */
    @PutMapping
    public ResponseVO<UserVO> updateUser(@Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return userService.updateUser(userUpdateDTO);
    }

    /**
     * 获取当前用户信息
     * @return 用户信息
     */
    @GetMapping("/getCurrentUser")
    public ResponseVO<UserVO> getCurrentUser() {
        TokenUserInfoDTO tokenUserInfoDTO = currentUserUtil.getCurrentUserInfo();
        return userService.getUserInfo(tokenUserInfoDTO.getUserId());
    }

    /**
     * 充值
     * @param rechargeMoney 充值金额
     * @return 充值后的用户金额
     */
    @PostMapping("/recharge")
    public ResponseVO<Map<String, Object>> recharge(@RequestParam @NotNull BigDecimal rechargeMoney) {
        return userService.recharge(rechargeMoney);
    }

    /**
     * 修改密码
     * @param oldPassword 旧密码
     * @param newPassword 新密码
     * @return 修改结果
     */
    @PutMapping("/updatePassword")
    public ResponseVO<Void> updatePassword(@RequestParam @NotBlank(message = "旧密码不能为空") String oldPassword,
                                           @RequestParam @NotBlank(message = "新密码不能为空") String newPassword) {
        return userService.updatePassword(oldPassword, newPassword);
    }

    /**
     * 分页查询用户列表
     * @param pageNum 页码
     * @param pageSize 页大小
     */
    @GetMapping("/getUserList")
    public PageResultVO<UserVO> getUserList(@RequestParam Integer pageNum,
                                            @RequestParam Integer pageSize) {
        return userService.getUsersPageByIds(pageNum, pageSize, null);
    }

    /**
     * 删除用户
     * @param userId 用户ID
     */
    @PutMapping("/{userId}")
    public ResponseVO<Void> deleteUser(@PathVariable @NotNull Long userId) {
        return userService.deleteUser(userId);
    }
}
