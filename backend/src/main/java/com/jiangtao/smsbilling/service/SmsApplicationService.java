package com.jiangtao.smsbilling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangtao.smsbilling.entity.SmsApplication;

import java.math.BigDecimal;

public interface SmsApplicationService extends IService<SmsApplication> {
    SmsApplication createApp(String appName, BigDecimal price);
    String resetAppKey(Long id);
    boolean canDelete(Long id);
}
