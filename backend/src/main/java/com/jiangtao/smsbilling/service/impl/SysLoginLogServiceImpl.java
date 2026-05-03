package com.jiangtao.smsbilling.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangtao.smsbilling.entity.SysLoginLog;
import com.jiangtao.smsbilling.mapper.SysLoginLogMapper;
import com.jiangtao.smsbilling.service.SysLoginLogService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class SysLoginLogServiceImpl extends ServiceImpl<SysLoginLogMapper, SysLoginLog> implements SysLoginLogService {
    @Override
    public void saveLog(Long userId, String username, String ip, String userAgent, int status) {
        SysLoginLog log = new SysLoginLog();
        log.setUserId(userId);
        log.setUsername(username);
        log.setIp(ip);
        log.setUserAgent(userAgent);
        log.setLoginTime(LocalDateTime.now());
        log.setStatus(status);
        save(log);
    }
}
