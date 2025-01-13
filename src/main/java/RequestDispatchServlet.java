import java.io.IOException;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import controller.AddReportWithPdfController;
import controller.BagDocumentListViewController;
import controller.BagOperationController;
import controller.DeleteDocumentController;
import controller.DocumentListViewController;
import controller.LoginController;
import controller.PdfViewerController;
import controller.RegisterController;
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
        // 获取请求路径
        String path = request.getRequestURI();

        // 取出请求的方法字符串
        int index = path.lastIndexOf("/");
        path = path.substring(index);

        switch (path) {
            case "/register"://新用户注册
                RegisterController.register(request, response);
                break;
                
            case "/login"://用户身份验证
                LoginController.processRequest(request, response);
                break;

            case "/loadPdf"://上传pdf文件
                AddReportWithPdfController.processRequest(request,response);
                break;

            case "/deleteDocument"://删除报告
                DeleteDocumentController.processRequest(request,response);
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

            default:
                response.getWriter().println(path);
                break;
        }
    }
}
