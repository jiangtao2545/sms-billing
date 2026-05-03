package com.jiangtao.smsbilling.controller;

import com.jiangtao.smsbilling.common.Result;
import com.jiangtao.smsbilling.mapper.SmsRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/stat")
public class StatController {
    @Autowired
    private SmsRecordMapper smsRecordMapper;

    @GetMapping("/byApp")
    public Result<List<Map<String, Object>>> byApp() {
        return Result.success(smsRecordMapper.statByApp());
    }

    @GetMapping("/byTime")
    public Result<List<Map<String, Object>>> byTime(@RequestParam(defaultValue = "day") String type) {
        String fmt;
        switch (type) {
            case "month":
                fmt = "%Y-%m";
                break;
            case "year":
                fmt = "%Y";
                break;
            default:
                fmt = "%Y-%m-%d";
        }
        return Result.success(smsRecordMapper.statByTime(fmt));
    }
}
