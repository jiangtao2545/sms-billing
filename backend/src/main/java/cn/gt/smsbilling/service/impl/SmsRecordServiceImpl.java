package cn.gt.smsbilling.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.gt.smsbilling.entity.SmsRecord;
import cn.gt.smsbilling.gateway.JdCloudSmsResponse;
import cn.gt.smsbilling.gateway.SmsGatewayService;
import cn.gt.smsbilling.mapper.SmsRecordMapper;
import cn.gt.smsbilling.service.SmsRecordService;
import cn.gt.smsbilling.util.SmsCountUtil;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
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

    private static final String UNSUBSCRIBE_TEXT = "回T退订";

    private static final int BATCH_MAX_PHONES = 100;

    @Autowired
    private SmsGatewayService smsGatewayService;

    @Override
    public SmsRecord send(String appId, String phone, String content, BigDecimal price, String signature, Integer smsType) {
        String fullContent = buildFullContent(content, signature, smsType);

        int charCount = SmsCountUtil.countChars(fullContent);
        int billingCount = SmsCountUtil.countBillingSegments(charCount);
        BigDecimal totalFee = SmsCountUtil.calcFee(billingCount, price);

        // 调用京东云短信网关
        JdCloudSmsResponse response = smsGatewayService.send(phone, fullContent);
        int status;
        String reqId = null;
        if (response != null && response.isSuccess()) {
            status = 1; // 已提交
            reqId = response.getReqId();
        } else {
            status = 0; // 提交失败
        }

        SmsRecord record = new SmsRecord();
        record.setAppId(appId);
        record.setPhone(phone);
        record.setContent(fullContent);
        record.setCharCount(charCount);
        record.setBillingCount(billingCount);
        record.setPrice(price);
        record.setTotalFee(totalFee);
        record.setReqId(reqId);
        record.setStatus(status);
        record.setCreateTime(LocalDateTime.now());
        save(record);
        return record;
    }

    @Override
    public List<SmsRecord> batchSend(String appId, List<String> phones, String content, BigDecimal price, String signature, Integer smsType) {
        String fullContent = buildFullContent(content, signature, smsType);
        int charCount = SmsCountUtil.countChars(fullContent);
        int billingCount = SmsCountUtil.countBillingSegments(charCount);
        BigDecimal totalFee = SmsCountUtil.calcFee(billingCount, price);

        List<SmsRecord> records = new ArrayList<>();

        for (int i = 0; i < phones.size(); i += BATCH_MAX_PHONES) {
            int end = Math.min(i + BATCH_MAX_PHONES, phones.size());
            List<String> batch = phones.subList(i, end);
            String phonesStr = String.join(",", batch);

            JdCloudSmsResponse response = smsGatewayService.send(phonesStr, fullContent);
            String reqId = null;
            int status;
            if (response != null && response.isSuccess()) {
                status = 1;
                reqId = response.getReqId();
            } else {
                status = 0;
            }

            for (String phone : batch) {
                SmsRecord record = new SmsRecord();
                record.setAppId(appId);
                record.setPhone(phone);
                record.setContent(fullContent);
                record.setCharCount(charCount);
                record.setBillingCount(billingCount);
                record.setPrice(price);
                record.setTotalFee(totalFee);
                record.setReqId(reqId);
                record.setStatus(status);
                record.setCreateTime(LocalDateTime.now());
                save(record);
                records.add(record);
            }
        }
        return records;
    }

    private String buildFullContent(String content, String signature, Integer smsType) {
        StringBuilder fullMsg = new StringBuilder();
        if (signature != null && !signature.isEmpty()) {
            fullMsg.append("【").append(signature).append("】");
        }
        fullMsg.append(content);
        if (smsType != null && smsType == 2) {
            fullMsg.append(UNSUBSCRIBE_TEXT);
        }
        return fullMsg.toString();
    }

    @Override
    public void updateStatusByReqIdAndPhone(String reqId, String phone, int status) {
        update(new UpdateWrapper<SmsRecord>().eq("req_id", reqId).eq("phone", phone)
                .set("status", status));
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
        String[] headers = {"ID", "应用ID", "手机号", "内容", "字符数", "计费条数", "单价", "总费用", "批次ID", "状态", "发送时间"};
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
            row.createCell(8).setCellValue(r.getReqId() != null ? r.getReqId() : "");
            row.createCell(9).setCellValue(statusText(r.getStatus()));
            row.createCell(10).setCellValue(r.getCreateTime() != null ? r.getCreateTime().format(FORMATTER) : "");
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        workbook.write(out);
        workbook.close();
        return out.toByteArray();
    }

    private String statusText(Integer status) {
        if (status == null) return "未知";
        switch (status) {
            case 0: return "提交失败";
            case 1: return "已提交";
            case 2: return "发送成功";
            case 3: return "发送失败";
            default: return "未知";
        }
    }
}
