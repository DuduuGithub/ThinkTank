package db.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * DBUtil类用于提供数据库连接和关闭连接的便捷方法。
 */

public class DBUtil {
    // 数据库连接
    private static final String URL = "jdbc:mysql://localhost:3306/java_db?useSSL=false&serverTimezone=UTC";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "123456";

    static {
        try {
            // 加载MySQL JDBC驱动程序
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            // 如果驱动类无法找到，则打印堆栈跟踪信息
            e.printStackTrace();
        }
    }

    /**
     * 获取一个数据库连接。
     * @return 数据库连接对象
     * @throws SQLException 如果无法建立连接将抛出此异常
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USERNAME, PASSWORD);
    }

    /**
     * 关闭数据库连接。
     * @param conn 要关闭的连接对象，如果为null则不执行任何操作
     */
    public static void closeConnection(Connection conn) {
        if (conn != null) {
            try {
                conn.close(); // 关闭连接
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
