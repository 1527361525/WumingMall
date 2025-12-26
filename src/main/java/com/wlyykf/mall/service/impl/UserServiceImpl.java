package com.wlyykf.mall.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wlyykf.mall.component.RedisComponent;
import com.wlyykf.mall.config.AppConfig;
import com.wlyykf.mall.constants.Constants;
import com.wlyykf.mall.dto.LoginDTO;
import com.wlyykf.mall.dto.RegisterDTO;
import com.wlyykf.mall.dto.TokenUserInfoDTO;
import com.wlyykf.mall.dto.UserUpdateDTO;
import com.wlyykf.mall.entity.User;
import com.wlyykf.mall.mappers.UserMapper;
import com.wlyykf.mall.service.EmailService;
import com.wlyykf.mall.service.UserService;
import com.wlyykf.mall.utils.CurrentUserUtil;
import com.wlyykf.mall.utils.PasswordUtil;
import com.wlyykf.mall.vo.*;
import jdk.nashorn.internal.parser.Token;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Resource
    private UserMapper userMapper;

    @Resource
    private EmailService emailService;

    @Resource
    private RedisComponent redisComponent;

    @Resource
    private CurrentUserUtil currentUserUtil;

    @Resource
    private AppConfig appConfig;

    @Override
    public ResponseVO<TokenUserInfoDTO> login(LoginDTO loginDTO) {
        // 校验邮箱和密码
        String email = loginDTO.getEmail();
        String password = loginDTO.getPassword();

        User user = userMapper.selectOne(new QueryWrapper<User>().eq("email", email));
        if (user != null && PasswordUtil.matches(password, user.getPassword())) {
            // 登录成功
            TokenUserInfoDTO tokenUserInfoDTO = new TokenUserInfoDTO();
            tokenUserInfoDTO.setUserId(user.getUserId());
            tokenUserInfoDTO.setNickName(user.getNickName());
            redisComponent.saveTokenInfo(tokenUserInfoDTO);

            return ResponseVO.success("登录成功", tokenUserInfoDTO);
        } else {
            // 登录失败
            return ResponseVO.fail("邮箱或密码错误", null);
        }
    }

    @Override
    public ResponseVO<Void> logout() {
        TokenUserInfoDTO tokenUserInfoDTO = currentUserUtil.getCurrentUserInfo();
        if (tokenUserInfoDTO == null) {
            return ResponseVO.fail("用户未登录", null);
        }
        redisComponent.cleanToken(tokenUserInfoDTO.getToken());
        return ResponseVO.success("登出成功", null);
    }

    @Override
    public ResponseVO<Void> sendCode(String email) {
        emailService.sendCode(email);
//        // 生成四位随机数字
//        String code = String.valueOf((int) (Math.random() * 10000));
//
//        // 存入redis
//        redisComponent.saveCheckCode(code, email);
//
//        log.info("验证码：" + code);

        return ResponseVO.success("验证码发送成功", null);
    }

    @Override
    public ResponseVO<Void> verifyCode(String email, String code) {
        boolean flag = emailService.verifyCode(email, code);
        return flag ? ResponseVO.success("验证码验证成功", null) : ResponseVO.fail("验证码验证失败", null);
    }

    @Override
    public ResponseVO<Void> register(RegisterDTO registerDTO) {
        String checkCode = redisComponent.getCheckCode(registerDTO.getEmail());
        if (checkCode == null) {
            return ResponseVO.fail("验证码已过期", null);
        }
        boolean isCodeVerifySuccess = checkCode.equals(registerDTO.getCode());
        if (!isCodeVerifySuccess) {
            return ResponseVO.fail("验证码验证失败", null);
        } else {
            User dbUser = userMapper.selectOne(new QueryWrapper<User>().eq("email", registerDTO.getEmail()));
            if (dbUser != null) {
                return ResponseVO.fail("该邮箱已存在", null);
            }
            User user = new User();
            user.setEmail(registerDTO.getEmail());
            user.setNickName(registerDTO.getNickName());
            user.setPassword(PasswordUtil.encode(registerDTO.getPassword()));
            if (userMapper.insert(user) > 0) {
                return ResponseVO.success("注册成功", null);
            }
            return ResponseVO.fail("注册失败", null);
        }
    }

    @Override
    public ResponseVO<TokenUserInfoDTO> autoLogin() {
        TokenUserInfoDTO tokenUserInfoDto = currentUserUtil.getCurrentUserInfo();
        if (tokenUserInfoDto == null) {
            return ResponseVO.fail("用户未登录", null);
        }
        if (tokenUserInfoDto.getExpireAt() - System.currentTimeMillis() < Constants.ONE_DAY) {
            //如果token过期时间小于1天，则为token续期
            redisComponent.saveTokenInfo(tokenUserInfoDto);
        }
        return ResponseVO.success(tokenUserInfoDto);
    }

    @Override
    public Map<String, Object> isAdmin() {
        TokenUserInfoDTO tokenUserInfoDTO = currentUserUtil.getCurrentUserInfo();
        Map<String, Object> result = new HashMap<>();
        if (tokenUserInfoDTO != null) {
            result.put("isAdmin", tokenUserInfoDTO.getUserId().equals(appConfig.getAdminId()));
            return result;
        }
        result.put("isAdmin", false);
        return result;
    }

    @Override
    public ResponseVO<UserVO> updateUser(UserUpdateDTO userUpdateDTO) {
        User dbUser = userMapper.selectById(userUpdateDTO.getUserId());
        if (dbUser != null) {
            dbUser.setNickName(userUpdateDTO.getNickName());
            dbUser.setPhone(userUpdateDTO.getPhone());
            dbUser.setAvatar(userUpdateDTO.getAvatar());
            if (userMapper.updateById(dbUser) > 0) {
                UserVO userVO = new UserVO();
                userVO.setUserId(String.valueOf(dbUser.getUserId()));
                userVO.setNickName(dbUser.getNickName());
                userVO.setEmail(dbUser.getEmail());
                userVO.setPhone(dbUser.getPhone());
                userVO.setAvatar(dbUser.getAvatar());
                userVO.setMoney(dbUser.getMoney());
                return ResponseVO.success("更新成功", userVO);
            }
        }
        return ResponseVO.fail("更新失败", null);
    }

    @Override
    public ResponseVO<UserVO> getUserInfo(Long userId) {
        User dbUser = userMapper.selectById(userId);
        if (dbUser != null) {
            UserVO userVO = new UserVO();
            userVO.setUserId(String.valueOf(dbUser.getUserId()));
            userVO.setNickName(dbUser.getNickName());
            userVO.setEmail(dbUser.getEmail());
            userVO.setPhone(dbUser.getPhone());
            userVO.setAvatar(dbUser.getAvatar());
            userVO.setMoney(dbUser.getMoney());
            return ResponseVO.success("获取成功", userVO);
        }
        return ResponseVO.fail("获取失败", null);
    }

    @Override
    public ResponseVO<Map<String, Object>> recharge(BigDecimal rechargeMoney) {
        Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
        // 校验金额是否合法
        if (rechargeMoney.compareTo(BigDecimal.ZERO) <= 0) {
            return ResponseVO.fail("充值金额不合法", null);
        }

        // 充值
        User dbUser = userMapper.selectById(userId);
        dbUser.setMoney(dbUser.getMoney().add(rechargeMoney));
        if (userMapper.updateById(dbUser) > 0) {
            Map<String, Object> result = new HashMap<>();
            result.put("money", dbUser.getMoney());
            return ResponseVO.success("充值成功", result);
        }
        return ResponseVO.fail("充值失败", null);
    }

    @Override
    public ResponseVO<Void> updatePassword(String oldPassword, String newPassword) {
        Long userId = currentUserUtil.getCurrentUserInfo().getUserId();
        User dbUser = userMapper.selectById(userId);
        if (PasswordUtil.matches(oldPassword, dbUser.getPassword())) {
            dbUser.setPassword(PasswordUtil.encode(newPassword));
            if (userMapper.updateById(dbUser) > 0) {
                return ResponseVO.success("修改密码成功", null);
            }
            return ResponseVO.fail("修改密码失败", null);
        }
        return ResponseVO.fail("旧密码错误", null);
    }

    @Override
    public List<UserVO> getUsersByIds(List<Long> userIdList) {
        List<UserVO> userList = userMapper.getUsersByIds(userIdList);
        return userList;
    }

    @Override
    public PageResultVO<UserVO> getUsersPageByIds(Integer pageNum, Integer pageSize, List<Long> userIdList) {
        Page<UserVO> page = Page.of(pageNum, pageSize);
        Page<UserVO> userVOPage = userMapper.getUsersPageByIds(page, userIdList);
        return PageResultVO.build(userVOPage);
    }

    @Override
    public ResponseVO<Void> deleteUser(Long userId) {
        if (userMapper.deleteById(userId) > 0) {
            return ResponseVO.success("删除成功", null);
        }
        return ResponseVO.fail("删除失败", null);
    }

}
