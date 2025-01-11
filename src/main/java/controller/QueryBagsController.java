package controller;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import service.ReportBagService;

public class QueryBagsController {
    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应类型
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();

        // 从session获取用户ID
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        if (userId == null) {
            out.print("[]"); // 未登录返回空数组
            return;
        }

        // 直接调用service层方法获取JSON
        String json = ReportBagService.queryBags(userId);
        out.print(json);
    }
}