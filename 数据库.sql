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
    content LONGTEXT,
    user_id INT,                      
    pdf_file LONGBLOB,
    FOREIGN KEY (user_id) REFERENCES user(user_id)
        ON DELETE SET NULL            
        ON UPDATE CASCADE             
);

-- 报告包表
create table bag (
    bag_id int auto_increment primary key,
    bag_name varchar(100),
    user_id int,
    foreign key (user_id) references user(user_id)
        on delete set null
        on update cascade
);

-- 报告包内容表
create table bag2document (
    bag_id int,
    document_id int,
    primary key(bag_id,document_id),
    foreign key (bag_id) references bag(bag_id)
        on delete cascade           -- 改为 cascade
        on update cascade,
    foreign key(document_id) references document(document_id)
        on delete cascade           -- 改为 cascade
        on update cascade
);