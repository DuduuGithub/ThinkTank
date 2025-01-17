package controller;

import java.io.InputStream;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import db.dao.proxy.DocumentDaoProxy;
import db.factory.DaoFactory;
import db.vo.Document;
import db.vo.User;
import service.PdfMetaDataService;
import service.ClsTokenGenerater;
import logger.SimpleLogger;

/*
 * 用途：用户通过表单传入一个pdf文件，通过调用大模型获得这个pdf的各个字段，并把这个报告存入数据库
 * 参数：request里面需要有pdf文件
 */
public class AddReportWithPdfController {
    public static void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            Part filePart = request.getPart("file");
            String fileName = filePart.getSubmittedFileName();
            int userId = (int) request.getSession().getAttribute("userId");

            try (InputStream pdfInputStream = filePart.getInputStream()) {
                // 调用大模型以获取元数据并构造一个Document对象
                Document document = PdfMetaDataService.getDocument(pdfInputStream, userId);
                
                // 在处理完一个请求后，强制进行垃圾回收
                System.gc();
                
                // 插入文档到数据库
                DocumentDaoProxy documentDaoProxy = DaoFactory.getInstance().getDocumentDao();
                int document_id = documentDaoProxy.insert(document);

                if (document_id == 0) {
                    throw new RuntimeException("文档插入数据库失败");
                }

                // 生成向量化文件
                String inputText = document.getContent();
                String vectorFileName = document_id + ".txt";
                
                SimpleLogger.log("Generating vector for document ID: " + document_id);
                
                // 创建ClsTokenGenerater实例并生成向量
                ClsTokenGenerater tokenGenerater = new ClsTokenGenerater(inputText, vectorFileName);
                tokenGenerater.generateClsToken();

                // 如果 userId 为 1 ，则是系统默认库，则需要再把该报告插入到所有其他用户名下
                if(userId==1){
                    DocumentListViewController documentListViewController = new DocumentListViewController();
                    List<User> allUsers = DaoFactory.getInstance().getUserDao().findAll();
                    for(User currentUser:allUsers){
                        if(currentUser.getUserId()!=1){
                            // 查找刚才插入的新报告
                            Document defaultDocument = DaoFactory.getInstance().getDocumentDao().findById(document_id);
                            defaultDocument.setUserId(currentUser.getUserId());
                            int newDocId = DaoFactory.getInstance().getDocumentDao().insert(defaultDocument);
                            documentListViewController.copyVectorFile(document_id, newDocId);
                        }
                    }
                }

                // 返回成功响应
                response.getWriter().write(String.format(
                    "{\"success\": true, \"message\": \"文件上传成功，并已生成向量化文件\", \"fileName\": \"%s\"}",
                    fileName));

            }
        } catch (Exception e) {
            SimpleLogger.log("Error processing PDF: " + e.getMessage());
            // 确保错误消息是有效的JSON字符串
            String errorMessage = e.getMessage().replace("\"", "'").replace("\n", " ");
            response.getWriter().write(String.format(
                "{\"success\": false, \"message\": \"添加失败: %s\"}",
                errorMessage));
            e.printStackTrace();
        }
    }
}
