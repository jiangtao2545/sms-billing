package cn.gt.smsbilling.gateway;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

public class JdCloudSmsResponse  implements Serializable {

    @JsonProperty("ReqCode")
    private String reqCode;
    @JsonProperty("ReqMsg")
    private String reqMsg;
    @JsonProperty("ReqId")
    private String reqId;

    public String getReqCode() {
        return reqCode;
    }

    public void setReqCode(String reqCode) {
        this.reqCode = reqCode;
    }

    public String getReqMsg() {
        return reqMsg;
    }

    public void setReqMsg(String reqMsg) {
        this.reqMsg = reqMsg;
    }

    public String getReqId() {
        return reqId;
    }

    public void setReqId(String reqId) {
        this.reqId = reqId;
    }

    public boolean isSuccess() {
        return "00".equals(reqCode);
    }
}
