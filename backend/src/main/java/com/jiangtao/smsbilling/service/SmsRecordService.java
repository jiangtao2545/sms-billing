package com.jiangtao.smsbilling.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.jiangtao.smsbilling.entity.SmsRecord;

import java.math.BigDecimal;
import java.util.List;

public interface SmsRecordService extends IService<SmsRecord> {
    SmsRecord send(String appId, String phone, String content, BigDecimal price);
    List<SmsRecord> batchSend(String appId, List<String> phones, String content, BigDecimal price);
    byte[] exportExcel(String appId, String phone, String startTime, String endTime, Integer status) throws Exception;
}
