package cn.gt.smsbilling.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.gt.smsbilling.common.Result;
import cn.gt.smsbilling.entity.SmsApplication;
import cn.gt.smsbilling.service.SmsApplicationService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class AppAuthInterceptor implements HandlerInterceptor {
    @Autowired
    private SmsApplicationService smsApplicationService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appId = request.getHeader("X-App-Id");
        String appKey = request.getHeader("X-App-Key");
        if (appId == null || appKey == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "缺少认证头")));
            return false;
        }
        SmsApplication app = smsApplicationService.getOne(
                new QueryWrapper<SmsApplication>().eq("app_id", appId).eq("app_key", appKey).eq("status", 1)
        );
        if (app == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "认证失败")));
            return false;
        }
        request.setAttribute("app", app);
        return true;
    }
}
