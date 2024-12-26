package controller;

import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;

import db.dao.impl.DocumentDaoImpl;
import db.vo.Document;
import service.PdfMetaDataService;

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
        //if (filePart != null) {

            // 获取原始文件名
            String fileName = filePart.getSubmittedFileName();

            // 获取文件输入流
            InputStream pdfInputStream = filePart.getInputStream();

            // 返回响应
            // 构造JSON响应
            String jsonResponse = String.format(
                    "{\"success\": true, \"message\": \"文件上传成功\", \"fileName\": \"%s\"}",
                    fileName);
            response.getWriter().println(jsonResponse);

            // 获取用户id
            int userId = (int) request.getSession().getAttribute("userId");

            // 调用大模型以获取元数据并构造一个Document对象
            Document document = PdfMetaDataService.getDocument(pdfInputStream, userId);

            DocumentDaoImpl documentDaoImpl = new DocumentDaoImpl();
            documentDaoImpl.insert(document);

        // } else {
        //     // 未选择文件的情况
        //     response.getWriter().println(
        //             "{\"success\": false, \"message\": \"请选择要上传的文件\"}");
        // }
    }
}
