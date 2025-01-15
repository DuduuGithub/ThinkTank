package controller;

import db.vo.Document;
import service.DocumentService;
import view.ThymeleafViewResolver;
import org.thymeleaf.context.Context;
import logger.SimpleLogger; // 引入自定义的 SimpleLogger
import service.VectorService;
import service.ClsTokenGenerater;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import logger.SimpleLogger;
public class DocumentListViewController {

    private DocumentService documentService;
    private ThymeleafViewResolver viewResolver;
    private VectorService vectorService;

    public DocumentListViewController(DocumentService documentService,ServletContext servletContext) {
        this.documentService = documentService;

        this.viewResolver = new ThymeleafViewResolver(servletContext);  // 使用你自己的 Thymeleaf 视图解析器
        this.vectorService = new VectorService();
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

        //设置编码以读取汉字
        request.setCharacterEncoding("UTF-8");


        // 记录请求到日志
        String method = request.getMethod();
        SimpleLogger.log("Received " + method + " request for document list view");

        // 从session获取当前登录用户的ID
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            SimpleLogger.log("No user logged in - redirecting to login page");
            response.sendRedirect("/login");
            return;
        }

        List<Document> documentList;
        List<Document> recommendedDocuments;
        Context context = new Context();

        if ("GET".equals(method)) {
            // GET请求：获取用户所有文档并基于这些文档推荐
            documentList = documentService.searchDocuments(userId, "", "", "");
            recommendedDocuments = getRecommendedDocuments(userId, documentList, null);
        } else {
            // POST请求：根据搜索条件筛选文档
            String title = request.getParameter("title") != null ? request.getParameter("title") : "";
            String keywords = request.getParameter("keywords") != null ? request.getParameter("keywords") : "";
            String subject = request.getParameter("subject") != null ? request.getParameter("subject") : "";

            documentList = documentService.searchDocuments(userId, title, keywords, subject);
            
            // 基于搜索结果进行推荐，而不是搜索文本
            recommendedDocuments = getRecommendedDocuments(userId, documentList, null);

            context.setVariable("titleFilter", title);
            context.setVariable("keywordsFilter", keywords);
            context.setVariable("subjectFilter", subject);
        }

        context.setVariable("documentList", documentList);
        context.setVariable("recommendedDocuments", recommendedDocuments);
        
        String renderedHtml = viewResolver.render("DocumentListView", context);
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");
        response.getWriter().write(renderedHtml);
    }

    private List<Document> getRecommendedDocuments(int userId, List<Document> userDocuments, String searchText) throws Exception {
        // 获取所有非用户的文档ID
        List<Document> allDocuments = documentService.getAllDocumentsExceptUser(userId);
        List<Integer> candidateDocIds = allDocuments.stream()
            .map(Document::getDocumentId)
            .collect(Collectors.toList());
        
        // 如果没有文档可推荐，返回空列表
        if (candidateDocIds.isEmpty() || userDocuments.isEmpty()) {
            SimpleLogger.log("No documents available for recommendation");
            return new ArrayList<>();
        }

        // 获取源文档ID列表
        List<Integer> sourceDocIds = userDocuments.stream()
            .map(Document::getDocumentId)
            .collect(Collectors.toList());

        SimpleLogger.log("Source document IDs: " + sourceDocIds);
        SimpleLogger.log("Candidate document IDs: " + candidateDocIds);

        // 使用VectorService找到最相似的文章
        List<Integer> similarDocIds = vectorService.findMostSimilarArticles(
            sourceDocIds,
            candidateDocIds,
            7  // 获取前7个最相似的文章
        );

        SimpleLogger.log("Similar document IDs: " + similarDocIds);

        // 获取相似文档的完整信息
        return similarDocIds.stream()
            .map(documentService::getDocumentById)
            .filter(Objects::nonNull)
            .collect(Collectors.toList());
    }
}