package db.vo;

/**
 * User类表示用户实体，对应数据库中的用户表（假设表名为user）。
 * 包含用户ID和密码等。
 */
public class User {
    private Integer userId;
    private String password;

    public User() {}

    // 含参构造函数，方便创建User对象时直接赋值
    public User(Integer userId, String password) {
        this.userId = userId;
        this.password = password;
    }

    // Getter和Setter方法，用于读取和修改用户ID和密码

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
