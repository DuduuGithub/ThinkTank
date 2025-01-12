package service;

import db.dao.proxy.Bag2documentDaoProxy;
import db.vo.Bag2document;

public class BagOperationService {

    public static String addToBag(int bagId, int documentId) {
        Bag2documentDaoProxy bag2documentDaoProxy = new Bag2documentDaoProxy();

        //检查报告是否已经存在
        if(bag2documentDaoProxy.exists(new Bag2document(bagId, documentId))){
            return "{\"success\": true, \"message\": \"已收藏到该报告包中\"}";
        }else{
            // 报告不存在，则将该报告插入该报告包
            boolean success = bag2documentDaoProxy.insert(new Bag2document(bagId, documentId));
            if (success) {
                return "{\"success\": true, \"message\": \"收藏成功\"}";
            } else {
                return "{\"success\": false, \"message\": \"收藏失败\"}";
            }
        }
    }

    public static String removeFromBag(int bagId, int documentId) {
        Bag2documentDaoProxy bag2documentDaoProxy = new Bag2documentDaoProxy();
        boolean success = bag2documentDaoProxy.delete(new Bag2document(bagId, documentId));

        if (success) {
            return "{\"success\": true, \"message\": \"删除成功\"}";
        } else {
            return "{\"success\": false, \"message\": \"删除失败\"}";
        }
    }
}
