// src/main/java/org/example/notebook/controller/userController.java
package org.example.notebook.controller;

import org.example.notebook.sevice.userService;

import jakarta.servlet.http.HttpSession;
import org.example.notebook.pojo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/user")
public class userController {
    @Autowired
    private userService userService;

    @Value("${upload.path}")
    private String uploadPath;

    @PostMapping("/update-avatar")
    @ResponseBody
    public Map<String, Object> updateAvatar(@RequestParam("avatar") MultipartFile avatarFile,
                                            HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        try {
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            // 检查文件是否为空
            if (avatarFile == null || avatarFile.isEmpty()) {
                result.put("success", false);
                result.put("message", "请选择头像文件");
                return result;
            }

            // 验证文件类型
            String originalFilename = avatarFile.getOriginalFilename();
            if (originalFilename == null ) {
                result.put("success", false);
                result.put("message", "只支持 JPG、PNG、GIF 格式的图片");
                return result;
            }

            // 验证文件大小（限制为5MB）
            if (avatarFile.getSize() > 5 * 1024 * 1024) {
                result.put("success", false);
                result.put("message", "图片大小不能超过5MB");
                return result;
            }

            // 使用配置的上传路径
            String uploadDir = uploadPath + "/avatar/";
            File dir = new File(uploadDir);
            if (!dir.exists()) {
                boolean created = dir.mkdirs();
                if (!created) {
                    throw new RuntimeException("无法创建头像上传目录: " + uploadDir);
                }
            }

            // 生成唯一文件名
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String uniqueFilename = System.currentTimeMillis() + "_" +
                    Math.round(Math.random() * 1000) + extension;

            // 保存文件
            File dest = new File(dir, uniqueFilename);
            avatarFile.transferTo(dest);

            // 设置头像URL路径
            String avatarUrl = "/images/avatar/" + uniqueFilename;

            // 更新数据库中的用户头像信息
            userService.updateUserAvatar(currentUser.getId(), avatarUrl);

            // 更新session中的用户信息
            currentUser.setAvatar(avatarUrl);
            session.setAttribute("currentUser", currentUser);

            result.put("success", true);
            result.put("message", "头像更新成功");
            result.put("avatarUrl", avatarUrl);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "头像更新失败: " + e.getMessage());
            e.printStackTrace(); // 记录错误日志
        }

        return result;
    }

    @PostMapping("/update-password")
    @ResponseBody
    public Map<String, Object> updatePassword(@RequestParam String currentPassword,
                                              @RequestParam String newPassword,
                                              HttpSession session) {
        Map<String, Object> result = new HashMap<>();

        try {
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                result.put("success", false);
                result.put("message", "用户未登录");
                return result;
            }

            // 验证当前密码是否正确
            if (!currentUser.getPassword().equals(currentPassword)) {
                result.put("success", false);
                result.put("message", "当前密码不正确");
                return result;
            }

            // 更新密码
            userService.updateUserPassword(currentUser.getId(), newPassword);

            // 更新session中的用户信息
            currentUser.setPassword(newPassword);
            session.setAttribute("currentUser", currentUser);

            result.put("success", true);
            result.put("message", "密码修改成功");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "密码修改失败: " + e.getMessage());
        }

        return result;
    }
    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
