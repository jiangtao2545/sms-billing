package cn.gt.smsbilling.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("sms_application")
public class SmsApplication {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String appId;
    private String appName;
    private String appKey;
    private BigDecimal price;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
