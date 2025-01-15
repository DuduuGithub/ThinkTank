package controller;

import java.io.IOException;
import java.util.List;
import java.util.ArrayList;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.google.gson.Gson;

import db.vo.Document;
import service.DocumentService;

public class GetDocumentsController {
    public static void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException{
        HttpSession session = request.getSession();
        Integer userId = (Integer) session.getAttribute("userId");
        
        DocumentService documentService = new DocumentService();
        List<Document> documents = documentService.getDocumentsInBag(userId);

        // 转换为简化的DTO列表
        List<DocumentDTO> simplifiedDocs = new ArrayList<>();
        for (Document doc : documents) {
            DocumentDTO dto = new DocumentDTO(doc.getDocumentId(), doc.getTitle());
            simplifiedDocs.add(dto);
        }

        // 转换为JSON并返回
        Gson gson = new Gson();
        String json = gson.toJson(simplifiedDocs);
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(json);
    }
}

// 创建一个内部类用于数据传输
class DocumentDTO {
    @SuppressWarnings("unused")
    private Integer documentId;
    @SuppressWarnings("unused")
    private String title;

    public DocumentDTO(Integer documentId, String title) {
        this.documentId = documentId;
        this.title = title;
    }
}