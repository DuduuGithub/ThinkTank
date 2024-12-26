package db.dao.impl;

import db.dao.base.UserDao;
import db.util.DBUtil;
import db.vo.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * UserDaoImpl类实现了UserDao接口中定义的用户相关数据库操作。
 * 使用JDBC直连数据库进行增删改查。
 */

 public class UserDaoImpl implements UserDao {

    @Override
    public void insert(User user) {
        String sql = "INSERT INTO user (user_id, password) VALUES (?, ?)";
        Connection conn = null;
        try {
            // 获取数据库连接
            conn = DBUtil.getConnection();
            // 准备SQL预编译语句
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, user.getUserId());
            pstmt.setString(2, user.getPassword());
            // 执行更新
            pstmt.executeUpdate();
        } catch (SQLException e) {
            // 出现异常则打印堆栈并抛出RuntimeException
            e.printStackTrace();
            throw new RuntimeException("插入用户数据失败", e);
        } finally {
            // 确保连接最终被关闭
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public void update(User user) {
        String sql = "UPDATE user SET password = ? WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, user.getPassword());
            pstmt.setInt(2, user.getUserId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("更新用户数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public void delete(String userId) {
        String sql = "DELETE FROM user WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("删除用户数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public User findById(Integer userId) {
        String sql = "SELECT * FROM user WHERE user_id = ?";
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setPassword(rs.getString("password"));
                return user;
            }
            return null;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询用户数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM user";
        Connection conn = null;
        List<User> users = new ArrayList<>();
        try {
            conn = DBUtil.getConnection();
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setPassword(rs.getString("password"));
                users.add(user);
            }
            return users;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("查询所有用户数据失败", e);
        } finally {
            DBUtil.closeConnection(conn);
        }
    }
}
