import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import controller.AddReportWithPdfController;
import controller.BagDocumentListViewController;
import controller.BagOperationController;
import controller.DeleteDocumentController;
import controller.DocumentListViewController;
import controller.DocumentOutput;
import controller.GetDocumentsController;
import controller.PdfViewerController;
import controller.UpdateDocumentController;
import controller.UserOperationController;
import service.DocumentService;
import logger.SimpleLogger;

@WebServlet("/app/*")
@MultipartConfig
public class RequestDispatchServlet extends HttpServlet {
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            dispatch(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            dispatch(request, response);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dispatch(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null) {
            // 检查是否已登录
            Integer userId = (Integer) session.getAttribute("userId");
            if (userId != null) {
                // 续期30分钟
                session.setMaxInactiveInterval(30 * 60);// 用户登录后每次操作之后都把session续期为30分钟
            }
        }
        
        // 获取请求路径
        String path = request.getRequestURI();

        // 取出请求的方法字符串
        int index = path.lastIndexOf("/");
        path = path.substring(index);

        switch (path) {
            case "/userOperation"://用户操作，包括注册和登录
                UserOperationController.processRequest(request, response);
                break;

            case "/loadPdf"://上传pdf文件报告
                AddReportWithPdfController.processRequest(request,response);
                break;

            case "/deleteDocument"://删除报告
                DeleteDocumentController.processRequest(request,response);
                break;

            case "/updateDocument":
                UpdateDocumentController.processRequest(request,response);
                break;

            case "/documentListView"://用户查看自己的报告和检索
                SimpleLogger.log("Forwarding request to DocumentListViewController");  // 记录到日志
                // 假设你已创建一个 DocumentService 实例来传递给 Controller
                DocumentService documentService = new DocumentService();

                // Get the ServletContext from the HttpServletRequest
                ServletContext servletContext = request.getServletContext();
                DocumentListViewController documentController = new DocumentListViewController(documentService,servletContext);
                documentController.processRequest(request, response);
                break;

            case "/pdf":
                PdfViewerController.pdfViewer(request,response);
                break;

            case "/bagDocumentListView":
                DocumentService bagDocService = new DocumentService();
                ServletContext bagServletContext = request.getServletContext();
                BagDocumentListViewController bagDocController = new BagDocumentListViewController(bagDocService, bagServletContext);
                bagDocController.processRequest(request, response);
                break;

            case "/bagOperation":// 用于获得用户的报告包列表、添加报告到报告包、删除报告包中的报告
                BagOperationController.processRequest(request, response);
                break;

            case "/getDocuments":// 获得报告包中的报告
                GetDocumentsController.processRequest(request, response);
                break;
            case "/generateReport":// 生成报告
                DocumentOutput.generateReport(request, response);
                break;

            default:
                response.getWriter().println(path);
                break;
        }
    }
}
