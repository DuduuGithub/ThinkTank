package db.dao.base;

import java.util.List;

import db.vo.Bag2document;

/*
 * 声明了Bag2documentDao接口
 */
public interface Bag2documentDao {
    /*
     * 向报告包插入报告
     */
    public boolean insert(Bag2document bag2document);
    /*
     * 从报告包中删除报告
     */
    public boolean delete(Bag2document bag2document);
    /*
     * 更新报告包中的报告，这个估计用不上
     */
    public boolean update(Bag2document bag2document);
    /*
     * 查询报告包中的报告，返回一个List，内容是报告的id
     */
    public List<Integer> queryByBagId(int bagId);
}
