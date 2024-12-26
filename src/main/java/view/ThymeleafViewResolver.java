package view;

import db.vo.Document;
import java.util.List;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

import java.io.StringWriter;
import logger.SimpleLogger;

import javax.servlet.ServletContext;

public class ThymeleafViewResolver {

    private TemplateEngine templateEngine;

    public ThymeleafViewResolver(ServletContext servletContext) {
        init(servletContext);
    }

    private void init(ServletContext servletContext) {
        // 创建 Thymeleaf 模板解析器
        ServletContextTemplateResolver templateResolver = new ServletContextTemplateResolver(servletContext);

        // 配置模板解析器
        templateResolver.setTemplateMode(TemplateMode.HTML);  // 模板模式为 HTML
        templateResolver.setPrefix("/");  // 设置为 webapp 目录的根目录
        templateResolver.setSuffix(".html");  // 模板文件后缀
        templateResolver.setCacheable(false);  // 禁用缓存，方便开发时查看变化
        // ⑥设置服务器端编码方式
        templateResolver.setCharacterEncoding("utf-8");

        // 创建模板引擎
        templateEngine = new TemplateEngine();
        templateEngine.setTemplateResolver(templateResolver);
    }

    // 渲染模板的方法，传入 Context 和 Writer（如 StringWriter）
    public String render(String templateName, Context context) {
        StringWriter writer = new StringWriter();

       // 假设 documentList 是一个 List<Document> 类型的集合
        List<Document> documentList = (List<Document>) context.getVariable("documentList");

        // 遍历 documentList 中的每个 Document 对象
        StringBuilder sb = new StringBuilder();
        for (Document document : documentList) {
            // 获取 Document 的各个属性值
            sb.append("Document ID: ").append(document.getDocumentId()).append(", ");
            sb.append("Title: ").append(document.getTitle()).append(", ");
            sb.append("Keywords: ").append(document.getKeywords()).append(", ");
            sb.append("Subject: ").append(document.getSubject()).append("\n");
        }

        // 记录日志
        SimpleLogger.log("context documentList details:\n" + sb.toString());


        templateEngine.process(templateName, context, writer); // 生成 HTML 字符串
        return writer.toString();  // 返回渲染的 HTML 字符串
    }
}
