package controller;

import java.io.IOException;
import java.io.PrintWriter;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import logger.SimpleLogger;
import service.BagOperationService;

public class BagOperationController {

    public static void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        
        String action = request.getParameter("action");
        String resultString;

        switch(action){
            case "queryBags":
                HttpSession session = request.getSession();
                Integer userId = (Integer) session.getAttribute("userId");
                resultString = BagOperationService.queryBags(userId);
                break;

            case "add":
                int bagId = Integer.parseInt(request.getParameter("bagId"));
                int documentId = Integer.parseInt(request.getParameter("documentId"));
                resultString = BagOperationService.addToBag(bagId, documentId);
                break;

            case "delete":
                bagId = Integer.parseInt(request.getParameter("bagId"));
                documentId = Integer.parseInt(request.getParameter("documentId"));
                resultString = BagOperationService.removeFromBag(bagId, documentId);
                break;

            case "createBag":
                session = request.getSession();
                userId = (Integer) session.getAttribute("userId");
                String bagName = request.getParameter("bagName");
                resultString = BagOperationService.createBag(bagName, userId);
                break;

            case "deleteBag":
                bagId = Integer.parseInt(request.getParameter("bagId"));
                SimpleLogger.log("deleteBag: " + bagId);
                resultString = BagOperationService.deleteBag(bagId);
                break;

            default:
                resultString = "{\"success\": false, \"message\": \"未知操作\"}";
                break;
        }

        // 写入响应
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(resultString);
        out.flush();
    }
}