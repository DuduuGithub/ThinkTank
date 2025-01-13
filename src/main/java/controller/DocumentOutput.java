package controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.vo.Document;
import service.AiReportService;
import service.DocumentService;



public class DocumentOutput {
    private static DocumentService documentService;
    // 新增方法：生成报告并展示到receive.html
    public static void generateReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 从请求中获取描述和论文列表
        String description = request.getParameter("description");
        List<String> papersID = getPapersIDFromRequest(request);

        // 通过论文编号获取论文内容
        List<String> papers = new ArrayList<>();
        for (String paperID : papersID) {
            Document document = documentService.getDocumentById(Integer.valueOf(paperID));
            if (document != null) {
                papers.add(document.getContent());
            }
        }

        // 创建 AiReportService 实例
        AiReportService aiReportService = new AiReportService(description, papers);

        // 生成报告
        String finalReport = aiReportService.generateReport();

        // 将生成的报告存入 request 属性
        request.setAttribute("reportContent", finalReport);

        // 转发到 receive.html
        request.getRequestDispatcher("/WEB-INF/views/receive.html").forward(request, response);
    }

    // 辅助方法：从请求中获取论文列表
    private static List<String> getPapersIDFromRequest(HttpServletRequest request) {
        List<String> papersID = new ArrayList<>();
        int i = 0;
        while (request.getParameter("paperID" + i) != null) {
            papersID.add(request.getParameter("paperID" + i));
            i++;
        }
        return papersID;
    }
}
