package db.factory;

import db.dao.impl.DocumentDaoImpl;
import db.dao.proxy.DocumentDaoProxy;

//待改，要返回Proxy对象
public class DaoFactory {
    public static DocumentDaoProxy createDocumentDaoProxy() {
        return new DocumentDaoProxy(new DocumentDaoImpl());
    }
    
    // public static UserDaoProxy createUserDaoProxy() {
    //     return new UserDaoImpl();
    // }
} 