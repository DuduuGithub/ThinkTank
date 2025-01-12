package db.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import db.dao.base.BagDao;
import db.vo.Bag;

public class BagDaoImpl implements BagDao {
    private Connection conn;
    private String sql;
    private PreparedStatement pstmt;
    private boolean flag;
    private ResultSet rs;

    public BagDaoImpl(Connection conn) {
        this.conn = conn;
    }

    /*
     * 用途：用户添加报告包
     */
    public boolean insert(Bag bag) {
        flag = false;
        sql = "insert into bag(bag_name,user_id) values(?,?)";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bag.getBagName());
            pstmt.setInt(2, bag.getUserId());
            flag = pstmt.executeUpdate() > 0;
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    };

    /*
     * 用途：用户修改报告包名
     */
    public boolean update(Bag bag) {
        flag = false;
        sql = "update bag set bag_name=? where bag_id=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, bag.getBagName());
            pstmt.setInt(2, bag.getBagId());
            flag = pstmt.executeUpdate() > 0;
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    };

    /*
     * 用途：用户删除报告包
     */
    public boolean delete(Bag bag) {
        flag = false;
        sql = "delete from bag where bag_id=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bag.getBagId());
            flag = pstmt.executeUpdate() > 0;
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    };

    /*
     * 用途：根据userId查询用户的报告包
     */
    public List<Bag> queryByUserId(int userId) {
        List<Bag> bags = new ArrayList<>();
        sql = "select * from bag where user_id=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, userId);

            // 执行查询
            rs = pstmt.executeQuery();

            // 遍历结果集
            while (rs.next()) {
                Bag bag = new Bag();
                bag.setBagId(rs.getInt(1));
                bag.setBagName(rs.getString(2));
                bag.setUserId(rs.getInt(3));
                bags.add(bag);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return bags;
    };

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}
