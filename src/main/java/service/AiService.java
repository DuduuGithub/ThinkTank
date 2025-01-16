package service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.JSONArray;
import okhttp3.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import logger.SimpleLogger;

public class AiService {
    private static final String API_URL = "https://api.deepseek.com/v1/chat/completions";
    private static final String API_KEY = "sk-a649f68000a04135adc55432f4d1da0e";
    private static final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();

    public String getCompletion(String prompt) {
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-chat");
            
            // 构建消息列表
            List<Map<String, String>> messages = new ArrayList<>();
            // 添加 system 消息
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "You are a helpful assistant specialized in analyzing document content and enhancing search terms.");
            messages.add(systemMessage);
            
            // 添加用户消息
            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", prompt);
            messages.add(userMessage);
            
            requestBody.put("messages", messages);
            requestBody.put("max_tokens", 100);
            requestBody.put("temperature", 0.7);
            requestBody.put("stream", false);

            String jsonBody = JSON.toJSONString(requestBody);
            SimpleLogger.log("Request payload: " + jsonBody);

            // 构建请求
            Request request = new Request.Builder()
                .url(API_URL)
                .addHeader("Authorization", "Bearer " + API_KEY)
                .addHeader("Content-Type", "application/json")
                .post(RequestBody.create(MediaType.parse("application/json"), jsonBody))
                .build();

            // 发送请求
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    String errorBody = response.body() != null ? response.body().string() : "Unknown error";
                    SimpleLogger.log("API request failed with code " + response.code() + ": " + errorBody);
                    return "";
                }

                String responseBody = response.body().string();
                SimpleLogger.log("API Response: " + responseBody);

                // 解析响应
                JSONObject jsonResponse = JSON.parseObject(responseBody);
                JSONArray choices = jsonResponse.getJSONArray("choices");
                if (choices != null && !choices.isEmpty()) {
                    JSONObject firstChoice = choices.getJSONObject(0);
                    JSONObject message = firstChoice.getJSONObject("message");
                    if (message != null) {
                        return message.getString("content");
                    }
                }
            }
        } catch (Exception e) {
            SimpleLogger.log("Error calling DeepSeek API: " + e.getMessage());
            SimpleLogger.log("Stack trace: " + Arrays.toString(e.getStackTrace()));
        }
        return "";
    }
} 