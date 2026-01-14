package com.boyfriend.helper.config;

import com.boyfriend.helper.agents.PhotoEditAgent;
import com.boyfriend.helper.agents.PhotoPoseAdjustAgent;
import com.volcengine.ark.runtime.service.ArkService;
import dev.langchain4j.agentic.AgenticServices;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.listener.ChatModelListener;
import dev.langchain4j.model.openai.OpenAiChatModel;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import static java.time.Duration.ofSeconds;

@Configuration
public class PhotoAgentConfig {


    @Value("${langchain4j.open-ai.chat-model.api-key}")
    private String apiKey;

    @Value("${langchain4j.open-ai.chat-model.model-name}")
    private String modelName;

    @Value("${langchain4j.open-ai.chat-model.base-url}")
    private String baseUrl;


    @Bean
    public ChatModelListener myChatModelListener() {
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

    @Bean
    @ConditionalOnMissingBean
    public ArkService arkService() {
        ConnectionPool connectionPool = new ConnectionPool(5, 1, TimeUnit.SECONDS);
        Dispatcher dispatcher = new Dispatcher();
        return ArkService.builder().dispatcher(dispatcher).connectionPool(connectionPool).apiKey(apiKey).build();
    }

    @Bean
    public PhotoEditAgent photoEditAgent(ArkService arkService) {
        return new PhotoEditAgent(arkService);
    }

    @Bean
    public UntypedAgent llmPhotoEditAgent(ChatModel chatLanguageModel, PhotoEditAgent photoEditAgent) {
        return AgenticServices
                .sequenceBuilder()
                .subAgents(
                        photoEditAgent
                )
                .outputKey("afterUrl") // outputKey defined on the non-AI agent annotation in ScoreAggregator.java
                .build();
    }

}
