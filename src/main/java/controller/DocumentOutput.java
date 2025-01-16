package controller;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import db.vo.Document;
import service.AiReportService;
import service.DocumentService;

public class DocumentOutput {
    private static DocumentService documentService = new DocumentService();

    // 新增方法：生成报告并展示到receive.html
    public static void generateReport(HttpServletRequest request, HttpServletResponse response) throws Exception {
        // 获取description参数
        String description = request.getParameter("description");

        // 获取bagIds参数并转换为List
        String[] bagIdsArray = request.getParameterValues("bagIds");
        List<String> bagIds = Arrays.asList(bagIdsArray != null ? bagIdsArray : new String[0]);

        // 获取documentIds参数并转换为List
        String[] documentIdsArray = request.getParameterValues("documentIds");
        List<String> documentIds = Arrays.asList(documentIdsArray != null ? documentIdsArray : new String[0]);

        // 获取useNetwork参数
        String useNetwork = request.getParameter("useNetwork");

        System.out.println("description: " + description);
        System.out.println("bagIds: " + bagIds);
        System.out.println("documentIds: " + documentIds);
        System.out.println("useNetwork: " + useNetwork);

        // 通过论文编号获取论文内容
        List<String> papers = new ArrayList<>();
        for (String paperID : documentIds) {
            try{
                Integer.parseInt(paperID);
            } catch (NumberFormatException e) {
                continue;
            }
            Document document = documentService.getDocumentById(Integer.valueOf(paperID));
            if (document != null) {
                papers.add(document.getTitle() + "：" + document.getSubject());
            }
        }

        if (bagIds.size() > 0) {
                // 通过包id获取论文内容
                for (String bagId : bagIds) {
                    try{
                        Integer.parseInt(bagId);
                    } catch (NumberFormatException e) {
                        continue;
                    }
                    List<Document> documents = documentService.getDocumentsInBag(Integer.valueOf(bagId));
                    for (Document document : documents) {
                        papers.add(document.getTitle() + "：" + document.getSubject());
                    }
                }


        }

        if (useNetwork != null && useNetwork.equals("true")) {
            // 通过网络获取论文内容
            description = description + "下面会为你提供格式要求以及参考报告，你可以在参考文档之外结合你所学知识进行报告的生成";
        } else {
            description = description + "下面会为你提供格式要求以及参考报告，你只能根据参考文档生成报告";
        }

        // 创建 AiReportService 实例
        AiReportService aiReportService = new AiReportService(description, papers);

        // 生成报告
        String finalReport = aiReportService.generateReport();

        System.out.println(finalReport);

        response.setCharacterEncoding("UTF-8");
        response.setContentType("text;charset=UTF-8");
        // 创建PrintWriter对象，用于写入响应内容
        PrintWriter out = response.getWriter();

        // 写入响应内容
        out.println(finalReport);

        // 关闭PrintWriter对象
        out.close();
    }

}
