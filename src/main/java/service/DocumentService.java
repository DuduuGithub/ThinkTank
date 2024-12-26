package service;

import java.util.List;
import db.dao.proxy.DocumentDaoProxy;
import db.factory.DaoFactory;
import db.vo.Document;

public class DocumentService {
    private DocumentDaoProxy documentDaoProxy;

    public DocumentService() {
        this.documentDaoProxy = DaoFactory.createDocumentDaoProxy();
    }

    public Document getDocumentById(Integer id) {
        return documentDaoProxy.findById(id);
    }

    // 高级搜索：根据用户ID和搜索条件返回匹配的文档列表
    public List<Document> searchDocuments(int userId, String title, String keywords, String subject) {
        return documentDaoProxy.searchDocuments(userId, title, keywords, subject);
    }
}
