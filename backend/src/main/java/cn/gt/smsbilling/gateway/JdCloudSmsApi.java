package cn.gt.smsbilling.gateway;

import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitClient;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

@RetrofitClient(baseUrl = "${sms.gateway.base-url}")
public interface JdCloudSmsApi {

    @POST("/HttpSmsMt")
    @FormUrlEncoded
    Call<JdCloudSmsResponse> sendSms(
            @Field("name") String name,
            @Field("pwd") String pwd,
            @Field("subid") String subid,
            @Field("phone") String phone,
            @Field("content") String content,
            @Field("mttime") String mttime,
            @Field("rpttype") int rpttype,
            @Field("extend") String extend
    );
}
