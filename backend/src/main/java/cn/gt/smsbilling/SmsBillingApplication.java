package cn.gt.smsbilling;

import com.ctrip.framework.apollo.bootstrap.ApolloApplicationListener;
import com.github.lianjiatech.retrofit.spring.boot.core.RetrofitScan;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@MapperScan("cn.gt.smsbilling.mapper")
@RetrofitScan("cn.gt.smsbilling.gateway")
public class SmsBillingApplication {
    public static void main(String[] args) {
        // 启用 Apollo 配置刷新
        ApolloApplicationListener.refreshDynamicConfig();
        SpringApplication.run(SmsBillingApplication.class, args);
    }
}
