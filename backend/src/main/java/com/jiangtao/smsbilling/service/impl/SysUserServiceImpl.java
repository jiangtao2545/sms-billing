package com.jiangtao.smsbilling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangtao.smsbilling.entity.SysUser;
import com.jiangtao.smsbilling.mapper.SysUserMapper;
import com.jiangtao.smsbilling.service.SysUserService;
import cn.hutool.crypto.digest.BCrypt;
import org.springframework.stereotype.Service;

@Service
public class SysUserServiceImpl extends ServiceImpl<SysUserMapper, SysUser> implements SysUserService {

    @Override
    public SysUser login(String username, String password) {
        SysUser user = getOne(new QueryWrapper<SysUser>().eq("username", username));
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!BCrypt.checkpw(password, user.getPassword())) {
            throw new RuntimeException("密码错误");
        }
        return user;
    }

    @Override
    public boolean changePassword(Long userId, String oldPassword, String newPassword) {
        SysUser user = getById(userId);
        if (user == null) return false;
        if (!BCrypt.checkpw(oldPassword, user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        user.setPassword(BCrypt.hashpw(newPassword, BCrypt.gensalt()));
        return updateById(user);
    }
}
