package db.factory;

import db.dao.proxy.BagDaoProxy;
import db.dao.proxy.Bag2documentDaoProxy;
import db.dao.proxy.DocumentDaoProxy;
import db.dao.proxy.UserDaoProxy;

/*
 * 工厂类，用于获取各种Dao的代理对象
 * 使用单例模式，确保全局只有一个工厂实例
 */
public class DaoFactory {
    private static DaoFactory instance = new DaoFactory();

    /*
     * 私有构造函数，防止外部创建实例
     */
    private DaoFactory() {
    }

    /*
     * 获取工厂实例
     */
    public static DaoFactory getInstance() {
        return instance;
    }

    /*
     * 获取BagDao的代理对象
     */
    public BagDaoProxy getBagDao() {
        return new BagDaoProxy();
    }

    /*
     * 获取Bag2documentDao的代理对象
     */
    public Bag2documentDaoProxy getBag2documentDao() {
        return new Bag2documentDaoProxy();
    }

    /*
     * 获取DocumentDao的代理对象
     */
    public DocumentDaoProxy getDocumentDao() {
        return new DocumentDaoProxy();
    }

    /*
     * 获取UserDao的代理对象
     */
    public UserDaoProxy getUserDao() {
        return new UserDaoProxy();
    }
} 