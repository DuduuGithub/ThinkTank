package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.dao.proxy.Bag2documentDaoProxy;
import db.vo.Bag2document;

public class CollectController {
    /*
     * 用途：用于将一个报告收藏到一个报告包
     * 参数：请求中包含报告的id和报告包的id
     */
    public static void processRequest(HttpServletRequest request,HttpServletResponse response){
        int documentId = Integer.parseInt(request.getParameter("id"));
        int bagId = Integer.parseInt(request.getParameter("bagId"));
        Bag2document bag2document = new Bag2document(bagId, documentId);

        Bag2documentDaoProxy bag2documentDaoProxy = new Bag2documentDaoProxy();
        boolean insertFlag = bag2documentDaoProxy.insert(bag2document);

        if(insertFlag){
            
        }

    }
}
