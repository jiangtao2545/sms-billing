package com.jiangtao.smsbilling.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangtao.smsbilling.entity.SmsApplication;
import com.jiangtao.smsbilling.entity.SmsRecord;
import com.jiangtao.smsbilling.mapper.SmsApplicationMapper;
import com.jiangtao.smsbilling.mapper.SmsRecordMapper;
import com.jiangtao.smsbilling.service.SmsApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SmsApplicationServiceImpl extends ServiceImpl<SmsApplicationMapper, SmsApplication> implements SmsApplicationService {
    @Autowired
    private SmsRecordMapper smsRecordMapper;

    @Override
    public SmsApplication createApp(String appName, BigDecimal price) {
        SmsApplication app = new SmsApplication();
        app.setAppId(UUID.randomUUID().toString(true));
        app.setAppName(appName);
        app.setAppKey(RandomUtil.randomString(32));
        app.setPrice(price);
        app.setStatus(1);
        app.setCreateTime(LocalDateTime.now());
        app.setUpdateTime(LocalDateTime.now());
        save(app);
        return app;
    }

    @Override
    public String resetAppKey(Long id) {
        SmsApplication app = getById(id);
        if (app == null) throw new RuntimeException("应用不存在");
        String newKey = RandomUtil.randomString(32);
        app.setAppKey(newKey);
        app.setUpdateTime(LocalDateTime.now());
        updateById(app);
        return newKey;
    }

    @Override
    public boolean canDelete(Long id) {
        SmsApplication app = getById(id);
        if (app == null) return true;
        return smsRecordMapper.selectCount(new QueryWrapper<SmsRecord>().eq("app_id", app.getAppId())) == 0;
    }
}
