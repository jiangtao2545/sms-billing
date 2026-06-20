package cn.gt.smsbilling.controller;

import cn.gt.smsbilling.service.SmsRecordService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SmsCallbackController {
    private static final Logger log = LoggerFactory.getLogger(SmsCallbackController.class);

    @Autowired
    private SmsRecordService smsRecordService;

    @GetMapping("/api/callback/sms")
    public String callback(
            @RequestParam int msgtype,
            @RequestParam String phone,
            @RequestParam(required = false) String reqid,
            @RequestParam(required = false) String receivetime,
            @RequestParam(required = false) String sendtime,
            @RequestParam(required = false) Integer state,
            @RequestParam(required = false) String content,
            @RequestParam(required = false) String subid) {

        if (msgtype == 2) {
            // 状态报告
            log.info("Status report: reqId={}, phone={}, state={}, sendTime={}, receiveTime={}",
                    reqid, phone, state, sendtime, receivetime);
            if (reqid != null && state != null) {
                int recordStatus = (state == 0) ? 2 : 3; // state=0成功→2, 其他失败→3
                smsRecordService.updateStatusByReqIdAndPhone(reqid, phone, recordStatus);
            }
        } else if (msgtype == 0) {
            // 上行消息
            log.info("Upstream message: phone={}, content={}, subid={}, receiveTime={}",
                    phone, content, subid, receivetime);
        } else {
            log.warn("Unknown msgtype={} from phone={}", msgtype, phone);
        }

        return "ok";
    }
}
