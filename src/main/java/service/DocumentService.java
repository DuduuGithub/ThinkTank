package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Map;

import db.dao.impl.DocumentDaoImpl;
import db.dao.proxy.Bag2documentDaoProxy;
import db.dao.proxy.DocumentDaoProxy;
import db.factory.DaoFactory;
import db.vo.Document;
import db.vo.Bag2document;
import logger.SimpleLogger;
import service.KeywordEnhanceService;

public class DocumentService {
    private DocumentDaoProxy documentDaoProxy;
    private KeywordEnhanceService keywordEnhanceService;

    public DocumentService() {
        this.documentDaoProxy = DaoFactory.getInstance().getDocumentDao();
        this.keywordEnhanceService = new KeywordEnhanceService(this);
    }

    public Document getDocumentById(Integer id) {
        return documentDaoProxy.findById(id);
    }

    // 普通搜索：直接根据条件匹配
    public List<Document> searchDocuments(int userId, String title, String keywords, String subject, int page, int pageSize) {
        return documentDaoProxy.searchDocuments(userId, title, keywords, subject, page, pageSize);
    }

    public List<Document> getDocumentsInBag(int bagId) {
        List<Document> documents = new ArrayList<>();

        // 先获取报告包中的所有文档ID
        Bag2documentDaoProxy bag2documentDao = new Bag2documentDaoProxy();
        List<Integer> documentIds = bag2documentDao.queryByBagId(bagId);

        // 然后获取每个文档的详细信息
        DocumentDaoImpl documentDaoImpl = new DocumentDaoImpl();
        for (Integer documentId : documentIds) {
            Document doc = documentDaoImpl.findById(documentId);
            if (doc != null) {
                documents.add(doc);
            }
        }
        return documents;
    }

    public List<Document> getAllDocumentsExceptUser(int userId, List<Document> sourceDocuments) {
        DocumentDaoProxy documentDao = new DocumentDaoProxy();
        List<Document> allDocuments = documentDao.findAll();

        // 获取源文档的标题列表
        Set<String> sourceTitles = sourceDocuments.stream()
                .map(Document::getTitle)
                .collect(Collectors.toSet());

        SimpleLogger.log("Source document titles: " + sourceTitles);

        // 过滤掉属于该用户的文档和标题重复的文档
        return allDocuments.stream()
                .filter(doc -> doc.getUserId() != userId)  // 排除用户自己的文档
                .filter(doc -> !sourceTitles.contains(doc.getTitle()))  // 排除标题重复的文档
                .collect(Collectors.toList());
    }

    /*
     * 用途：删除文档
     * 返回值：删除成功与否
     * 参数：documentId,userId
     */
    public static String deleteDocument(int documentId, int userId) {
        DocumentDaoProxy documentDaoProxy = DaoFactory.getInstance().getDocumentDao();
        Document document = documentDaoProxy.findById(documentId);
        if (document.getUserId() == userId) {
            documentDaoProxy.delete(String.valueOf(documentId));
            return "{\"success\": true, \"message\": \"删除成功\"}";
        }
        else{
            return "{\"success\": false, \"message\": \"该报告不属于您，无法删除\"}";
        }
    }

    public int insertDocument(Document document) {
        DocumentDaoProxy documentDao = DaoFactory.getInstance().getDocumentDao();
        return documentDao.insert(document);
    }

    public boolean addDocumentToBag(int bagId, int documentId) {
        try {
            Bag2documentDaoProxy bag2documentDao = new Bag2documentDaoProxy();
            Bag2document newBag2document = new Bag2document(bagId, documentId);
            return bag2documentDao.insert(newBag2document);
        } catch (Exception e) {
            SimpleLogger.log("Error adding document to bag: " + e.getMessage());
            return false;
        }
    }

    // 获取总文档数
    public int getTotalDocuments(int userId, String title, String keywords, String subject) {
        return documentDaoProxy.getTotalDocuments(userId, title, keywords, subject);
    }

    /**
     * 获取所有文档
     * @return 所有文档列表
     */
    public List<Document> getAllDocuments() {
        return documentDaoProxy.getAllDocuments();
    }

    // AI 增强搜索：使用 AI 增强所有搜索条件后再搜索
    public List<Document> searchDocumentsWithAI(int userId, String title, String keywords, String subject, int page, int pageSize) {
        SimpleLogger.log("开始执行AI增强搜索...");
        SimpleLogger.log("用户ID：" + userId);
        SimpleLogger.log("页码：" + page);
        SimpleLogger.log("每页大小：" + pageSize);
        SimpleLogger.log("原始搜索条件 - 标题：[" + title + "], 关键词：[" + keywords + "], 主题：[" + subject + "]");

        // 如果有任何搜索条件，使用 AI 增强
        if ((title != null && !title.trim().isEmpty()) || 
            (keywords != null && !keywords.trim().isEmpty()) || 
            (subject != null && !subject.trim().isEmpty())) {
            
            SimpleLogger.log("检测到有效的搜索条件，准备调用AI增强服务...");
            
            Map<String, String> enhancedTerms = keywordEnhanceService.enhanceSearchTerms(title, keywords, subject);
            SimpleLogger.log("AI增强服务返回结果：" + enhancedTerms);
            
            // 如果 AI 返回了增强的搜索条件，就使用它们
            String enhancedTitle = enhancedTerms.get("title");
            String enhancedKeywords = enhancedTerms.get("keywords");
            String enhancedSubject = enhancedTerms.get("subject");
            
            SimpleLogger.log("处理AI返回的增强结果：");
            // 只在 AI 返回结果时替换原始条件
            if (enhancedTitle != null && !enhancedTitle.isEmpty()) {
                SimpleLogger.log("使用增强后的标题替换原标题：" + enhancedTitle);
                title = enhancedTitle;
            } else {
                SimpleLogger.log("AI未返回有效的标题增强结果，保持原标题不变");
            }
            
            if (enhancedKeywords != null && !enhancedKeywords.isEmpty()) {
                SimpleLogger.log("使用增强后的关键词替换原关键词：" + enhancedKeywords);
                keywords = enhancedKeywords;
            } else {
                SimpleLogger.log("AI未返回有效的关键词增强结果，保持原关键词不变");
            }
            
            if (enhancedSubject != null && !enhancedSubject.isEmpty()) {
                SimpleLogger.log("使用增强后的主题替换原主题：" + enhancedSubject);
                subject = enhancedSubject;
            } else {
                SimpleLogger.log("AI未返回有效的主题增强结果，保持原主题不变");
            }
            
            SimpleLogger.log("最终使用的搜索条件 - 标题：[" + title + "], 关键词：[" + keywords + "], 主题：[" + subject + "]");
        } else {
            SimpleLogger.log("未检测到有效的搜索条件，将执行空条件搜索");
        }
        
        SimpleLogger.log("开始执行数据库搜索...");
        List<Document> results = documentDaoProxy.searchDocuments(userId, title, keywords, subject, page, pageSize);
        SimpleLogger.log("数据库搜索完成，返回结果数量：" + (results != null ? results.size() : 0));
        
        return results;
    }
}
