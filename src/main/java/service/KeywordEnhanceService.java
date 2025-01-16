package service;

import db.vo.Document;
import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;
import java.util.stream.Collectors;

public class KeywordEnhanceService {
    private DocumentService documentService;
    private AiService aiService;

    public KeywordEnhanceService(DocumentService documentService) {
        this.documentService = documentService;
        this.aiService = new AiService();
    }

    /**
     * 增强用户输入的关键词
     * @param userInput 用户输入的关键词
     * @return 增强后的关键词
     */
    public String enhanceKeywords(String userInput) {
        if (userInput == null || userInput.trim().isEmpty()) {
            return "";
        }

        // 1. 获取所有已有文档的关键词
        List<Document> allDocuments = documentService.getAllDocuments();
        Set<String> existingKeywords = new HashSet<>();
        
        for (Document doc : allDocuments) {
            if (doc.getKeywords() != null && !doc.getKeywords().isEmpty()) {
                // 假设关键词是用逗号分隔的
                existingKeywords.addAll(Arrays.asList(doc.getKeywords().split(",")));
            }
        }

        // 2. 构建 AI 提示
        String prompt = String.format(
            "我有一个文档库，包含以下关键词：%s\n" +
            "用户输入的搜索词是：%s\n" +
            "请从已有的关键词中选择最相关的3-5个关键词，用逗号分隔。如果找不到相关的，就返回空字符串。\n" +
            "只返回关键词，不要其他任何解释。",
            String.join(",", existingKeywords),
            userInput
        );

        // 3. 调用 AI 获取建议的关键词
        String enhancedKeywords = aiService.getCompletion(prompt).trim();
        
        return enhancedKeywords;
    }
} 