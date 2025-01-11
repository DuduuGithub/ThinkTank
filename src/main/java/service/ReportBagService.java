package service;

import java.util.List;

import com.google.gson.Gson;

import db.dao.proxy.BagDaoProxy;
import db.vo.Bag;

public class ReportBagService {
    public static String queryBags(Integer userId) {
        // 查询用户的报告包
        BagDaoProxy bagDao = new BagDaoProxy();
        List<Bag> bags = bagDao.queryByUserId(userId);
        
        // 转换为JSON
        Gson gson = new Gson();
        return gson.toJson(bags);
    }
}
