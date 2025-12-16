package org.example.notebook.controller;

import jakarta.servlet.http.HttpSession;
import org.example.notebook.pojo.notebook;
import org.example.notebook.pojo.user;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.example.notebook.sevice.notebookService;
@RestController
@RequestMapping("/notebook")
public class notebookController {

    @Autowired
    private notebookService notebookService;

    @GetMapping("/list")
    @ResponseBody
    public List<notebook> getAllNotebooks(@RequestParam(required = false) String search,
                                          HttpSession session) {
        // 获取当前登录用户
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return new ArrayList<>(); // 如果未登录，返回空列表
        }

        // 根据用户ID获取错题集
        List<notebook> notebooks = notebookService.getUserNotebooks(currentUser.getId());

        // 如果有搜索参数，则可以根据名称筛选
        if (search != null && !search.isEmpty()) {
            return notebooks.stream()
                    .filter(nb -> nb.getName().contains(search))
                    .collect(Collectors.toList());
        }

        return notebooks;
    }
    @PostMapping("/add")
    @ResponseBody
    public Map<String, Object> addNotebook(@ModelAttribute notebook notebook, HttpSession session) {
        Map<String, Object> response = new HashMap<>();
        try {
            // 获取当前登录用户
            user currentUser = (user) session.getAttribute("currentUser");
            if (currentUser == null) {
                response.put("success", false);
                response.put("message", "用户未登录");
                return response;
            }

            // 创建错题集并关联用户
            int result = notebookService.createNotebook(notebook, currentUser.getId());

            if (result > 0) {
                response.put("success", true);
                response.put("message", "错题集添加成功");
            } else {
                response.put("success", false);
                response.put("message", "错题集添加失败");
            }
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "错题集添加失败: " + e.getMessage());
        }
        return response;
    }
}