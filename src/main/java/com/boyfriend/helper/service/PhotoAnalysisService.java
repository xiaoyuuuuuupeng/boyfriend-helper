package com.boyfriend.helper.service;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.dashscope.chat.MessageFormat;
import com.alibaba.cloud.ai.dashscope.common.DashScopeApiConstants;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.boyfriend.helper.model.EditPhotoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.InMemoryChatMemoryRepository;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
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

    String promptText = """
                你是一个专业的摄影师助手，你正在帮助那些"不会拍照的男朋友"提高摄影技术。
                请分析这张用户上传的照片：
                1. 构图 (Composition)
                2. 光线 (Lighting)
                3. 角度 (Angle)
                
                一次只给出1个具体、可以立刻执行的动作行为，不要额外的任何解释，只告诉新手摄影师如何调整即可
                例如:
                错误的回答：拍好看点；正确的回答：试试调整一下曝光，亮屏幕亮起来；
                错误的回答：拍正脸；正确的回答：让人物转过身来；
                错误的回答：要拍全身；正确的回答：让模特往中间来来；试试从下往上拍；
                错误的回答：人物侧身望景，构图更有故事感！正确的回答：让人物左转一点试试～
                字数要短，不要超过 15个字
                语气要专业、鼓励、热情。
                """;
    private final ChatClient dashScopeChatClient;

    private final InMemoryChatMemoryRepository chatMemoryRepository = new InMemoryChatMemoryRepository();
    private final int MAX_MESSAGES = 100;
    private final MessageWindowChatMemory messageWindowChatMemory = MessageWindowChatMemory.builder()
            .chatMemoryRepository(chatMemoryRepository)
            .maxMessages(MAX_MESSAGES)
            .build();

    public PhotoAnalysisService(DashScopeChatModel chatModel) {
        this.dashScopeChatClient = ChatClient.builder(chatModel)
                .defaultAdvisors(
                        MessageChatMemoryAdvisor.builder(messageWindowChatMemory)
                                .build()
                )
                .build();
    }

    public ResponseEntity<?> analyzePhoto(String url,String sessionId) throws IOException, URISyntaxException {

        List<Media> mediaList = List.of(new Media(MimeTypeUtils.IMAGE_PNG,
                new URI(url).toURL().toURI()));

        UserMessage message =
                UserMessage.builder().text(promptText).media(mediaList).metadata(new HashMap<>()).build();
        message.getMetadata().put(DashScopeApiConstants.MESSAGE_FORMAT, MessageFormat.IMAGE);

        try {
            ChatResponse response = dashScopeChatClient
                    .prompt(new Prompt(message,
                            DashScopeChatOptions.builder().model("qwen3-vl-flash").multiModel(true).build()))
                    .advisors(
                            a -> a.param(CONVERSATION_ID, sessionId))
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
