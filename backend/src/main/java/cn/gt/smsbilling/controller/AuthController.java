package cn.gt.smsbilling.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import cn.gt.smsbilling.common.Result;
import cn.gt.smsbilling.entity.SysLoginLog;
import cn.gt.smsbilling.entity.SysUser;
import cn.gt.smsbilling.service.SysLoginLogService;
import cn.gt.smsbilling.service.SysUserService;
import cn.gt.smsbilling.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private SysLoginLogService loginLogService;

    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> body, HttpServletRequest request) {
        String username = body.get("username");
        String password = body.get("password");
        // BCrypt 最多处理 72 个字符，超出部分会被静默截断，因此在此处主动拒绝超长密码
        if (password != null && password.length() > 72) {
            return Result.error("密码长度不能超过72个字符");
        }
        try {
            SysUser user = sysUserService.login(username, password);
            String token = jwtUtil.generateToken(user.getId(), user.getUsername());
            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");
            loginLogService.saveLog(user.getId(), user.getUsername(), ip, ua, 1);
            Map<String, Object> data = new HashMap<>();
            data.put("token", token);
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("nickname", user.getNickname());
            return Result.success(data);
        } catch (RuntimeException e) {
            String ip = request.getRemoteAddr();
            String ua = request.getHeader("User-Agent");
            loginLogService.saveLog(null, username, ip, ua, 0);
            return Result.error(e.getMessage());
        }
    }

    @PostMapping("/logout")
    public Result<Void> logout() {
        return Result.success();
    }

    @PostMapping("/password")
    public Result<Void> changePassword(@RequestBody Map<String, String> body, HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        String oldPassword = body.get("oldPassword");
        String newPassword = body.get("newPassword");
        // BCrypt 最多处理 72 个字符，超出部分会被静默截断，因此在此处主动拒绝超长密码
        if (newPassword != null && newPassword.length() > 72) {
            return Result.error("密码长度不能超过72个字符");
        }
        sysUserService.changePassword(userId, oldPassword, newPassword);
        return Result.success();
    }

    @GetMapping("/loginLogs")
    public Result<Object> loginLogs(@RequestParam(defaultValue = "1") int pageNum,
                                     @RequestParam(defaultValue = "10") int pageSize,
                                     HttpServletRequest request) {
        Long userId = (Long) request.getAttribute("userId");
        Page<SysLoginLog> page = new Page<>(pageNum, pageSize);
        loginLogService.page(page, new QueryWrapper<SysLoginLog>().eq("user_id", userId).orderByDesc("login_time"));
        return Result.success(page);
    }
}
