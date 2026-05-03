package com.jiangtao.smsbilling.controller;

import com.jiangtao.smsbilling.common.Result;
import com.jiangtao.smsbilling.entity.SmsApplication;
import com.jiangtao.smsbilling.entity.SmsRecord;
import com.jiangtao.smsbilling.service.SmsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/openapi")
public class OpenApiController {
    @Autowired
    private SmsRecordService smsRecordService;

    @PostMapping("/sms/send")
    public Result<SmsRecord> send(@RequestBody Map<String, String> body, HttpServletRequest request) {
        SmsApplication app = (SmsApplication) request.getAttribute("app");
        String phone = body.get("phone");
        String content = body.get("content");
        SmsRecord record = smsRecordService.send(app.getAppId(), phone, content, app.getPrice());
        return Result.success(record);
    }
}
