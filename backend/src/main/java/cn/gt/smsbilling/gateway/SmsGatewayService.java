package cn.gt.smsbilling.gateway;

import cn.hutool.crypto.digest.DigestUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class SmsGatewayService {
    private static final Logger log = LoggerFactory.getLogger(SmsGatewayService.class);
    private static final DateTimeFormatter MT_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");

    @Autowired
    private JdCloudSmsApi jdCloudSmsApi;

    @Autowired
    private SmsGatewayProperties properties;

    public JdCloudSmsResponse send(String phone, String content) {
        return send(phone, content, "");
    }

    public JdCloudSmsResponse send(String phone, String content, String extend) {
        String mttime = LocalDateTime.now().format(MT_TIME_FORMATTER);
        String pwd = DigestUtil.md5Hex(properties.getPassword() + mttime);

        try {
            JdCloudSmsResponse response = jdCloudSmsApi.sendSms(
                    properties.getName(),
                    pwd,
                    properties.getSubid(),
                    phone,
                    content,
                    mttime,
                    1,  // rpttype: 1=JSON
                    extend
            ).execute().body();

            if (response != null) {
             log.info("SMS gateway response: code={}, msg={}, reqId={}, phone={}",
               response.getReqCode(), response.getReqMsg(), response.getReqId(), phone);
            }
            return response;
        } catch (IOException e) {
            log.error("SMS gateway call failed for phone={}: {}", phone, e.getMessage());
            return null;
        }
    }
}
