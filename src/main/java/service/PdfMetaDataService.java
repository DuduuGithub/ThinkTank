package service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.JsonArray;

import db.vo.Document;
import logger.SimpleLogger;

public class PdfMetaDataService {
    static {
        // 在类加载时设置日志级别
        java.util.logging.Logger.getLogger("org.apache.pdfbox.pdmodel.font.PDSimpleFont")
            .setLevel(java.util.logging.Level.SEVERE);
    }

    /*
     * 将pdf文件的元数据和内容构造一个Document对象
     * 参数：pdf的输入流，用户id
     * 返回：Document对象
     */
    @SuppressWarnings("rawtypes")
    public static Document getDocument(InputStream pdfInputStream, int userId) throws Exception {
        // 创建输入流的副本，这样可以多次读取
        byte[] pdfBytes = pdfInputStream.readAllBytes();

        // 使用一个副本来获取元数据
        ByteArrayInputStream pdfInputStream1 = new ByteArrayInputStream(pdfBytes);
        Map metaDataMap = PdfMetaDataService.getPdfMetaData(pdfInputStream1);

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
        String pdfContent = PdfTools.getPdfContent(pdfInputStream);
        
        // 修改提示词，使其更加明确和简洁
        String prompt = "请仅提取以下内容，并按指定格式返回JSON：\n" +
                       "1. 文章标题(title)\n" +
                       "2. 文章关键词(keywords)\n" +
                       "3. 根据全文内容生成主题词(subject)\n" +
                       "你的回答只能有包含如下key的，如下格式的JSON内容，不要包含其他任何内容：\n" +
                       "{\"title\":,\"keywords\":,\"subject\":}\n" +
                       "注意：所有字段必须是字符串，不能是数组\n" +
                       "提供给你的文章内容如下：" + pdfContent;

        String result = BigModelNew.askQuestion(prompt);
        
        // 检查大模型返回结果
        SimpleLogger.log("大模型返回结果: " + result);

        // 检查结果是否为空
        if (result == null || result.trim().isEmpty()) {
            throw new Exception("AI模型返回结果为空");
        }

        // 检查是否包含JSON
        if (!result.contains("{") || !result.contains("}")) {
            throw new Exception("AI模型返回的结果不包含有效的JSON格式");
        }

        // 截取result中的json字符串
        String jsonString = result.substring(result.indexOf('{'), result.lastIndexOf('}') + 1);
        SimpleLogger.log("pdfMetaDta json字符串: " + jsonString);

        try {
            // 使用 JsonParser 先解析 JSON
            JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();
            
            // 创建结果 Map
            Map<String, Object> map = new HashMap<>();
            
            // 处理每个字段，确保是字符串格式
            String title = getStringValue(jsonObject, "title");
            String keywords = getStringValue(jsonObject, "keywords");
            String subject = getStringValue(jsonObject, "subject");
            
            // 检查关键字段是否为空
            if (title.trim().isEmpty() || keywords.trim().isEmpty() || subject.trim().isEmpty()) {
                throw new Exception("AI模型返回的数据不完整，缺少必要字段");
            }
            
            // 添加到 map 中
            map.put("title", title);
            map.put("keywords", keywords);
            map.put("subject", subject);
            map.put("content", pdfContent);
            
            return map;
        } catch (Exception e) {
            SimpleLogger.log("Error parsing JSON response: " + e.getMessage());
            throw new Exception("解析AI返回的数据时出错: " + e.getMessage());
        }
    }

    // 辅助方法：从 JsonObject 中安全地获取字符串值
    private static String getStringValue(JsonObject jsonObject, String key) {
        if (jsonObject.has(key)) {
            if (jsonObject.get(key).isJsonArray()) {
                // 如果是数组，将数组元素用逗号连接
                JsonArray array = jsonObject.getAsJsonArray(key);
                List<String> items = new ArrayList<>();
                array.forEach(element -> items.add(element.getAsString()));
                return String.join("，", items);
            } else {
                // 如果是字符串，直接返回
                return jsonObject.get(key).getAsString();
            }
        }
        return ""; // 如果字段不存在，返回空字符串
    }
}
