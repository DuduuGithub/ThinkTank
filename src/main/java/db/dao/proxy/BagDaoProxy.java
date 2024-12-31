package db.dao.proxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import db.dao.base.BagDao;
import db.dao.impl.BagDaoImpl;
import db.util.DBUtil;
import db.vo.Bag;

/*
 * 代理类，使用时直接使用代理类即可
 * 当使用增删改时，会返回一个boolean，表示操作是否成功
 * 当使用查询时，会返回一个List<Bag>，表示查询到的报告包
 */
public class BagDaoProxy implements BagDao {
    private BagDao dao;
    private Connection conn;
    private boolean flag;

    /*
     * 构造函数，获取一个连接，并初始化dao
     */
    public BagDaoProxy() {
        try {
            conn = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        dao = new BagDaoImpl(conn);
    }

    /*
     * 用途：用户添加报告包
     */
    public boolean insert(Bag bag) {
        checkConnection();
        flag = dao.insert(bag);
        DBUtil.closeConnection(conn);
        return flag;
    };

    /*
     * 用途：用户修改报告包名
     */
    public boolean update(Bag bag) {
        checkConnection();
        flag = dao.update(bag);
        DBUtil.closeConnection(conn);
        return flag;
    };

    /*
     * 用途：用户删除报告包
     */
    public boolean delete(Bag bag) {
        checkConnection();
        flag = dao.delete(bag);
        DBUtil.closeConnection(conn);
        return flag;
    };

    /*
     * 用途：根据userId查询用户的报告包
     */
    public List<Bag> queryByUserId(int userId) {
        checkConnection();
        List<Bag> list = dao.queryByUserId(userId);
        DBUtil.closeConnection(conn);
        return list;
    };

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
