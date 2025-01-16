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
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.thymeleaf.context.WebContext;

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

    // 渲染模板的方法
    public String render(String templateName, HttpServletRequest request, HttpServletResponse response, WebContext context) {
        StringWriter writer = new StringWriter();
        templateEngine.process(templateName, context, writer);
        return writer.toString();
    }
}
