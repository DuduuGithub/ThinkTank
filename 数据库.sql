USE java_db;

-- 用户表
CREATE TABLE user (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    password VARCHAR(100) NOT NULL    
);

-- 文档表
CREATE TABLE document (
    document_id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(200) NOT NULL,
    keywords VARCHAR(200),
    subject VARCHAR(100),
    content TEXT,
    user_id INT,                      
    pdf_file LONGBLOB,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
        ON DELETE SET NULL            
        ON UPDATE CASCADE             
);