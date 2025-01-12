package service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import db.dao.impl.DocumentDaoImpl;
import db.dao.proxy.Bag2documentDaoProxy;
import db.dao.proxy.DocumentDaoProxy;
import db.factory.DaoFactory;
import db.vo.Document;

public class DocumentService {
    private DocumentDaoProxy documentDaoProxy;

    public DocumentService() {
        this.documentDaoProxy = DaoFactory.getInstance().getDocumentDao();
    }

    public Document getDocumentById(Integer id) {
        return documentDaoProxy.findById(id);
    }

    // 高级搜索：根据用户ID和搜索条件返回匹配的文档列表
    public List<Document> searchDocuments(int userId, String title, String keywords, String subject) {
        return documentDaoProxy.searchDocuments(userId, title, keywords, subject);
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

    public List<Document> getAllDocumentsExceptUser(int userId) {
        DocumentDaoProxy documentDao = new DocumentDaoProxy();
        List<Document> allDocuments = documentDao.findAll();
        
        // 过滤掉属于该用户的文档
        return allDocuments.stream()
                .filter(doc -> doc.getUserId() != userId)
                .collect(Collectors.toList());
    }
}
