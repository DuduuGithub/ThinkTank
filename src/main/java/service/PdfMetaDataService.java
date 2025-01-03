package service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import com.google.gson.Gson;

import db.vo.Document;
import logger.SimpleLogger;

public class PdfMetaDataService {
    /*
     * 将
     */
    @SuppressWarnings("rawtypes")
    public static Document getDocument(InputStream pdfInputStream, int userId) throws Exception {

        // 创建输入流的副本，这样可以多次读取
        byte[] pdfBytes = pdfInputStream.readAllBytes();

        // 使用一个副本来获取元数据
        ByteArrayInputStream pdfInputStream1 = new ByteArrayInputStream(pdfBytes);
        Map metaDataMap = PdfMetaDataService.getPdfMetaData(pdfInputStream1);

        // 检查元数据是否获取成功
        SimpleLogger.log("获取到的元数据: " + metaDataMap);

        String title = (String) metaDataMap.get("title");
        String keywords = (String) metaDataMap.get("keywords");
        String subject = (String) metaDataMap.get("subject");
        String content = (String) metaDataMap.get("content");

        // 使用另一个副本来创建Document对象
        ByteArrayInputStream pdfInputStream2 = new ByteArrayInputStream(pdfBytes);
        Document document = new Document(title, keywords, subject, content, userId, pdfInputStream2);

        return document;
    }

    /*
     * 用途：获得pdf输入流的元数据
     * 参数：pdf的输入流
     * 返回：
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map getPdfMetaData(InputStream pdfInputStream) throws Exception {
        // 添加调试信息
        System.out.println("开始读取PDF内容");
        
        String pdfContent = PdfTools.getPdfContent(pdfInputStream);
        
        // 检查PDF内容是否成功提取
        System.out.println("PDF内容长度: " + (pdfContent != null ? pdfContent.length() : 0));
        
        // 调用大模型获得结果
        String result = BigModelNew.askQuestion(pdfContent + "。"
                + "请你给出以上文章的标题（title），关键词（文章自带的关键词,keywords），主题词（根据文章内容生成的主题词，subject），返回的形式为一个形如{\"title\":\"value1\", \"keywords\":\"value2\", \"subject\":\"value3\"}的json字符串，其中，value2和values3要求是不要用列表形式的，用中文逗号“，”分割");

        // 检查大模型返回结果
        System.out.println("大模型返回结果: " + result);

        // 截取result中的json字符串
        String jsonString = result = result.substring(result.indexOf('{'), result.lastIndexOf('}') + 1);

        // 创建Gson对象，用于把json字符串转换为Map
        Gson gson = new Gson();

        // 将json字符串转换为Map
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);

        map.put("content", pdfContent);

        return map;
    }
}
