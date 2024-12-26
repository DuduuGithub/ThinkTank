package db.dao.base;

import db.vo.Document;
import java.io.InputStream;
import java.util.List;

/**
 * DocumentDao接口定义了论文数据访问层的操作方法。
 * 包括基本的CRUD、根据用户ID和关键词查询，以及批量插入、批量导出论文数据。
 */
public interface DocumentDao {

    /**
     * 插入一篇新论文
     * @param paper 论文对象
     */
    void insert(Document document);

    /**
     * 更新一篇论文的数据
     * @param paper 论文对象
     */
    void update(Document document);

    /**
     * 根据论文ID删除一篇论文
     * @param paperId 论文ID
     */
    void delete(String documentId);

    /**
     * 根据ID查询文档
     * @param documentId 文档ID
     * @return 文档对象
     */
    Document findById(Integer documentId);

    /**
     * 根据用户ID查找该用户下的所有论文
     * @param userId 用户ID
     * @return 论文列表
     */
    List<Document> findByUserId(String userId);

    /**
     * 根据关键词查询论文
     * @param keywords 关键词
     * @return 匹配关键词的论文列表
     */
    List<Document> findByKeywords(String keywords);

    /**
     * 查询所有论文数据
     * @return 所有论文列表
     */
    List<Document> findAll();

    /**
     * 批量插入多篇论文
     * @param documents 论文列表
     * @return 成功插入的论文数目
     */
    int batchInsert(List<Document> documents);

    /**
     * 批量导出指定ID列表的论文
     * @param documentIds 论文ID列表
     * @return 对应ID的论文列表
     */
    List<Document> batchExport(List<String> documentIds);

    /**
     * 保存论文对应的PDF文件二进制数据到数据库中
     * @param documentId 论文ID
     * @param pdfData PDF文件的二进制数据
     */
    void savePdfFile(String documentId, byte[] pdfData);

    /**
     * 根据论文ID获取存储在数据库中的PDF文件数据
     * @param documentId 论文ID
     * @return PDF文件的二进制数据，如果没有则返回null
     */
    byte[] getPdfFile(String documentId);

    /**
     * 获取文档的PDF文件流
     * @param documentId 文档ID
     * @return PDF文件的输入流
     */
    InputStream getPdfInputStream(Integer documentId);

    /**
     * 保存PDF文件
     * @param documentId 文档ID
     * @param pdfInputStream PDF文件的输入流
     */
    void savePdf(Integer documentId, InputStream pdfInputStream);
}

