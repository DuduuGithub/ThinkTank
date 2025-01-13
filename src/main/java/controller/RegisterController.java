package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import db.dao.proxy.UserDaoProxy;
import db.factory.DaoFactory;
import db.vo.User;

public class RegisterController {
    public static void register(HttpServletRequest request, HttpServletResponse response) throws Exception{
        // 获取用户名和密码
        String userId = request.getParameter("userId");
        String password = request.getParameter("password");
    
        UserDaoProxy userDapProxy = DaoFactory.getInstance().getUserDao();
        userDapProxy.insert(new User(Integer.parseInt(userId), password));

        HttpSession session = request.getSession();
        session.setAttribute("userId", Integer.parseInt(userId));
        // 设置会话过期时间（例如 30 分钟）
        session.setMaxInactiveInterval(30 * 60);
        // 重定向到首页
        response.sendRedirect("/aireport/index.html");
    }
}
