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
import controller.GetDocumentsController;
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

            case "/getDocuments":// 获得报告包中的报告
                GetDocumentsController.processRequest(request, response);
                break;
            case "/generateReport":// 生成报告
                response.setCharacterEncoding("UTF-8");
                response.setContentType("text;charset=UTF-8");
                response.getWriter().println("""
阿达父母死后，他依照遗愿，将父母的骨灰撒到大海里。

爹啊，妈啊，你们忍心抛下我孤零零的一个吗。

他对着怀里的骨灰袋念念叨叨。天还没亮，夜空的金星很亮，远方出现鱼肚白。他是从山东海边租的渔船，配了一个小的发动机，拉一根线就轰轰开动。船舱上盘着厚厚的渔网。他念叨的时候抹着泪，其实他没有眼泪，只是抹着脸，但觉得抹泪显得情真意切一些。他的眼泪在父母咽气的时候流过，现在已经没有了。

爹啊，妈啊，你们还嫌我的人生不够倒霉吗。

他抹了一阵泪，天开始亮了。不管人是死是活，海还是那片海，数千年如一日不变。他坐在船上看日出。天空变橙红，小半个太阳是淡金色，一点都不耀眼，这让他内心静下来。天亮之后，白云轻雾，天蓝如洗。海水是墨色，夹杂泥沙。他觉得很舒服，也倦了，只想这样静静地航行，不管航行到那儿。

他慢慢睡着了。

再醒来的时候，他赫然发现前方有一座小岛。离得远，看不清大小。他在GPS上寻找，没有找到，就查下了岛的坐标，记在脑子里，准备回去查。他驾船向小岛驶去。岛的四周被雾气遮掩，看不清全貌。但可以看出岛很小，小得在地图上无法标注。他减了速，熄了引擎，靠惯性朝岛漂去。离得足够近了他抛下锚，跳进水里，又顺着沙滩走到岛上。

岛上除了沙滩、一座小山和树，一无所有。树木郁郁葱葱，很迷人，但是似乎也没有太出奇的地方。他沿着小山绕岛半周，忽然发现一侧的树丛里似乎隐藏着一块竖立的石头。他扒开树丛过去看，发现那是一块无字碑，碑下有一条小路。

他很惊奇，沿着小路一步一步小心翼翼地走过去，心里产生一种莫名的紧张。

路的尽头是一道小门。那是一个山洞，洞口圆整，小门是铜质，门上有圆钉。

他尝试了一下，小门能推动。他轻轻推开门进去，洞里黑漆漆的，什么都看不见。门口透进的光只能照到几米的范围，能看见地面平整，似乎是石材铺就，刻有文字一般的纹理。他用手向四周探索，不知道洞内宽度。

“谁？”

突然，黑暗中响起一个声音。

他吓坏了，打了一个哆嗦，本能地反问道：“谁？”

有片刻没有反应，他几乎以为是自己的幻听。

但是接下来，声音又响起来了。“向。”只是一声之后又没有了，十几秒之后才有下一个声音，“里。”然后又是十几秒，“走。”

他很紧张，有几分恐惧。在这样的地方待一会儿已经令他恐惧，更不用说听到这样奇怪的声音。但他不想逃走。他的好奇推促他向里走。他觉得自己的人生已经没什么可以失去，即使遇到危险也无所谓了。

他触摸到石壁，摸索着向深处走去。转过一个弯道，又一个弯道，他的眼前豁然开朗。

“哎哟，妈呀！”他后退着惊呼起来。

这是一个非常大的石洞，或许已经处在山的腹地。洞的穹顶高昂，顶端的一个圆洞透入天光。在光束的照亮下，他吃惊地见到性质各异的人像，质地很像兵马俑，但是姿态样貌都不同。正对着他的是一个穿帝王袍的男人像，端坐在巨石上。在他身边，有相互依偎的一对男女，有长须的老人，也有年轻的书生。每个塑像都栩栩如生。

他情不自禁地凑上前，在塑像前挥手。太像真人了。他尤其被一个穿帝王袍的人吸引，仔仔细细端详。人像与陶俑兵马俑一般的颜色，但是有着生命体才有的细微光泽，栩栩如生的面目，剑眉细眼，宽阔的下巴，面容沉静安稳，与一般画中的描述大不相同。他没有戴冠，但身上陶土制的袍子有着层层叠叠的厚度，显出华贵。他的眼睛遥望向远方。

“刚才是谁？”他向空洞处喊。

（二）

他举目四望，海上茫茫一片，没有船只，也没有标志。

他只好一个人慢慢地划，划向虚无。

爹啊，妈啊，我怎么这么倒霉啊！他这次是真的哭了。

海上没有一个人影，阳光照耀着海面，他重复地划着，很久都像是没有动。暗蓝色无穷无尽，麻醉神经。他划着划着，怎么也划不到岸。在孤独而静谧的大海里，生命似乎融化在看不到尽头的一个人的重复劳作当中，回到生命本身。

他原本有机会长生不老，但他错过了。洞中声音告诉他，他所看到的所有人像都是不老之人。他们都是历史之人，来到此处，只求长生。一部分躯体化为木石，另一部分躯体变得无比稀薄，飘荡在高空，和木石本体有微弱的联系，生命流逝速度变成从前的几十倍。因此一个人的生命也可以延长几十倍。这里有寻找桃花源的武陵人，有驾乘黄鹤去的修仙人，有七步成诗、赋里结缘的曹植和洛神，有才高八斗的江南才子唐伯虎，也有嬴政，那个坐着穿帝王袍的人。

“秦始皇？”他叫起来，“他不是死了吗？”

“没人见到他死，他出海了，带三千童子。”

“那不是徐福吗？”

“那是告诉世人的故事。嬴政是第一个人，他准备很久了，做了太多实验。”

他也有机会得到永生。在声音的指引下，他甚至都拿到了一颗不老丹，就在他的口袋里。他只要将父母的骨灰撒入大海，就可以妥妥当当地回到洞里变成神仙了。可他哪里想得到，他一上船，就遇到了海盗。他不知道这年代竟然还有海盗。海盗从一个转角突然出现，将他劫上他们的船，搜光了他身上的财物，将他抛进一只橡皮艇，又将他的船拖走了。

我注定倒一辈子霉了吗？他哭道。他揣着不老丹，却不知道怎么做。

大海在他眼前展开，广袤，重复，平静，无边。

他越来越累，阳光的金色和蓝色让他头晕。

永生是不是就是这种感觉，他想，永远是重复，没个尽头。

他又睡着了。
""");
                break;

            default:
                response.getWriter().println(path);
                break;
        }
    }
}
