package cn.gt.smsbilling.gateway;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "sms.gateway")
public class SmsGatewayProperties {
    private String baseUrl;
    private String name;
    private String password;
    private String subid = "";
    private String callbackHost;

    public String getBaseUrl() { return baseUrl; }
    public void setBaseUrl(String baseUrl) { this.baseUrl = baseUrl; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getSubid() { return subid; }
    public void setSubid(String subid) { this.subid = subid; }
    public String getCallbackHost() { return callbackHost; }
    public void setCallbackHost(String callbackHost) { this.callbackHost = callbackHost; }
}
