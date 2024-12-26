package controller;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest; 
import javax.servlet.http.HttpServletResponse;

import logger.SimpleLogger;
import service.PdfTools;

public class PdfViewerController {
    public static void main(String[] args) throws IOException {
        InputStream pdf = PdfTools.getPdfInputStream(1);
        String content = PdfTools.getPdfContent(pdf);
        System.out.println(content);
        pdf.close();
    }





    public static void pdfViewer(HttpServletRequest request,HttpServletResponse response) throws IOException{
        // 获取请求的文件的id
        String fileIdParam = request.getParameter("id");
        SimpleLogger.log("请求id为" + fileIdParam + "的pdf文件");
        if (fileIdParam == null || fileIdParam.isEmpty()) {
            // 参数缺失，返回错误信息
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "请求中缺少fileId参数");
            return;
        }
        int fileId = 0;
        try{
            fileId = Integer.parseInt(fileIdParam);
        }catch(NumberFormatException e){
            // 参数格式错误，返回错误信息
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "fileId参数无效");
            return;
        }

        InputStream pdfInputStream = PdfTools.getPdfInputStream(fileId);
        if (pdfInputStream != null) {
            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "inline; filename=pdf_file.pdf");

            // 将输入流写入响应输出流
            try (OutputStream outputStream = response.getOutputStream()) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = pdfInputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
        } else {
            response.getWriter().println("PDF 文件未找到！");
        }
    }
}
