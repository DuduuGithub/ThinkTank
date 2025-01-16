package service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.gson.Gson;

import logger.SimpleLogger;
import okhttp3.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class BigModelNew extends WebSocketListener {
    // 各版本的hostUrl及其对应的domain参数，具体可以参考接口文档 https://www.xfyun.cn/doc/spark/Web.html
    public static final String hostUrl = "https://spark-api.xf-yun.com/v4.0/chat";
    public static final String domain = "4.0Ultra";
    public static final String appid = "ce9ffe63";
    public static final String apiSecret = "ODhjMzViODcwODU4Njk0ZTgxZWI1ZGVh";
    public static final String apiKey = "37a6c3241c800fc455c445176efddc0d";

    public static final Gson gson = new Gson();

    private static boolean wsCloseFlag = false;
    private static String totalAnswer = "";
    private static StringBuilder currentResponse = new StringBuilder();
    private static OkHttpClient client;
    private static long lastMessageTime;  // 新增：最后一条消息的时间
    private static final long TIMEOUT_MS = 100000;  // 新增：超时时间，100秒

    // 线程来发送音频与参数
    class MyThread extends Thread {
        private WebSocket webSocket;
        private String question;

        public MyThread(WebSocket webSocket, String question) {
            this.webSocket = webSocket;
            this.question = question;
        }

        public void run() {
            try {
                JSONObject requestJson = new JSONObject();

                JSONObject header = new JSONObject();  // header参数
                header.put("app_id", appid);
                header.put("uid", UUID.randomUUID().toString().substring(0, 10));

                JSONObject parameter = new JSONObject(); // parameter参数
                JSONObject chat = new JSONObject();
                chat.put("domain", domain);
                chat.put("temperature", 0.5);
                chat.put("max_tokens", 4096);
                parameter.put("chat", chat);

                JSONObject payload = new JSONObject(); // payload参数
                JSONObject message = new JSONObject();
                JSONArray text = new JSONArray();

                // 仅添加最新问题，而不需要历史记录
                RoleContent roleContent = new RoleContent();
                roleContent.role = "user";
                roleContent.content = question;
                text.add(JSON.toJSON(roleContent));  // 只添加当前问题

                message.put("text", text);
                payload.put("message", message);

                requestJson.put("header", header);
                requestJson.put("parameter", parameter);
                requestJson.put("payload", payload);

                // 发送请求
                webSocket.send(requestJson.toString());

                // 等待 WebSocket 完成后关闭
                while (!wsCloseFlag) {
                    Thread.sleep(200);
                }
                webSocket.close(1000, "");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // 用于外部调用的接口方法
    public static String askQuestion(String question) throws Exception {
        // 重置所有状态
        wsCloseFlag = false;
        totalAnswer = "";
        currentResponse.setLength(0);
        lastMessageTime = System.currentTimeMillis();  // 重置时间
        
        if (client != null) {
            // 清理旧的连接
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
        }
        
        // 创建新的 client
        client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();

        // 构建鉴权url
        String authUrl = getAuthUrl(hostUrl, apiKey, apiSecret);
        String url = authUrl.replace("http://", "ws://").replace("https://", "wss://");
        Request request = new Request.Builder().url(url).build();

        // 连接到 WebSocket 并发送请求
        WebSocketListener listener = new BigModelNew();
        WebSocket webSocket = client.newWebSocket(request, listener);
        
        // 启动线程发送问题
        MyThread myThread = new BigModelNew().new MyThread(webSocket, question);
        myThread.start();

        // 等待答案返回
        while (!wsCloseFlag) {
            Thread.sleep(10);
        }

        // 获取结果并清理
        String result = totalAnswer;
        totalAnswer = "";
        
        SimpleLogger.log("BigModelNew大模型回答: " + result);
        return result;
    }

    @Override
    public void onOpen(WebSocket webSocket, Response response) {
        super.onOpen(webSocket, response);
        wsCloseFlag = false;
        currentResponse.setLength(0);
        lastMessageTime = System.currentTimeMillis();  // 初始化时间
    }

    @Override
    public void onMessage(WebSocket webSocket, String text) {        
        JsonParse myJsonParse = gson.fromJson(text, JsonParse.class);
        
        if (myJsonParse.header.code != 0) {
            SimpleLogger.log("发生错误，错误码：" + myJsonParse.header.code + 
                           "，消息：" + myJsonParse.header.sid);
            wsCloseFlag = true;
            webSocket.close(1000, "Error: " + myJsonParse.header.code);
            return;
        }

        List<Text> textList = myJsonParse.payload.choices.text;
        for (Text temp : textList) {
            currentResponse.append(temp.content);
        }

        // 检查是否超时
        if (System.currentTimeMillis() - lastMessageTime > TIMEOUT_MS) {
            SimpleLogger.log("响应超时，使用当前累积的响应");
            totalAnswer = currentResponse.toString();
            currentResponse.setLength(0);
            wsCloseFlag = true;
            webSocket.close(1000, "Timeout");
            return;
        }

        if (myJsonParse.header.status == 2) {
            totalAnswer = currentResponse.toString();
            currentResponse.setLength(0);
            wsCloseFlag = true;
            SimpleLogger.log("连接已关闭");
        }
    }

    @Override
    public void onFailure(WebSocket webSocket, Throwable t, Response response) {
        wsCloseFlag = true;
        currentResponse.setLength(0);
        SimpleLogger.log("WebSocket连接失败: " + t.getMessage());
        if (response != null) {
            try {
                SimpleLogger.log("失败响应: " + response.body().string());
            } catch (IOException e) {
                SimpleLogger.log("读取失败响应出错: " + e.getMessage());
            }
        }
    }

    // 鉴权方法
    public static String getAuthUrl(String hostUrl, String apiKey, String apiSecret) throws Exception {
        URL url = new URL(hostUrl);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US);
        format.setTimeZone(TimeZone.getTimeZone("GMT"));
        String date = format.format(new Date());
        
        String preStr = "host: " + url.getHost() + "\n" +
                "date: " + date + "\n" +
                "GET " + url.getPath() + " HTTP/1.1";
        Mac mac = Mac.getInstance("hmacsha256");
        SecretKeySpec spec = new SecretKeySpec(apiSecret.getBytes(StandardCharsets.UTF_8), "hmacsha256");
        mac.init(spec);

        byte[] hexDigits = mac.doFinal(preStr.getBytes(StandardCharsets.UTF_8));
        String sha = Base64.getEncoder().encodeToString(hexDigits);

        String authorization = String.format("api_key=\"%s\", algorithm=\"%s\", headers=\"%s\", signature=\"%s\"", apiKey, "hmac-sha256", "host date request-line", sha);
        HttpUrl httpUrl = Objects.requireNonNull(HttpUrl.parse("https://" + url.getHost() + url.getPath())).newBuilder().
                addQueryParameter("authorization", Base64.getEncoder().encodeToString(authorization.getBytes(StandardCharsets.UTF_8))).
                addQueryParameter("date", date).
                addQueryParameter("host", url.getHost()).
                build();

        return httpUrl.toString();
    }

    //返回的json结果拆解
    class JsonParse {
        Header header;
        Payload payload;
    }

    class Header {
        int code;
        int status;
        String sid;
    }

    class Payload {
        Choices choices;
    }

    class Choices {
        List<Text> text;
    }

    class Text {
        String role;
        String content;
    }

    class RoleContent {
        String role;
        String content;

        public String getRole() {
            return role;
        }

        public void setRole(String role) {
            this.role = role;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }
    }

    public static void closeAllConnections() {
        if (client != null) {
            client.dispatcher().executorService().shutdown();
            client.connectionPool().evictAll();
            client = null;
        }
    }

    public static void main(String[] args) {
        try {
            // 测试第一个问题
            System.out.println("提问第一个问题：");
            String answer1 = askQuestion("你好，请介绍一下你自己");
            System.out.println("\n最终答案：" + answer1);
            
            // 测试第二个问题
            System.out.println("\n提问第二个问题：");
            String answer2 = askQuestion("1+1等于多少？");
            System.out.println("\n最终答案：" + answer2);

            // 测试第二个问题
            System.out.println("\n提问第二个问题：");
            String answer3 = askQuestion("1+2等于多少？");
            System.out.println("\n最终答案：" + answer3);

            // 测试第二个问题
            System.out.println("\n提问第二个问题：");
            String answer4= askQuestion("1+3等于多少？");
            System.out.println("\n最终答案：" + answer4);

            // 测试第二个问题
            System.out.println("\n提问第二个问题：");
            String answer5 = askQuestion("1+4等于多少？");
            System.out.println("\n最终答案：" + answer5);

            // 测试第二个问题
            System.out.println("\n提问第二个问题：");
            String answer6 = askQuestion("1+5等于多少？");
            System.out.println("\n最终答案：" + answer6);
            
        } catch (Exception e) {
            System.out.println("发生错误：");
            e.printStackTrace();
        }
    }
}
