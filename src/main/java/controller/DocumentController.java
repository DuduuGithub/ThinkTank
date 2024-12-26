package controller;

import service.DocumentService;
import db.vo.Document;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class DocumentController {

    private DocumentService documentService;

    public DocumentController() {
        // 初始化服务层
        documentService = new DocumentService();
    }

    // 根据文档编号定位到文档详情页
    public void getDocumentDetails(HttpServletRequest request, HttpServletResponse response) throws IOException {
        
        String documentIdParam = request.getParameter("documentId");

        if (documentIdParam == null || documentIdParam.isEmpty()) {
            // 参数缺失，返回错误信息
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Missing or invalid documentId");
            return;
        }

        try {
            int documentId = Integer.parseInt(documentIdParam);

            // 2. 调用业务层获取文档详情
            Document document = documentService.getDocumentById(Integer.valueOf(documentId));

            if (document == null) {
                // 文档不存在，返回错误信息
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Document not found");
                return;
            }

            // 3. 将文档详情存入 request 属性
            request.setAttribute("document", document);
            // 4. 转发到文档详情页
            request.getRequestDispatcher("/WEB-INF/views/documentDetails.jsp").forward(request, response);

        } catch (java.lang.NumberFormatException e) {
            // 如果文档编号格式错误，返回错误信息
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid documentId format");
        } catch (Exception e) {
            // 捕获其他异常，返回服务器错误信息
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Server error: " + e.getMessage());
        }
    }
}
