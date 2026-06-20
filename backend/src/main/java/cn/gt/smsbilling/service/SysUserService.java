package cn.gt.smsbilling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.gt.smsbilling.entity.SysUser;

public interface SysUserService extends IService<SysUser> {
    SysUser login(String username, String password);
    boolean changePassword(Long userId, String oldPassword, String newPassword);
}
