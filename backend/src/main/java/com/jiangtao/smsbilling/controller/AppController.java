package com.jiangtao.smsbilling.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.jiangtao.smsbilling.common.Result;
import com.jiangtao.smsbilling.entity.SmsApplication;
import com.jiangtao.smsbilling.service.SmsApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/app")
public class AppController {
    @Autowired
    private SmsApplicationService appService;

    @GetMapping("/list")
    public Result<Object> list(@RequestParam(defaultValue = "1") int pageNum,
                                @RequestParam(defaultValue = "10") int pageSize) {
        Page<SmsApplication> page = new Page<>(pageNum, pageSize);
        appService.page(page);
        return Result.success(page);
    }

    @PostMapping("/create")
    public Result<SmsApplication> create(@RequestBody Map<String, Object> body) {
        String appName = (String) body.get("appName");
        BigDecimal price = new BigDecimal(body.get("price").toString());
        SmsApplication app = appService.createApp(appName, price);
        return Result.success(app);
    }

    @PutMapping("/update/{id}")
    public Result<Void> update(@PathVariable Long id, @RequestBody SmsApplication app) {
        app.setId(id);
        app.setUpdateTime(LocalDateTime.now());
        appService.updateById(app);
        return Result.success();
    }

    @PutMapping("/status/{id}")
    public Result<Void> toggleStatus(@PathVariable Long id, @RequestBody Map<String, Integer> body) {
        SmsApplication app = appService.getById(id);
        if (app == null) return Result.error("应用不存在");
        app.setStatus(body.get("status"));
        app.setUpdateTime(LocalDateTime.now());
        appService.updateById(app);
        return Result.success();
    }

    @PostMapping("/resetKey/{id}")
    public Result<String> resetKey(@PathVariable Long id) {
        String newKey = appService.resetAppKey(id);
        return Result.success(newKey);
    }

    @DeleteMapping("/delete/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!appService.canDelete(id)) {
            return Result.error("该应用存在发送记录，无法删除");
        }
        appService.removeById(id);
        return Result.success();
    }

    @GetMapping("/detail/{id}")
    public Result<SmsApplication> detail(@PathVariable Long id) {
        return Result.success(appService.getById(id));
    }
}
