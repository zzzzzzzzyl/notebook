package org.example.notebook.controller;

import jakarta.servlet.http.HttpSession;
import org.example.notebook.pojo.user;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {
    @GetMapping("/mistake")
    public String mistake(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "index";
    }

    @GetMapping("/mistake/manage")
    public String mistakeManagement(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "mistake_management";
    }

    @GetMapping("/mistake/add")
    public String addMistake(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "add_mistake";
    }

    @GetMapping("/mistake/view")
    public String viewMistake(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "mistake_view";
    }

    @GetMapping("/mistake/test")
    public String testMistake(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "mistake_test";
    }

    @GetMapping("/mistake/review")
    public String review(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "review";
    }

    @GetMapping("/mistake/review-test")
    public String reviewTest(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "review_test";
    }

    @GetMapping("/notebook/manage")
    public String notebookManagement(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "notebook_management";
    }

    @GetMapping("/settings")
    public String settingsPage(Model model, HttpSession session) {
        user currentUser = (user) session.getAttribute("currentUser");
        if (currentUser == null) {
            return "redirect:/login";
        }
        model.addAttribute("currentUser", currentUser);
        return "settings";
    }
}