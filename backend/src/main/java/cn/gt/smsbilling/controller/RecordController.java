package cn.gt.smsbilling.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.gt.smsbilling.common.Result;
import cn.gt.smsbilling.entity.SmsRecord;
import cn.gt.smsbilling.service.SmsRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestController
@RequestMapping("/api/record")
public class RecordController {
    @Autowired
    private SmsRecordService smsRecordService;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @GetMapping("/list")
    public Result<Object> list(@RequestParam(defaultValue = "1") int pageNum,
                                @RequestParam(defaultValue = "10") int pageSize,
                                @RequestParam(required = false) String appId,
                                @RequestParam(required = false) String phone,
                                @RequestParam(required = false) String startTime,
                                @RequestParam(required = false) String endTime,
                                @RequestParam(required = false) Integer status) {
        Page<SmsRecord> page = new Page<>(pageNum, pageSize);
        QueryWrapper<SmsRecord> wrapper = new QueryWrapper<>();
        if (StringUtils.hasText(appId)) wrapper.eq("app_id", appId);
        if (StringUtils.hasText(phone)) wrapper.like("phone", phone);
        if (StringUtils.hasText(startTime)) wrapper.ge("create_time", LocalDateTime.parse(startTime, FORMATTER));
        if (StringUtils.hasText(endTime)) wrapper.le("create_time", LocalDateTime.parse(endTime, FORMATTER));
        if (status != null) wrapper.eq("status", status);
        wrapper.orderByDesc("create_time");
        smsRecordService.page(page, wrapper);
        return Result.success(page);
    }

    @GetMapping("/detail/{id}")
    public Result<SmsRecord> detail(@PathVariable Long id) {
        return Result.success(smsRecordService.getById(id));
    }

    @GetMapping("/export")
    public ResponseEntity<byte[]> export(@RequestParam(required = false) String appId,
                                          @RequestParam(required = false) String phone,
                                          @RequestParam(required = false) String startTime,
                                          @RequestParam(required = false) String endTime,
                                          @RequestParam(required = false) Integer status) throws Exception {
        byte[] data = smsRecordService.exportExcel(appId, phone, startTime, endTime, status);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        headers.setContentDispositionFormData("attachment", "sms_records.xlsx");
        return ResponseEntity.ok().headers(headers).body(data);
    }
}
