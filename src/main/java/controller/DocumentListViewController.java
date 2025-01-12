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

        int userId = 1; // 默认的 userId，实际应从 session 获取
        // int userId = (int) request.getSession().getAttribute("userId");

        List<Document> documentList;
        List<Document> recommendedDocuments;
        Context context = new Context();

        if ("GET".equals(method)) {
            // GET请求：获取用户所有文档并基于平均向量推荐
            documentList = documentService.searchDocuments(userId, "", "", "");
            recommendedDocuments = getRecommendedDocuments(userId, documentList, null);
        } else {
            // POST请求：根据搜索条件筛选文档并推荐
            String title = request.getParameter("title") != null ? request.getParameter("title") : "";
            String keywords = request.getParameter("keywords") != null ? request.getParameter("keywords") : "";
            String subject = request.getParameter("subject") != null ? request.getParameter("subject") : "";

            documentList = documentService.searchDocuments(userId, title, keywords, subject);
            
            // 基于搜索文本进行推荐
            String searchText = title + " " + keywords + " " + subject;
            recommendedDocuments = getRecommendedDocuments(userId, null, searchText);

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
        // 获取所有非用户的文档
        List<Document> allDocuments = documentService.getAllDocumentsExceptUser(userId);
        
        // 如果没有文档可推荐，返回空列表
        if (allDocuments.isEmpty()) {
            return new ArrayList<>();
        }

        double[] targetVector;
        if (searchText != null && !searchText.trim().isEmpty()) {
            // 基于搜索文本的推荐
            targetVector = vectorService.vectorize(searchText);
        } else {
            // 基于用户文档平均向量的推荐
            List<double[]> userDocumentVectors = new ArrayList<>();
            for (Document doc : userDocuments) {
                userDocumentVectors.add(vectorService.vectorize(doc.getTitle() + " " + doc.getKeywords() + " " + doc.getSubject()));
            }
            targetVector = vectorService.calculateAverageVector(userDocumentVectors);
        }

        // 如果无法获得目标向量，返回空列表
        if (targetVector == null) {
            return new ArrayList<>();
        }

        // 获取所有候选文档的向量
        List<double[]> candidateVectors = new ArrayList<>();
        for (Document doc : allDocuments) {
            candidateVectors.add(vectorService.vectorize(doc.getTitle() + " " + doc.getKeywords() + " " + doc.getSubject()));
        }

        // 获取前7个最相似的文档
        List<VectorService.SimilarityResult> topResults = vectorService.getTopNSimilarVectors(targetVector, candidateVectors, 7);
        
        // 转换结果为文档列表
        List<Document> recommendedDocs = new ArrayList<>();
        for (VectorService.SimilarityResult result : topResults) {
            recommendedDocs.add(allDocuments.get(result.index));
        }

        return recommendedDocs;
    }
}