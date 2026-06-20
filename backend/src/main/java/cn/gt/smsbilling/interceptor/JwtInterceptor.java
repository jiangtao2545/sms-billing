package cn.gt.smsbilling.interceptor;

import com.fasterxml.jackson.databind.ObjectMapper;
import cn.gt.smsbilling.common.Result;
import cn.gt.smsbilling.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class JwtInterceptor implements HandlerInterceptor {
    @Autowired
    private JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String token = request.getHeader("Authorization");
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        if (token == null || !jwtUtil.isTokenValid(token)) {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(401);
            response.getWriter().write(objectMapper.writeValueAsString(Result.error(401, "未登录或登录已过期")));
            return false;
        }
        request.setAttribute("userId", jwtUtil.getUserId(token));
        request.setAttribute("username", jwtUtil.getUsername(token));
        return true;
    }
}
