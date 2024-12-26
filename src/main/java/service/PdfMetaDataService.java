package service;

import java.io.InputStream;
import java.util.Map;

import com.google.gson.Gson;

import db.vo.Document;

public class PdfMetaDataService {
    @SuppressWarnings("rawtypes")
    public static Document getDocument(InputStream pdfInputStream, int userId) throws Exception {
        // 获取pdf的各个字段
        Map metaDataMap = PdfMetaDataService.getPdfMetaData(pdfInputStream);

        String title = (String) metaDataMap.get("title");
        String keywords = (String) metaDataMap.get("keywords");
        String subject = (String) metaDataMap.get("subject");
        String content = (String) metaDataMap.get("content");

        // 把数据打包入一个Document对象并返回
        Document document = new Document(title, keywords, subject, content, userId, pdfInputStream);

        return document;
    }

    /*
     * 用途：获得pdf输入流的元数据
     * 参数：pdf的输入流
     * 返回：
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map getPdfMetaData(InputStream pdfInputStream) throws Exception {
        String pdfContent = PdfTools.getPdfContent(pdfInputStream);

        // 调用大模型获得结果
        String result = BigModelNew.askQuestion(pdfContent + "。"
                + "请你给出以上文章的标题（title），关键词（文章自带的关键词,keywords），主题词（根据文章内容生成的主题词，subject），返回的形式为一个形如{\"title\":\"value1\", \"keywords\":\"value2\", \"subject\":\"value3\"}的json字符串");

        // 截取result中的json字符串
        String jsonString = result = result.substring(result.indexOf('{'), result.lastIndexOf('}') + 1);

        // 创建Gson对象，用于把json字符串转换为Map
        Gson gson = new Gson();

        // 将json字符串转换为Map
        Map<String, Object> map = gson.fromJson(jsonString, Map.class);

        map.put("content", pdfContent);
        map.put("pdfFile", pdfInputStream);

        System.out.println(map);

        return map;
    }
}
