package db.dao.proxy;

import db.dao.impl.DocumentDaoImpl;
import db.vo.Document;
import logger.SimpleLogger;
import java.util.List;

public class DocumentDaoProxy {

    private DocumentDaoImpl documentDaoImpl;

    // 通过构造方法注入具体实现
    public DocumentDaoProxy(DocumentDaoImpl documentDaoImpl) {
        this.documentDaoImpl = documentDaoImpl;
    }

    // 代理搜索文档的逻辑
    public List<Document> searchDocuments(int userId, String title, String keywords, String subject) {
        // 过滤查询条件为空的情况
        if (title == null) title = "";
        if (keywords == null) keywords = "";
        if (subject == null) subject = "";

        // 记录搜索日志
        SimpleLogger.log("用户" + userId + "搜索了标题包含" + title + "、关键词包含" + keywords + "、主题包含" + subject + "的文档");
        // 调用DocumentDaoImpl的查询方法
        return documentDaoImpl.searchDocuments(userId, title, keywords, subject);
    }

    public Document findById(Integer documentId) {
        return documentDaoImpl.findById(documentId);
    }
}
