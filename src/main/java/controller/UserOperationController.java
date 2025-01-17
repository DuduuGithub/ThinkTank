package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import service.UserOperationService;

public class UserOperationController {
    public static void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception{
        String action = request.getParameter("action");
        if(action.equals("register")){
            // 获取用户输入的密码
            String password = request.getParameter("password");
            
            // 获取插入结果
            String result = UserOperationService.register(password);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");
            response.getWriter().write(result);
        }
        else if(action.equals("login")){
            // 获取用户名和密码
            String userId = request.getParameter("userId");
            String password = request.getParameter("password");
        
            if (UserOperationService.userVerify(userId, password)) {
                // 认证成功，设置会话属性
                HttpSession session = request.getSession();
                session.setAttribute("userId", Integer.parseInt(userId));
                // 设置会话过期时间（例如 30 分钟）
                session.setMaxInactiveInterval(30 * 60);
                // 重定向到首页
                response.sendRedirect("/aireport/index.html");
            } else {
                // 认证失败，重定向回登录页面并添加错误参数
                //response.getWriter().println(userId + '\n' + password);
                response.sendRedirect("/aireport/login.html?error=1");
            }
        }
    }
}
