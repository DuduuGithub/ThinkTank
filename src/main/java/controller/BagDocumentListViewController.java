package controller;

import db.vo.Document;
import service.DocumentService;
import view.ThymeleafViewResolver;
import org.thymeleaf.context.Context;
import logger.SimpleLogger;
import service.VectorService;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;
import java.util.stream.Collectors;
import java.io.File;
import java.io.IOException;

public class BagDocumentListViewController {
    private DocumentService documentService;
    private ThymeleafViewResolver viewResolver;
    private VectorService vectorService;
    private static final String VECTOR_DIR = "D:\\java_hm_work\\tokens\\";

    public BagDocumentListViewController(DocumentService documentService, ServletContext servletContext) {
        this.documentService = documentService;
        this.viewResolver = new ThymeleafViewResolver(servletContext);
        this.vectorService = new VectorService();
    }

    public void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        request.setCharacterEncoding("UTF-8");
        String method = request.getMethod();
        SimpleLogger.log("Received " + method + " request for bag document list view");

        // 从session获取用户ID
        Integer userId = (Integer) request.getSession().getAttribute("userId");
        if (userId == null) {
            SimpleLogger.log("No user logged in - redirecting to login page");
            response.sendRedirect("/login");
            return;
        }

        // 处理加入库请求
        if ("POST".equals(method) && "addToLibrary".equals(request.getParameter("action"))) {
            handleAddToLibrary(request, response, userId);
            return;
        }

        // 获取报告包ID
        String bagIdStr = request.getParameter("bagId");
        if (bagIdStr == null || bagIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing bag ID");
            return;
        }

        int bagId = Integer.parseInt(bagIdStr);
        List<Document> documentList = documentService.getDocumentsInBag(bagId);
        List<Document> recommendedDocuments = getRecommendedDocuments(userId, documentList);

        Context context = new Context();
        context.setVariable("documentList", documentList);
        context.setVariable("recommendedDocuments", recommendedDocuments);
        context.setVariable("bagId", bagId);

        String renderedHtml = viewResolver.render("BagDocumentListView", context);
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");
        response.getWriter().write(renderedHtml);
    }

    private List<Document> getRecommendedDocuments(int userId, List<Document> bagDocuments) throws Exception {
        // 获取所有非用户的文档ID（排除标题重复的文档）
        List<Document> allDocuments = documentService.getAllDocumentsExceptUser(userId, bagDocuments);
        List<Integer> candidateDocIds = allDocuments.stream()
            .map(Document::getDocumentId)
            .collect(Collectors.toList());
        
        // 如果没有文档可推荐，返回空列表
        if (candidateDocIds.isEmpty() || bagDocuments.isEmpty()) {
            SimpleLogger.log("No documents available for recommendation");
            return new ArrayList<>();
        }

        // 获取源文档ID列表
        List<Integer> sourceDocIds = bagDocuments.stream()
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

    private void handleAddToLibrary(HttpServletRequest request, HttpServletResponse response, int userId) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        try {
            int documentId = Integer.parseInt(request.getParameter("documentId"));
            int bagId = Integer.parseInt(request.getParameter("bagId")); // 获取报告包ID
            
            // 获取原始文档
            Document sourceDoc = documentService.getDocumentById(documentId);
            if (sourceDoc == null) {
                sendJsonResponse(response, false, "文档不存在");
                return;
            }
            
            // 创建新文档对象
            Document newDoc = new Document();
            newDoc.setUserId(userId);
            newDoc.setTitle(sourceDoc.getTitle());
            newDoc.setKeywords(sourceDoc.getKeywords());
            newDoc.setSubject(sourceDoc.getSubject());
            newDoc.setContent(sourceDoc.getContent());
            newDoc.setPdfFile(sourceDoc.getPdfFile());
            
            // 插入新文档
            int newDocId = documentService.insertDocument(newDoc);
            
            if (newDocId > 0) {
                // 复制向量文件
                copyVectorFile(documentId, newDocId);
                
                // 将新文档添加到报告包中
                boolean addedToBag = documentService.addDocumentToBag(bagId, newDocId);
                
                if (addedToBag) {
                    sendJsonResponse(response, true, "成功加入库并添加到报告包");
                } else {
                    sendJsonResponse(response, false, "文档已加入库，但添加到报告包失败");
                }
            } else {
                sendJsonResponse(response, false, "添加文档失败");
            }
            
        } catch (Exception e) {
            SimpleLogger.log("Error adding document to library: " + e.getMessage());
            sendJsonResponse(response, false, "操作失败：" + e.getMessage());
        }
    }

    private void copyVectorFile(int sourceDocId, int newDocId) {
        try {
            File sourceFile = new File(VECTOR_DIR + sourceDocId + ".txt");
            File targetFile = new File(VECTOR_DIR + newDocId + ".txt");
            java.nio.file.Files.copy(sourceFile.toPath(), targetFile.toPath());
        } catch (IOException e) {
            SimpleLogger.log("Error copying vector file: " + e.getMessage());
        }
    }

    private void sendJsonResponse(HttpServletResponse response, boolean success, String message) throws IOException {
        String json = String.format("{\"success\": %b, \"message\": \"%s\"}", success, message);
        response.getWriter().write(json);
    }
}