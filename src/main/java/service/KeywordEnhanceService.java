package service;

import db.vo.Document;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import logger.SimpleLogger;

public class KeywordEnhanceService {
    private DocumentService documentService;
    private AiService aiService;

    public KeywordEnhanceService(DocumentService documentService) {
        this.documentService = documentService;
        this.aiService = new AiService();
    }

    /**
     * 增强搜索条件
     */
    public Map<String, String> enhanceSearchTerms(String title, String keywords, String subject) {
        SimpleLogger.log("开始AI增强搜索 - 原始输入：");
        SimpleLogger.log("标题：" + title);
        SimpleLogger.log("关键词：" + keywords);
        SimpleLogger.log("主题：" + subject);

        // 获取所有已有文档
        List<Document> allDocuments = documentService.getAllDocuments();
        SimpleLogger.log("获取到文档总数：" + allDocuments.size());
        
        // 收集所有已有的标题、关键词和主题
        Set<String> existingTitles = new HashSet<>();
        Set<String> existingKeywords = new HashSet<>();
        Set<String> existingSubjects = new HashSet<>();
        
        for (Document doc : allDocuments) {
            if (doc.getTitle() != null) {
                existingTitles.add(doc.getTitle());
            }
            if (doc.getKeywords() != null) {
                existingKeywords.addAll(Arrays.asList(doc.getKeywords().split(",")));
            }
            if (doc.getSubject() != null) {
                existingSubjects.add(doc.getSubject());
            }
        }

        SimpleLogger.log("现有标题数量：" + existingTitles.size());
        SimpleLogger.log("现有关键词数量：" + existingKeywords.size());
        SimpleLogger.log("现有主题数量：" + existingSubjects.size());
        SimpleLogger.log("现有标题列表：" + String.join("，", existingTitles));
        SimpleLogger.log("现有关键词列表：" + String.join("，", existingKeywords));
        SimpleLogger.log("现有主题列表：" + String.join("，", existingSubjects));

        // 构建 AI 提示
        StringBuilder prompt = new StringBuilder("我有一个文档库，包含以下内容：\n");
        prompt.append("标题列表：").append(String.join("，", existingTitles)).append("\n");
        prompt.append("关键词列表：").append(String.join("，", existingKeywords)).append("\n");
        prompt.append("主题列表：").append(String.join("，", existingSubjects)).append("\n\n");
        
        prompt.append("用户搜索条件：\n");
        if (title != null && !title.isEmpty()) {
            prompt.append("标题：").append(title).append("\n");
        }
        if (keywords != null && !keywords.isEmpty()) {
            prompt.append("关键词：").append(keywords).append("\n");
        }
        if (subject != null && !subject.isEmpty()) {
            prompt.append("主题：").append(subject).append("\n");
        }
        
        prompt.append("\n请根据用户的搜索条件，从已有的内容中选择最相关的一个：\n");
        prompt.append("1. 如果用户输入了标题，从已有标题中选择1个最相关的\n");
        prompt.append("2. 如果用户输入了关键词，从已有关键词中选择1个最相关的\n");
        prompt.append("3. 如果用户输入了主题，从已有主题中选择1个最相关的\n");
        prompt.append("请按以下格式返回（每个字段用竖线|分隔，如果某个字段没有相关结果则返回空）：\n");
        prompt.append("标题结果|关键词结果|主题结果");

        SimpleLogger.log("发送给AI的提示：" + prompt.toString());

        // 调用 AI 获取建议
        String aiResponse = aiService.getCompletion(prompt.toString());
        SimpleLogger.log("AI返回的原始响应：" + aiResponse);

        String[] parts = aiResponse.split("\\|");
        SimpleLogger.log("AI响应解析后的部分数量：" + parts.length);
        
        // 解析结果
        Map<String, String> enhancedTerms = new HashMap<>();
        enhancedTerms.put("title", parts.length > 0 ? parts[0].trim() : "");
        enhancedTerms.put("keywords", parts.length > 1 ? parts[1].trim() : "");
        enhancedTerms.put("subject", parts.length > 2 ? parts[2].trim() : "");
        
        SimpleLogger.log("AI增强后的结果：");
        SimpleLogger.log("增强后的标题：" + enhancedTerms.get("title"));
        SimpleLogger.log("增强后的关键词：" + enhancedTerms.get("keywords"));
        SimpleLogger.log("增强后的主题：" + enhancedTerms.get("subject"));
        
        return enhancedTerms;
    }
} 