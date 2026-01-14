package com.boyfriend.helper.config;

import com.boyfriend.helper.agents.PhotoPoseAdjustAgent;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;

import static java.time.Duration.ofSeconds;

@Configuration
public class AgentConfig {


    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;

    @Bean
    public ChatModelListener myChatModelListener(){
        return new Langchain4jChatModelListener();
    }

    /**
     * 创建 ChatLanguageModel Bean
     */
    @Bean
    public ChatModel chatLanguageModel(ChatModelListener myChatModelListener) {
        ArrayList<ChatModelListener> listeners = new ArrayList<>();
        listeners.add(myChatModelListener);
        return OpenAiChatModel.builder()
                .listeners(listeners)
                .apiKey(apiKey)
                .modelName(modelName)
                .baseUrl(baseUrl)
                .temperature(0.7)
                .timeout(ofSeconds(60))
                .build();
    }

    @Bean
    public PhotoPoseAdjustAgent photoPoseAdjustAgent(ChatModel chatLanguageModel) {
        return AgenticServices
                .agentBuilder(PhotoPoseAdjustAgent.class)
                .chatModel(chatLanguageModel)
                .outputKey("adjust")
                .build();
    }


}
