package controller;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import service.DocumentService;

public class DeleteDocumentController {
    public static void processRequest(HttpServletRequest request,HttpServletResponse response) throws Exception{
        int documentId = Integer.parseInt(request.getParameter("documentId"));
        
        int userId = (int) request.getSession().getAttribute("userId");

        String resultString = DocumentService.deleteDocument(documentId, userId);

        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.print(resultString);
        out.flush();
    }
}
