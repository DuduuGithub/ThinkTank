package db.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.List;
import java.util.ArrayList;

import db.dao.base.Bag2documentDao;
import db.vo.Bag2document;

public class Bag2documentDaoImpl implements Bag2documentDao {
    private Connection conn;
    private String sql;
    private PreparedStatement pstmt;
    private boolean flag;
    private ResultSet rs;

    public Bag2documentDaoImpl(Connection conn) {
        this.conn = conn;
    }

    @Override
    public boolean insert(Bag2document bag2document) {
        flag = false;
        sql = "insert into bag2document(bag_id,document_id) values(?,?)";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bag2document.getBagId());
            pstmt.setInt(2, bag2document.getDocumentId());
            flag = pstmt.executeUpdate() > 0;
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean delete(Bag2document bag2document) {
        flag = false;
        sql = "delete from bag2document where bag_id=? and document_id=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bag2document.getBagId());
            pstmt.setInt(2, bag2document.getDocumentId());
            flag = pstmt.executeUpdate() > 0;
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    @Override
    public boolean update(Bag2document bag2document) {
        flag = false;
        // sql = "update bag2document set bag_id=?,document_id=? where bag_id=? and
        // document_id=?";
        // try {
        // pstmt = conn.prepareStatement(sql);
        // pstmt.setInt(1, bag2document.getBagId());
        // pstmt.setInt(2, bag2document.getDocumentId());
        // flag = pstmt.executeUpdate() > 0;
        // pstmt.close();
        // } catch (Exception e) {
        // e.printStackTrace();
        // }
        return flag;
    }

    @Override
    public List<Integer> queryByBagId(int bagId) {
        List<Integer> list = new ArrayList<>();
        sql = "select document_id from bag2document where bag_id=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bagId);
            rs = pstmt.executeQuery();
            while (rs.next()) {
                list.add(rs.getInt(1));
            }
            rs.close();
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public boolean exists(Bag2document bag2document){
        flag = false;
        sql = "select count(*) from bag2document where bag_id=? and document_id=?";
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, bag2document.getBagId());
            pstmt.setInt(2, bag2document.getDocumentId());
            rs = pstmt.executeQuery();
            if(rs.next()){
                flag = rs.getInt(1)>0;
            }
            pstmt.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return flag;
    }

    public void setConnection(Connection conn) {
        this.conn = conn;
    }
}
