package db.dao.impl;

import db.dao.base.DocumentDao;
import db.util.DBUtil;
import db.vo.Document;

import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * PaperDaoImpl类实现了PaperDao接口中定义的论文相关数据库操作。
 * 使用JDBC来实现论文的增删改查、按条件查询、批量操作以及PDF文件存取等功能。
 */

 public class DocumentDaoImpl implements DocumentDao {

    // 高级搜索文档：根据标题、关键词、主题和内容进行筛选
    public List<Document> searchDocuments(int userId, String title, String keywords, String subject) {
        StringBuilder sql = new StringBuilder("SELECT * FROM document WHERE user_id = ?");
        
        // 动态添加搜索条件
        List<Object> params = new ArrayList<>();
        params.add(userId);
        
        if (!title.isEmpty()) {
            sql.append(" AND title LIKE ?");
            params.add("%" + title + "%");
        }
        
        if (!keywords.isEmpty()) {
            sql.append(" AND keywords LIKE ?");
            params.add("%" + keywords + "%");
        }
        
        if (!subject.isEmpty()) {
            sql.append(" AND subject LIKE ?");
            params.add("%" + subject + "%");
        }
        
        // 执行查询
        Connection conn = null;
        List<Document> documents = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql.toString());
            
            // 为SQL语句设置参数
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }
            return documents;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询文档数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public void insert(Document document) {
        String sql = "INSERT INTO document (title, keywords, subject, content, user_id, pdf_File) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getKeywords());
            pstmt.setString(3, document.getSubject());
            pstmt.setString(4, document.getContent());
            pstmt.setInt(5, document.getUserId());
            pstmt.setBinaryStream(6, document.getPdfFile());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("插入论文数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public void update(Document document) {
        String sql = "UPDATE document SET title = ?, keywords = ?, subject = ?, content = ?, user_id = ? WHERE document_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, document.getTitle());
            pstmt.setString(2, document.getKeywords());
            pstmt.setString(3, document.getSubject());
            pstmt.setString(4, document.getContent());
            pstmt.setInt(5, document.getUserId());
            pstmt.setInt(6, document.getDocumentId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新论文数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public void delete(String documentId) {
        String sql = "DELETE FROM document WHERE document_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, documentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除论文数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public Document findById(Integer documentId) {
        String sql = "SELECT * FROM document WHERE document_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, documentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return extractDocumentFromResultSet(rs);
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询论文数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public List<Document> findByUserId(String userId) {
        String sql = "SELECT * FROM document WHERE user_id = ?";
        Connection conn = null;
        List<Document> documents = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }
            return documents;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户论文数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public List<Document> findByKeywords(String keywords) {
        String sql = "SELECT * FROM document WHERE keywords LIKE ?";
        Connection conn = null;
        List<Document> documents = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + keywords + "%");
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }
            return documents;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("根据关键词查询论文失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public List<Document> findAll() {
        String sql = "SELECT * FROM document";
        Connection conn = null;
        List<Document> documents = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }
            return documents;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询所有论文数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public void savePdfFile(String documentId, byte[] pdfData) {
        String sql = "UPDATE document SET pdf_file = ? WHERE document_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBytes(1, pdfData);
            pstmt.setString(2, documentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("保存PDF文件失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public byte[] getPdfFile(String documentId) {
        String sql = "SELECT pdf_file FROM document WHERE document_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, documentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBytes("pdf_file");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取PDF文件失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public int batchInsert(List<Document> documents) {
        String sql = "INSERT INTO document (document_id, title, keywords, subject, content, user_id) VALUES (?, ?, ?, ?, ?, ?)";
        Connection conn = null;
        int successCount = 0;
        
        try {
            conn = DBUtil.getConnection();
            // 开启事务（关闭自动提交）
            conn.setAutoCommit(false);
            
            PreparedStatement pstmt = conn.prepareStatement(sql);
            for (Document document : documents) {
                pstmt.setInt(1, document.getDocumentId());
                pstmt.setString(2, document.getTitle());
                pstmt.setString(3, document.getKeywords());
                pstmt.setString(4, document.getSubject());
                pstmt.setString(5, document.getContent());
                pstmt.setInt(6, document.getUserId());
                pstmt.addBatch();
                successCount++;
                
                // 每插入1000条执行一次批处理，以减少内存占用和提高效率
                if (successCount % 1000 == 0) {
                    pstmt.executeBatch();
                }
            }
            // 执行剩余批处理
            pstmt.executeBatch();
            // 提交事务
            conn.commit();
            
            return successCount;
        } catch (SQLException e) {
            // 出现异常回滚事务
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            throw new RuntimeException("批量插入论文数据失败", e);
        } finally {
            // 恢复自动提交并关闭连接
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public List<Document> batchExport(List<String> documentIds) {
        // 使用重复的问号构建一组占位符
        String placeholders = String.join(",", Collections.nCopies(documentIds.size(), "?"));
        String sql = "SELECT * FROM document WHERE document_id IN (" + placeholders + ")";
        
        Connection conn = null;
        List<Document> documents = new ArrayList<>();
        
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            // 为SQL语句设置参数
            for (int i = 0; i < documentIds.size(); i++) {
                pstmt.setString(i + 1, documentIds.get(i));
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                documents.add(extractDocumentFromResultSet(rs));
            }
            
            return documents;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("批量导出论文数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    /**
     * 从ResultSet中提取论文数据的私有辅助方法。
     * @param rs 查询结果集
     * @return 填充完成的Paper对象
     * @throws SQLException ��果读取ResultSet时出现问题将抛出该异常
     */
    private Document extractDocumentFromResultSet(ResultSet rs) throws SQLException {
        Document document = new Document();
        document.setDocumentId(rs.getInt("document_id"));
        document.setTitle(rs.getString("title"));
        document.setKeywords(rs.getString("keywords"));
        document.setSubject(rs.getString("subject"));
        document.setContent(rs.getString("content"));
        document.setUserId(rs.getInt("user_id"));
        document.setPdfFile(rs.getBinaryStream("pdf_file"));
        return document;
    }

    @Override
    public InputStream getPdfInputStream(Integer documentId) {
        String sql = "SELECT pdf_file FROM document WHERE document_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, documentId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getBinaryStream("pdf_file");
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("获取PDF文件失败", e);
        }
        // 注意：这里不能关闭连接，因为InputStream还需要使用
    }

    @Override
    public void savePdf(Integer documentId, InputStream pdfInputStream) {
        String sql = "UPDATE document SET pdf_file = ? WHERE document_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setBinaryStream(1, pdfInputStream);
            pstmt.setInt(2, documentId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("保存PDF文件失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }
}
