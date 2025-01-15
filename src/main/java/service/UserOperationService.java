package service;

import db.dao.impl.UserDaoImpl;
import db.dao.proxy.UserDaoProxy;
import db.factory.DaoFactory;
import db.vo.User;

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
    public static String register(String password){
        UserDaoProxy userDapProxy = DaoFactory.getInstance().getUserDao();
        int userId = userDapProxy.insert(new User(password));
        if(userId == -1){
            return "{\"success\":false,\"message\":\"注册失败\"}";
        }else{
            return "{\"success\":true,\"message\":\"注册成功\",\"userId\":\"" + userId + "\"}";
        }
    }
}
