package com.jiangtao.smsbilling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangtao.smsbilling.entity.SysLoginLog;

public interface SysLoginLogService extends IService<SysLoginLog> {
    void saveLog(Long userId, String username, String ip, String userAgent, int status);
}
