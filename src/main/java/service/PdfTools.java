package service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.File;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import db.dao.impl.DocumentDaoImpl;

public class PdfTools {
    /*
     * 用途：获取目标pdf的输入流
     * 参数：报告的id
     * 返回值：一个输入流
     */
    public static InputStream getPdfInputStream(int fileId){
        DocumentDaoImpl documentDaoImpl = new DocumentDaoImpl();
        InputStream pdfInputStream = documentDaoImpl.getPdfInputStream(fileId);
        return pdfInputStream;
    }

    /*
     * 暂时无用
     */
    public static String encodePdfToBase64(InputStream pdfInputStream) throws IOException {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = pdfInputStream.read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, bytesRead);
        }
        return java.util.Base64.getEncoder().encodeToString(byteArrayOutputStream.toByteArray());
    }
    

    
    /*
     * 用途：当向数据库插入一个报告时，用来提取文字内容并存到数据库中
     * 参数：pdf文件的输入流
     * 返回值：一个包含pdf所有文字的字符串
     */
    public static String getPdfContent(InputStream inputStream){
        try {
            PDDocument document = PDDocument.load(inputStream);// 加载 PDF 文档
            
            PDFTextStripper pdfStripper = new PDFTextStripper();// 创建 PDFTextStripper 对象来提取文本
            String text = pdfStripper.getText(document);// 提取文本
            
            // 关闭文档
            document.close();
            return text;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    /*
     * 用途：读取一个pdf的所有文字内容，用处不大
     * 参数：一个表示pdf路径的字符串
     * 返回值：一个包含pdf所有文字的字符串
     */
    public static String getPdfContent(String pdfFilePath){
        try {
            File file = new File(pdfFilePath);
            PDDocument document = PDDocument.load(file);
            
            // 使用PDFTextStripper提取文本
            PDFTextStripper stripper = new PDFTextStripper();
            String text = stripper.getText(document);
            
            // System.out.println(text);
            
            document.close();
            return text;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}