package service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

    // 高级搜索：根据用户ID和搜索条件返回匹配的文档列表
    public List<Document> searchDocuments(int userId, String title, String keywords, String subject, int page, int pageSize) {
        // 如果提供了关键词，使用 AI 增强关键词
        if (keywords != null && !keywords.trim().isEmpty()) {
            String enhancedKeywords = keywordEnhanceService.enhanceKeywords(keywords);
            // 如果 AI 返回了增强的关键词，就使用增强的关键词
            if (!enhancedKeywords.isEmpty()) {
                SimpleLogger.log("Original keywords: " + keywords);
                SimpleLogger.log("Enhanced keywords: " + enhancedKeywords);
                keywords = enhancedKeywords;
            }
        }
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
}
