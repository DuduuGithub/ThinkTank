package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class AddReportController {
    public static void processRequest(HttpServletRequest request,HttpServletResponse response){
        int documentId = Integer.parseInt(request.getParameter("documentId"));
        String title = request.getParameter("title");
        String keywords = request.getParameter("keywords");
        String subject = request.getParameter("subject");
    }
}
