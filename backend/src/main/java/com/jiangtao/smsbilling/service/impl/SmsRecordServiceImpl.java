package com.jiangtao.smsbilling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.jiangtao.smsbilling.entity.SmsRecord;
import com.jiangtao.smsbilling.mapper.SmsRecordMapper;
import com.jiangtao.smsbilling.service.SmsRecordService;
import com.jiangtao.smsbilling.util.SmsCountUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class SmsRecordServiceImpl extends ServiceImpl<SmsRecordMapper, SmsRecord> implements SmsRecordService {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public SmsRecord send(String appId, String phone, String content, BigDecimal price) {
        int charCount = SmsCountUtil.countChars(content);
        int billingCount = SmsCountUtil.countBillingSegments(charCount);
        BigDecimal totalFee = SmsCountUtil.calcFee(billingCount, price);
        int status = Math.random() < 0.9 ? 1 : 0;

        SmsRecord record = new SmsRecord();
        record.setAppId(appId);
        record.setPhone(phone);
        record.setContent(content);
        record.setCharCount(charCount);
        record.setBillingCount(billingCount);
        record.setPrice(price);
        record.setTotalFee(totalFee);
        record.setStatus(status);
        record.setCreateTime(LocalDateTime.now());
        save(record);
        return record;
    }

    @Override
    public List<SmsRecord> batchSend(String appId, List<String> phones, String content, BigDecimal price) {
        List<SmsRecord> records = new ArrayList<>();
        for (String phone : phones) {
            records.add(send(appId, phone, content, price));
        }
        return records;
    }

    @Override
    public byte[] exportExcel(String appId, String phone, String startTime, String endTime, Integer status) throws Exception {
        QueryWrapper<SmsRecord> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(appId)) wrapper.eq("app_id", appId);
        if (StringUtils.hasText(phone)) wrapper.like("phone", phone);
        if (StringUtils.hasText(startTime)) wrapper.ge("create_time", LocalDateTime.parse(startTime, FORMATTER));
        if (StringUtils.hasText(endTime)) wrapper.le("create_time", LocalDateTime.parse(endTime, FORMATTER));
        if (status != null) wrapper.eq("status", status);
        List<SmsRecord> records = list(wrapper);

        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("短信记录");
        String[] headers = {"ID", "应用ID", "手机号", "内容", "字符数", "计费条数", "单价", "总费用", "状态", "发送时间"};
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < headers.length; i++) {
            headerRow.createCell(i).setCellValue(headers[i]);
        }
        int rowNum = 1;
        for (SmsRecord r : records) {
            Row row = sheet.createRow(rowNum++);
            row.createCell(0).setCellValue(r.getId());
            row.createCell(1).setCellValue(r.getAppId());
            row.createCell(2).setCellValue(r.getPhone());
            row.createCell(3).setCellValue(r.getContent());
            row.createCell(4).setCellValue(r.getCharCount());
            row.createCell(5).setCellValue(r.getBillingCount());
            row.createCell(6).setCellValue(r.getPrice().doubleValue());
            row.createCell(7).setCellValue(r.getTotalFee().doubleValue());
            row.createCell(8).setCellValue(r.getStatus() == 1 ? "成功" : "失败");
            row.createCell(9).setCellValue(r.getCreateTime() != null ? r.getCreateTime().format(FORMATTER) : "");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }
}
