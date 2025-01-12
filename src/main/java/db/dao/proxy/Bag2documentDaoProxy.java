package db.dao.proxy;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import db.dao.base.Bag2documentDao;
import db.dao.impl.Bag2documentDaoImpl;
import db.util.DBUtil;
import db.vo.Bag2document;

/*
 * 代理类，使用时直接使用代理类即可
 * 当使用增删改时，会返回一个boolean，表示操作是否成功
 * 当使用查询时，会返回一个List<Integer>，表示查询到的报告的id
 */
public class Bag2documentDaoProxy implements Bag2documentDao {
    private Bag2documentDaoImpl bag2documentDao;
    private Connection conn;
    private boolean flag;

    /*
     * 构造函数，获取一个连接，并初始化dao
     */
    public Bag2documentDaoProxy() {
        try {
            conn = DBUtil.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        bag2documentDao = new Bag2documentDaoImpl(conn);
    }

    /*
     * 向报告包插入报告
     */
    @Override
    public boolean insert(Bag2document bag2document) {
        checkConnection();
        flag = bag2documentDao.insert(bag2document);
        DBUtil.closeConnection(conn);
        return flag;
    }

    /*
     * 从报告包中删除报告
     */
    @Override
    public boolean delete(Bag2document bag2document) {
        checkConnection();
        flag = bag2documentDao.delete(bag2document);
        DBUtil.closeConnection(conn);
        return flag;
    }

    /*
     * 更新报告包中的报告，这个估计用不上
     */
    @Override
    public boolean update(Bag2document bag2document) {
        checkConnection();
        flag = bag2documentDao.update(bag2document);
        DBUtil.closeConnection(conn);
        return flag;
    }

    /*
     * 查询报告包中的报告，返回一个List，内容是报告的id
     */
    @Override
    public List<Integer> queryByBagId(int bagId) {
        checkConnection();
        List<Integer> list = bag2documentDao.queryByBagId(bagId);
        DBUtil.closeConnection(conn);
        return list;
    }

    @Override
    public boolean exists(Bag2document bag2document){
        checkConnection();
        flag = bag2documentDao.exists(bag2document);
        DBUtil.closeConnection(conn);
        return flag;
    }

    /*
     * 检查连接是否为空，如果为空，则获取一个新连接并更新给实现类实例，所有方法都要检查
     */
    private void checkConnection() {
        try {
            if (conn.isClosed()) {
                try {
                    conn = DBUtil.getConnection();
                    this.bag2documentDao.setConnection(conn);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
