package org.example.notebook.controller;

import jakarta.servlet.http.HttpSession;
import org.example.notebook.pojo.user;
import org.example.notebook.sevice.userService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Controller
public class LoginController {

    @Autowired
    private userService userService;

    @GetMapping("/login")
    public String loginPage(HttpSession session) {
        // 如果已经登录，重定向到主页
        if (session.getAttribute("currentUser") != null) {
            return "redirect:/";
        }
        return "login";
    }

    @PostMapping("/api/login")
    @ResponseBody
    public Map<String, Object> login(@RequestParam String username,
                                     @RequestParam String password,
                                     HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        // 使用数据库验证用户
        user authenticatedUser = userService.authenticate(username, password);

        if (authenticatedUser != null) {
            // 将用户信息存储在session中
            session.setAttribute("currentUser", authenticatedUser);
            result.put("success", true);
            result.put("message", "登录成功");
        } else {
            result.put("success", false);
            result.put("message", "用户名或密码错误");
        }

        return result;
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/api/register")
    @ResponseBody
    public Map<String, Object> register(@RequestBody Map<String, String> requestData) {
        String username = requestData.get("username");
        String password = requestData.get("password");

        Map<String, Object> result = new HashMap<>();

        System.out.println("开始注册用户: " + username);

        try {
            // 注册用户
            boolean success = userService.register(username, password);

            System.out.println("注册结果: " + success);

            if (success) {
                result.put("success", true);
                result.put("message", "注册成功");
            } else {
                result.put("success", false);
                result.put("message", "注册失败，用户名可能已存在");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "注册过程中发生错误: " + e.getMessage());
        }

        return result;
    }
}
