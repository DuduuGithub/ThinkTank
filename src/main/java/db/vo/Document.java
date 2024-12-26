package db.vo;

import java.io.InputStream;

/**
 * Document类表示论文实体，对应数据库中的document表。
 * 包含论文ID、标题、关键词、主题、内容、所属用户ID，以及PDF文件数据等信息。
 */
public class Document {
    private Integer documentId;
    private String title;
    private String keywords;
    private String subject;
    private String content;
    private Integer userId;
    private InputStream pdfFile;

    // 无参构造函数
    public Document() {}

    // 含参构造函数，用于快速构建document对象
    public Document(Integer documentId, String title, String keywords, String subject, String content, Integer userId) {
        this.documentId = documentId;
        this.title = title;
        this.keywords = keywords;
        this.subject = subject;
        this.content = content;
        this.userId = userId;
    }

    /*
     * 用途：用于根据除documentId之外的字段构造document对象
     */
    public Document(String title, String keywords, String subject, String content, Integer userId, InputStream pdfFile){
        this.title = title;
        this.keywords = keywords;
        this.subject = subject;
        this.content = content;
        this.userId = userId;
        this.pdfFile = pdfFile;
    }

    // Getter和Setter方法

    public Integer getDocumentId() {
        return documentId;
    }

    public void setDocumentId(Integer documentId) {
        this.documentId = documentId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public InputStream getPdfFile() {
        return pdfFile;
    }

    public void setPdfFile(InputStream pdfFile) {
        this.pdfFile = pdfFile;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }
}
