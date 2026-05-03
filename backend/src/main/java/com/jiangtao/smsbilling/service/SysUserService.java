package com.jiangtao.smsbilling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangtao.smsbilling.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser login(String username, String password);
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}
