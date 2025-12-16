package org.example.notebook.interceptor;
import org.example.notebook.pojo.user;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 获取请求的URI
        String uri = request.getRequestURI();

        // 如果是登录页面、注册页面、登录接口、注册接口或静态资源，直接放行
        if (uri.startsWith("/login") || uri.startsWith("/api/login")
                || uri.startsWith("/register") || uri.startsWith("/api/register")
                || uri.startsWith("/images/") || uri.startsWith("/css/") || uri.startsWith("/js/")) {
            return true;
        }

        // 获取session
        HttpSession session = request.getSession();
        user currentUser = (user) session.getAttribute("currentUser");

        // 如果没有登录，重定向到登录页面
        if (currentUser == null) {
            response.sendRedirect("/login");
            return false;
        }

        // 如果已登录，放行
        return true;
    }
}