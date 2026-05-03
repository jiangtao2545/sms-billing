package com.jiangtao.smsbilling.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sms_record")
public class SmsRecord {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String appId;
    private String phone;
    private String content;
    private Integer charCount;
    private Integer billingCount;
    private BigDecimal price;
    private BigDecimal totalFee;
    private Integer status;
    private LocalDateTime createTime;
}
