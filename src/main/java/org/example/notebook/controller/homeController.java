package org.example.notebook.controller;

import jakarta.servlet.http.HttpSession;
import org.example.notebook.pojo.user;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class homeController {

    @GetMapping("/")
    public String indexPage(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "index";
    }
}