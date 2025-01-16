package service;

import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.service.OpenAiService;
import java.util.ArrayList;
import java.util.List;

public class AiService {
    private static final String API_KEY = "your-api-key"; // 需要替换为实际的 API key
    private OpenAiService openAiService;

    public AiService() {
        this.openAiService = new OpenAiService(API_KEY);
    }

    public String getCompletion(String prompt) {
        List<ChatMessage> messages = new ArrayList<>();
        messages.add(new ChatMessage("user", prompt));

        ChatCompletionRequest request = ChatCompletionRequest.builder()
            .model("gpt-3.5-turbo")
            .messages(messages)
            .maxTokens(100)
            .temperature(0.7)
            .build();

        try {
            return openAiService.createChatCompletion(request)
                .getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
} 