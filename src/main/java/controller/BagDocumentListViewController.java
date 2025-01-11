package controller;

import db.vo.Document;
import service.DocumentService;
import view.ThymeleafViewResolver;
import org.thymeleaf.context.Context;
import logger.SimpleLogger;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class BagDocumentListViewController {

    private DocumentService documentService;
    private ThymeleafViewResolver viewResolver;

    public BagDocumentListViewController(DocumentService documentService, ServletContext servletContext) {
        this.documentService = documentService;
        this.viewResolver = new ThymeleafViewResolver(servletContext);
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");

        // 获取报告包ID
        String bagIdStr = request.getParameter("bagId");
        if (bagIdStr == null || bagIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "需要提供报告包ID");
            return;
        }

        int bagId = Integer.parseInt(bagIdStr);

        // 获取报告包中的文档列表
        List<Document> documentList = documentService.getDocumentsInBag(bagId);

        // 设置上下文
        Context context = new Context();
        context.setVariable("bagId", bagId);
        context.setVariable("documentList", documentList);

        // 渲染视图
        String renderedHtml = viewResolver.render("BagDocumentListView", context);
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");
        response.getWriter().write(renderedHtml);
    }
}