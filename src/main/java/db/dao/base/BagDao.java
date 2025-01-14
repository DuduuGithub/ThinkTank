package db.dao.base;

import java.util.List;

import db.vo.Bag;

/*
 * BagDao接口声明用户报告包的所有基本方法
 */
public interface BagDao {
    /*
     * 用途：用户添加报告包
     */
    public boolean insert(Bag bag);

    /*
     * 用途：用户修改报告包名
     */
    public boolean update(Bag bag);

    /*
     * 用途：用户删除报告包
     */
    public boolean delete(int bagId);

    /*
     * 用途：根据userId查询用户的报告包
     */
    public List<Bag> queryByUserId(int userId);
}
