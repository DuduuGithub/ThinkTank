package db.dao.proxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import db.dao.base.UserDao;
import db.dao.impl.UserDaoImpl;
import db.util.DBUtil;
import db.vo.User;

/*
 * 代理类，使用时直接使用代理类即可
 * 当使用增删改时，会返回一个void
 * 当使用查询时，会返回对应的数据
 */
public class UserDaoProxy implements UserDao {
    private UserDao dao;
    private Connection conn;

    /*
     * 构造函数，获取一个连接，并初始化dao
     */
    public UserDaoProxy() {
        try {
            conn = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dao = new UserDaoImpl();
    }

    /*
     * 用途：插入新用户数据
     */
    @Override
    public void insert(User user) {
        checkConnection();
        dao.insert(user);
        DBUtil.closeConnection(conn);
    }

    /*
     * 用途：更新用户数据
     */
    @Override
    public void update(User user) {
        checkConnection();
        dao.update(user);
        DBUtil.closeConnection(conn);
    }

    /*
     * 用途：删除用户
     */
    @Override
    public void delete(String userId) {
        checkConnection();
        dao.delete(userId);
        DBUtil.closeConnection(conn);
    }

    /*
     * 用途：根据ID查询用户
     */
    @Override
    public User findById(Integer userId) {
        checkConnection();
        User user = dao.findById(userId);
        DBUtil.closeConnection(conn);
        return user;
    }

    /*
     * 用途：查询所有用户
     */
    @Override
    public List<User> findAll() {
        checkConnection();
        List<User> list = dao.findAll();
        DBUtil.closeConnection(conn);
        return list;
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
