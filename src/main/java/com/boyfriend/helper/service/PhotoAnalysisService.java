package com.boyfriend.helper.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.boyfriend.helper.model.AnalysisRequest;
import com.boyfriend.helper.model.EditPhotoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImageMessage;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.ai.content.Media;
import java.net.URI;
import static org.springframework.ai.chat.memory.ChatMemory.CONVERSATION_ID;
@Service
@Slf4j
public class PhotoAnalysisService {

    private final ChatClient dashScopeChatClient;

    public PhotoAnalysisService(DashScopeChatModel chatModel,MessageWindowChatMemory messageWindowChatMemory) {
        this.dashScopeChatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                                .build()
                )
                .build();
    }
    public ResponseEntity<?> analyzePhoto(AnalysisRequest request) throws IOException, URISyntaxException {

        List<Media> mediaList = List.of(new Media(MimeTypeUtils.IMAGE_PNG,
                new URI(request.url()).toURL().toURI()));

        UserMessage message =
                UserMessage.builder().text(request.prompt()).media(mediaList).metadata(new HashMap<>()).build();
        message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

        try {
            ChatResponse response = dashScopeChatClient
                    .prompt(new Prompt(message))
                    .advisors(
                            a -> a.param(CONVERSATION_ID, request.sessionId()))
                    .call()
                    .chatResponse();
            log.info("response:{}", response.getResult().getOutput().getText());
            return ResponseEntity.ok(response.getResult().getOutput().getText());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of(
                            "error", "图像生成失败",
                            "message", e.getMessage(),
                            "timestamp", LocalDateTime.now()
                    ));
        }
    }
}
