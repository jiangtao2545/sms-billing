package cn.gt.smsbilling.controller;

import cn.gt.smsbilling.common.Result;
import cn.gt.smsbilling.entity.SmsApplication;
import cn.gt.smsbilling.entity.SmsRecord;
import cn.gt.smsbilling.service.SmsApplicationService;
import cn.gt.smsbilling.service.SmsRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/sms")
public class SmsController {
    @Autowired
    private SmsRecordService smsRecordService;
    @Autowired
    private SmsApplicationService appService;

    @PostMapping("/send")
    public Result<SmsRecord> send(@RequestBody Map<String, Object> body) {
        String appId = (String) body.get("appId");
        String phone = (String) body.get("phone");
        String content = (String) body.get("content");
        SmsApplication app = appService.getOne(new QueryWrapper<SmsApplication>().eq("app_id", appId).eq("status", 1));
        if (app == null) return Result.error("应用不存在或已禁用");
        SmsRecord record = smsRecordService.send(appId, phone, content, app.getPrice());
        return Result.success(record);
    }

    @PostMapping("/batch")
    public Result<List<SmsRecord>> batch(@RequestBody Map<String, Object> body) {
        String appId = (String) body.get("appId");
        @SuppressWarnings("unchecked")
        List<String> phones = (List<String>) body.get("phones");
        String content = (String) body.get("content");
        SmsApplication app = appService.getOne(new QueryWrapper<SmsApplication>().eq("app_id", appId).eq("status", 1));
        if (app == null) return Result.error("应用不存在或已禁用");
        List<SmsRecord> records = smsRecordService.batchSend(appId, phones, content, app.getPrice());
        return Result.success(records);
    }
}
