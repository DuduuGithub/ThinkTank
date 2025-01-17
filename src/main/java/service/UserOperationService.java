package service;

import java.util.List;

import db.dao.impl.UserDaoImpl;
import db.dao.proxy.UserDaoProxy;
import db.factory.DaoFactory;
import db.vo.User;
import db.vo.Document;
import controller.DocumentListViewController;

public class UserOperationService {

    /*
     * 用途：用于用户登录时对用户的验证
     * 参数：字符串形式的用户id和密码
     * 返回：如果验证通过，返回True，否则返回False
     */
    public static boolean userVerify(String userIdString,String password){
        
        // 把字符串userId转化为整数形式
        int userId = Integer.parseInt(userIdString);

        UserDaoImpl userDaoImpl = new UserDaoImpl();
        User queryResult = userDaoImpl.findById(userId);

        if(queryResult == null){
            return false;
        }
        else{
            String rightPassword = queryResult.getPassword();
            if(password.equals(rightPassword)){
                return true;
            }
            else{
                return false;
            }
        }
    }
    
    /*
     * 用途：用于注册新用户，会把系统默认的报告加到新用户的名下
     * 参数：新用户的密码
     * 返回：表明注册结果的json格式字符串
     */
    public static String register(String password){
        UserDaoProxy userDapProxy = DaoFactory.getInstance().getUserDao();
        int userId = userDapProxy.insert(new User(password));
        if(userId == -1){
            return "{\"success\":false,\"message\":\"注册失败\"}";
        }else{
            // 查找到系统初始库的所有报告，并将其插入到新用户的名下
            List<Document> defaultDocuments = DaoFactory.getInstance().getDocumentDao().findByUserId("1");
            for(Document document:defaultDocuments){
                // 把报告插入数据库并获取新的记录的 documentId
                document.setUserId(userId);
                int newDocId = DaoFactory.getInstance().getDocumentDao().insert(document);
                // 把向量文件复制一份
                new DocumentListViewController().copyVectorFile(document.getDocumentId(), newDocId);
            }
            return "{\"success\":true,\"message\":\"注册成功\",\"userId\":\"" + userId + "\"}";
        }
    }
}
