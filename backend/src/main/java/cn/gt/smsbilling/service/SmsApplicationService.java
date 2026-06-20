package cn.gt.smsbilling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import cn.gt.smsbilling.entity.SmsApplication;

import java.math.BigDecimal;

public interface SmsApplicationService extends IService<SmsApplication> {
    SmsApplication createApp(String appName, BigDecimal price, String signature, Integer smsType);
    String resetAppKey(Long id);
    boolean canDelete(Long id);
}
