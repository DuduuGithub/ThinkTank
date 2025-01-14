package controller;

import java.io.InputStream;
import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import db.dao.proxy.DocumentDaoProxy;
import db.factory.DaoFactory;
import db.vo.Document;
import service.PdfMetaDataService;
import service.ClsTokenGenerater;
import logger.SimpleLogger;

/*
 * 用途：用户通过表单传入一个pdf文件，通过调用大模型获得这个pdf的各个字段，并把这个报告存入数据库
 * 参数：request里面需要有pdf文件
 */
public class AddReportWithPdfController {
    public static void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置响应类型为 JSON 格式
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Part filePart = request.getPart("file");

        // 获取原始文件名
        String fileName = filePart.getSubmittedFileName();

        // 获取文件输入流
        InputStream pdfInputStream = null;

        // 获取用户id
        int userId = (int) request.getSession().getAttribute("userId");

        try {
            pdfInputStream = filePart.getInputStream();
            
            // 使用 try-with-resources 确保资源正确关闭
            try (InputStream finalInputStream = pdfInputStream) {
                // 调用大模型以获取元数据并构造一个Document对象
                Document document = PdfMetaDataService.getDocument(finalInputStream, userId);
                
                // 在处理完一个请求后，强制进行垃圾回收
                System.gc();
                
                // 插入文档到数据库
                DocumentDaoProxy documentDaoProxy = DaoFactory.getInstance().getDocumentDao();
                int document_id=documentDaoProxy.insert(document);

                
                // 确保获取到了documentId
                if (document_id == 0) {
                    throw new RuntimeException("Failed to get document ID after insertion");
                }

                // 生成向量化文件
                String inputText = document.getContent();
                String vectorFileName = document_id + ".txt";
                
                SimpleLogger.log("Generating vector for document ID: " + document_id);
                
                // 创建ClsTokenGenerater实例并生成向量
                ClsTokenGenerater tokenGenerater = new ClsTokenGenerater(inputText, vectorFileName);
                tokenGenerater.generateClsToken();

                // 返回成功响应
                String jsonResponse = String.format(
                        "{\"success\": true, \"message\": \"文件上传成功，并已生成向量化文件\", \"fileName\": \"%s\"}",
                        fileName);
                response.getWriter().println(jsonResponse);
            }

        } catch (Exception e) {
            SimpleLogger.log("Error processing PDF: " + e.getMessage());
            String jsonResponse = String.format(
                    "{\"success\": false, \"message\": \"处理文件时出错: %s\"}",
                    e.getMessage());
            response.getWriter().println(jsonResponse);
            e.printStackTrace();
        } finally {
            // 确保关闭输入流
            if (pdfInputStream != null) {
                try {
                    pdfInputStream.close();
                } catch (IOException e) {
                    SimpleLogger.log("Error closing input stream: " + e.getMessage());
                }
            }
            
            // 强制关闭之前的连接
            try {
                BigModelNew.closeAllConnections();  // 需要在 BigModelNew 类中添加这个方法
            } catch (Exception e) {
                SimpleLogger.log("Error closing AI model connections: " + e.getMessage());
            }
        }
    }
}
