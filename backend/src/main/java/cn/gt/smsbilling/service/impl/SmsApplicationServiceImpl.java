package cn.gt.smsbilling.service.impl;

import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gt.smsbilling.entity.SmsApplication;
import cn.gt.smsbilling.entity.SmsRecord;
import cn.gt.smsbilling.mapper.SmsApplicationMapper;
import cn.gt.smsbilling.mapper.SmsRecordMapper;
import cn.gt.smsbilling.service.SmsApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class SmsApplicationServiceImpl extends ServiceImpl<SmsApplicationMapper, SmsApplication> implements SmsApplicationService {
    @Autowired
    private SmsRecordMapper smsRecordMapper;

    @Override
    public SmsApplication createApp(String appName, BigDecimal price, String signature, Integer smsType) {
        SmsApplication app = new SmsApplication();
        app.setAppId(UUID.randomUUID().toString(true));
        app.setAppName(appName);
        app.setAppKey(RandomUtil.randomString(32));
        app.setPrice(price);
        app.setSignature(signature);
        app.setSmsType(smsType != null ? smsType : 1);
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
