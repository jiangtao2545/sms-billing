package cn.gt.smsbilling.controller;

import cn.gt.smsbilling.common.Result;
import cn.gt.smsbilling.entity.SmsApplication;
import cn.gt.smsbilling.entity.SmsRecord;
import cn.gt.smsbilling.service.SmsApplicationService;
import cn.gt.smsbilling.service.SmsRecordService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

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
        SmsRecord record = smsRecordService.send(appId, phone, content, app.getPrice(), app.getSignature(), app.getSmsType());
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
        List<SmsRecord> records = smsRecordService.batchSend(appId, phones, content, app.getPrice(), app.getSignature(), app.getSmsType());
        return Result.success(records);
    }

    @PostMapping("/parseExcel")
    public Result<List<String>> parseExcel(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) return Result.error("文件为空");
        String filename = file.getOriginalFilename();
        if (filename == null || (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
            return Result.error("仅支持 .xlsx 或 .xls 格式");
        }

        Pattern phonePattern = Pattern.compile("^1[3-9]\\d{9}$");
        LinkedHashSet<String> phones = new LinkedHashSet<>();

        try (Workbook workbook = WorkbookFactory.create(file.getInputStream())) {
            Sheet sheet = workbook.getSheetAt(0);
            for (Row row : sheet) {
                Cell cell = row.getCell(0);
                if (cell == null) continue;
                String phone = getCellString(cell).replaceAll("\\s+", "");
                if (phone.isEmpty()) continue;
                // 处理科学计数法或带小数点的数字
                if (phone.contains(".")) {
                    phone = phone.substring(0, phone.indexOf('.'));
                }
                if (phonePattern.matcher(phone).matches() && phones.size() < 10000) {
                    phones.add(phone);
                }
            }
        } catch (Exception e) {
            return Result.error("文件解析失败: " + e.getMessage());
        }

        if (phones.isEmpty()) return Result.error("未找到有效的手机号");
        return Result.success(new ArrayList<>(phones));
    }

    @GetMapping("/downloadTemplate")
    public void downloadTemplate(HttpServletResponse response) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("手机号列表");
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("手机号（每行一个）");

        String[] examples = {"13800138000", "13900139000", "15000150000"};
        for (int i = 0; i < examples.length; i++) {
            Row row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(examples[i]);
        }

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding("UTF-8");
        String filename = URLEncoder.encode("手机号导入模板.xlsx", "UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename*=UTF-8''" + filename);

        try (OutputStream out = response.getOutputStream()) {
            workbook.write(out);
        }
        workbook.close();
    }

    private String getCellString(Cell cell) {
        if (cell.getCellType() == CellType.STRING) {
            return cell.getStringCellValue().trim();
        } else if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        }
        return cell.toString().trim();
    }
}
