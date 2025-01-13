package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import service.BagOperationService;

public class BagOperationController {

    public static void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");

        String resultString;

        if(action.equals("queryBags")){
            // 从session获取用户ID
            HttpSession session = request.getSession();
            Integer userId = (Integer) session.getAttribute("userId");
            
            if (userId == null) {
                
            }
            resultString = BagOperationService.queryBags(userId);
        }else{
            int bagId = Integer.parseInt(request.getParameter("bagId"));
            int documentId = Integer.parseInt(request.getParameter("documentId"));
            
            if(action.equals("delete")) {
                resultString = BagOperationService.removeFromBag(bagId, documentId);
            } else if(action.equals("add")) {
                resultString = BagOperationService.addToBag(bagId, documentId);
            } else {
                resultString = "{\"success\": false, \"message\": \"未知操作\"}";
            }
        }
        
        // 写入响应
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(resultString);
        out.flush();
    }
}