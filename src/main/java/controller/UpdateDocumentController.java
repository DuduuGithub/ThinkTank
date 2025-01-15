package controller;

import db.dao.proxy.DocumentDaoProxy;
import db.factory.DaoFactory;
import db.vo.Document;
import logger.SimpleLogger;
import service.DocumentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;

public class UpdateDocumentController {
    public static void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        String action = request.getParameter("action");
        int documentId = Integer.parseInt(request.getParameter("documentId"));
        SimpleLogger.log("UpdateDocumentController: action = " + action + ", documentId = " + documentId + "\n\n\n\n\n\n\n\n\n\n\n\n");
        DocumentDaoProxy documentDaoProxy = DaoFactory.getInstance().getDocumentDao();

        if (action.equals("get")) {
            Document document = documentDaoProxy.findById(documentId);
            out.print("{\"title\": \"" + document.getTitle() + 
                      "\", \"keywords\": \"" + document.getKeywords() + 
                      "\", \"subject\": \"" + document.getSubject() + "\"}");
            out.flush();
        } else if (action.equals("update")) {
            String title = request.getParameter("title");
            String keywords = request.getParameter("keywords");
            String subject = request.getParameter("subject");
            
            DocumentService documentService = new DocumentService();
            Document document = documentService.getDocumentById(documentId);
            if (document == null) {
                out.print("{\"success\": false, \"message\": \"文档不存在\"}");
                out.flush();
                return;
            }
            document.setTitle(title);
            document.setKeywords(keywords);
            document.setSubject(subject);
            
            boolean success = documentDaoProxy.update(document);
            
            if (success) {
                out.print("{\"success\": true, \"message\": \"修改成功\"}");
            } else {
                out.print("{\"success\": false, \"message\": \"修改失败\"}");
            }
            out.flush();
        }
        
    }
}
