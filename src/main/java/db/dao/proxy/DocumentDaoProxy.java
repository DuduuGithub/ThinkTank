package db.dao.proxy;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import db.dao.base.DocumentDao;
import db.dao.impl.DocumentDaoImpl;
import db.util.DBUtil;
import db.vo.Document;
import logger.SimpleLogger;

/*
 * 代理类，使用时直接使用代理类即可
 * 当使用增删改时，会返回一个void
 * 当使用查询时，会返回对应的数据
 */
public class DocumentDaoProxy implements DocumentDao {
    private DocumentDaoImpl dao;
    private Connection conn;

    /*
     * 构造函数，获取一个连接，并初始化dao
     */
    public DocumentDaoProxy() {
        try {
            conn = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dao = new DocumentDaoImpl();
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
        return dao.searchDocuments(userId, title, keywords, subject);
    }

    /*
     * 用途：插入新文档
     */
    @Override
    public int insert(Document document) {
        checkConnection();
        int rt=dao.insert(document);
        DBUtil.closeConnection(conn);
        return rt;
    }

    /*
     * 用途：更新文档数据
     */
    @Override
    public boolean update(Document document) {
        checkConnection();
        boolean flag = dao.update(document);
        DBUtil.closeConnection(conn);
        return flag;
    }

    /*
     * 用途：删除文档
     */
    @Override
    public void delete(String documentId) {
        checkConnection();
        dao.delete(documentId);
        DBUtil.closeConnection(conn);
    }

    /*
     * 用途：根据ID查询文档
     */
    @Override
    public Document findById(Integer documentId) {
        checkConnection();
        Document doc = dao.findById(documentId);
        DBUtil.closeConnection(conn);
        return doc;
    }

    /*
     * 用途：根据用户ID查询文档列表
     */
    @Override
    public List<Document> findByUserId(String userId) {
        checkConnection();
        List<Document> list = dao.findByUserId(userId);
        DBUtil.closeConnection(conn);
        return list;
    }

    /*
     * 用途：根据关键词搜索文档
     */
    @Override
    public List<Document> findByKeywords(String keywords) {
        checkConnection();
        List<Document> list = dao.findByKeywords(keywords);
        DBUtil.closeConnection(conn);
        return list;
    }

    /*
     * 用途：查询所有文档
     */
    @Override
    public List<Document> findAll() {
        checkConnection();
        List<Document> list = dao.findAll();
        DBUtil.closeConnection(conn);
        return list;
    }

    /*
     * 用途：批量插入文档
     */
    @Override
    public int batchInsert(List<Document> documents) {
        checkConnection();
        int result = dao.batchInsert(documents);
        DBUtil.closeConnection(conn);
        return result;
    }

    /*
     * 用途：批量导出文档
     */
    @Override
    public List<Document> batchExport(List<String> documentIds) {
        checkConnection();
        List<Document> list = dao.batchExport(documentIds);
        DBUtil.closeConnection(conn);
        return list;
    }

    /*
     * 用途：保存文档的PDF文件
     */
    @Override
    public void savePdfFile(String documentId, byte[] pdfData) {
        checkConnection();
        dao.savePdfFile(documentId, pdfData);
        DBUtil.closeConnection(conn);
    }

    /*
     * 用途：获取文档的PDF文件数据
     */
    @Override
    public byte[] getPdfFile(String documentId) {
        checkConnection();
        byte[] data = dao.getPdfFile(documentId);
        DBUtil.closeConnection(conn);
        return data;
    }

    /*
     * 用途：获取文档的PDF文件输入流
     */
    @Override
    public InputStream getPdfInputStream(Integer documentId) {
        checkConnection();
        InputStream stream = dao.getPdfInputStream(documentId);
        DBUtil.closeConnection(conn);
        return stream;
    }

    /*
     * 用途：保存PDF文件
     */
    @Override
    public void savePdf(Integer documentId, InputStream pdfInputStream) {
        checkConnection();
        dao.savePdf(documentId, pdfInputStream);
        DBUtil.closeConnection(conn);
    }

    /*
     * 检查连接是否为空，如果为空，则获取一个连接，所有方法都要检查
     */
    private void checkConnection() {
        if (conn == null) {
            try {
                conn = DBUtil.getConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
