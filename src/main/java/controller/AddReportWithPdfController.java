package controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

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
    // public static void main(String[] args) {
    //     String pdfFilePath = "D:\\郭如璇的文件\\200大二上\\民俗学\\000神话修改.pdf"; // 请替换为实际的文件路径
        
    //     // 创建文件对象
    //     File pdfFile = new File(pdfFilePath);
        
    //     // 确保文件存在
    //     if (!pdfFile.exists()) {
    //         System.out.println("文件不存在！");
    //         return;
    //     }
    //     InputStream inputStream = null;

    //     // 获取文件输入流
    //     try  {
    //         inputStream = new FileInputStream(pdfFile);
    //         // 现在可以使用inputStream对PDF文件进行处理
    //         System.out.println("PDF文件已成功打开！");

    //         // 读取文件内容（示例代码中这里不做具体处理）
    //         // 你可以根据需要读取文件内容或将其存储到数据库等
    //         byte[] buffer = new byte[1024];
    //         int bytesRead;
    //         Document document = new Document("null", "null", "null", "null", 1, inputStream);
    //         DocumentDaoImpl documentDaoImpl = new DocumentDaoImpl();
    //         documentDaoImpl.insert(document);
    //         while ((bytesRead = inputStream.read(buffer)) != -1) {
    //             // 这里只是示例，实际情况你可以进行其他操作，如存储到数据库
    //             System.out.println("读取到的字节数: " + bytesRead);
    //         }
    //     } catch (IOException e) {
    //         e.printStackTrace();
    //         System.out.println("读取PDF文件时发生错误！");
    //     }
    // }

    public static void processRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 设置响应类型为 JSON 格式
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        Part filePart = request.getPart("file");

        // 获取原始文件名
        String fileName = filePart.getSubmittedFileName();

        // 获取文件输入流
        InputStream pdfInputStream = filePart.getInputStream();

        /*
         * 调试
         */
        // File file = new File("C:\\Users\\a1372\\Desktop\\man.pdf");
        //     try (OutputStream outStream = new FileOutputStream(file)) {
        //         byte[] buffer = new byte[1024];
        //         int bytesRead;
        //         while ((bytesRead = pdfInputStream.read(buffer)) != -1) {
        //             outStream.write(buffer, 0, bytesRead);
        //         }
        //     }

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
        // // 未选择文件的情况
        // response.getWriter().println(
        // "{\"success\": false, \"message\": \"请选择要上传的文件\"}");
        // }
    }
}
