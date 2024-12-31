package controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DeleteReportController {
    public static void processRequest(HttpServletRequest request,HttpServletResponse response) throws Exception{
        String fileIdParam = request.getParameter("id");
        if (fileIdParam == null || fileIdParam.isEmpty()) {
            // 参数缺失，返回错误信息
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "请求中缺少fileId参数");
            return;
        }
        int fileId = 0;
        try{
            fileId = Integer.parseInt(fileIdParam);
        }catch(NumberFormatException e){
            // 参数格式错误，返回错误信息
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "fileId参数无效");
            return;
        }
        
    }
}
