package controller;

import db.vo.Document;
import service.DocumentService;
import view.ThymeleafViewResolver;
import org.thymeleaf.context.Context;
import logger.SimpleLogger; // 引入自定义的 SimpleLogger

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public class DocumentListViewController {

    private DocumentService documentService;
    private ThymeleafViewResolver viewResolver;

    public DocumentListViewController(DocumentService documentService,ServletContext servletContext) {
        this.documentService = documentService;

        this.viewResolver = new ThymeleafViewResolver(servletContext);  // 使用你自己的 Thymeleaf 视图解析器
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
        Context context = new Context();

        if ("GET".equals(method)) {
            // GET 请求：直接获取该用户的所有文档
            documentList = documentService.searchDocuments(userId,"","","");
            SimpleLogger.log(String.format("GET request: Found %d document(s) for user %d", 
                documentList.size(), userId));
        } else {
            // POST 请求：根据搜索条件筛选文档
            String title = request.getParameter("title");
            String keywords = request.getParameter("keywords");
            String subject = request.getParameter("subject");
            // 获取请求的字符编码
            String encoding = request.getCharacterEncoding();
            SimpleLogger.log("Request Encoding: " + encoding);

            // 如果参数为null或者为空字符串，给它们设置一个默认值
            if (title == null || title.trim().isEmpty()) {
                title = ""; // 或者设置默认值，例如 "默认标题"
            }
            if (keywords == null || keywords.trim().isEmpty()) {
                keywords = ""; // 或者设置默认值，例如 "默认关键字"
            }
            if (subject == null || subject.trim().isEmpty()) {
                subject = ""; // 或者设置默认值，例如 "默认主题"
            }

            SimpleLogger.log(String.format("POST request: search title: %s ,keywords:%s,subject: %s,  for user %d", 
            title,keywords,subject, userId));

            documentList = documentService.searchDocuments(userId, title, keywords, subject);
            
            // 记录搜索结果
            if (documentList != null && !documentList.isEmpty()) {
                SimpleLogger.log(String.format("POST request: Found %d document(s) for user %d with filters: title='%s', keywords='%s', subject='%s'",
                        documentList.size(), userId, title, keywords, subject));
            } else {
                SimpleLogger.log(String.format("POST request: No documents found for user %d with filters: title='%s', keywords='%s', subject='%s'",
                        userId, title, keywords, subject));
            }

            // 只在 POST 请求时设置过滤条件
            context.setVariable("titleFilter", title);
            context.setVariable("keywordsFilter", keywords);
            context.setVariable("subjectFilter", subject);
        }

        // 共同的响应处理逻辑
        context.setVariable("documentList", documentList);
        String renderedHtml = viewResolver.render("DocumentListView", context);
        response.setCharacterEncoding("utf-8");
        response.setContentType("text/html");
        response.getWriter().write(renderedHtml);
    }
}