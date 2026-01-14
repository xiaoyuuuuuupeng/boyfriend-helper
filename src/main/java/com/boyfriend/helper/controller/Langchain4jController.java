package com.boyfriend.helper.controller;

import com.boyfriend.helper.agents.PhotoAdjustAgent;
import com.boyfriend.helper.agents.PhotoEditAgent;
import com.boyfriend.helper.agents.PhotoPoseAdjustAgent;
import com.boyfriend.helper.common.PromptConstants;
import com.boyfriend.helper.model.AnalysisRequest;
import dev.langchain4j.agentic.UntypedAgent;
import dev.langchain4j.data.message.ImageContent;
import dev.langchain4j.data.message.TextContent;
import dev.langchain4j.data.message.UserMessage;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.response.ChatResponse;
import dev.langchain4j.model.openai.OpenAiChatModel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.Map;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

@Slf4j
@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
public class Langchain4jController {

    private final PhotoPoseAdjustAgent photoPoseAdjustAgent;
    private final ChatModel chatLanguageModel;
    private final UntypedAgent llmPhotoEditAgent;

    /**
     * 健康检查接口
     */
    @PostMapping("/analyzeByUrl")
    public ResponseEntity<?> analyzePhotoByUrl(@RequestParam("url") String url,
                                               @RequestParam("sessionId")String sessionId,
                                               @RequestParam(value = "model", defaultValue = "doubao") String model,
                                               @RequestParam(value = "prompt",defaultValue = PromptConstants.PHOTO_ANALYSIS_PROMPT)
                                               String prompt) {
        if (url.isEmpty()) {
            return ResponseEntity.badRequest().body("请上传一张照片");
        }

        try {
            UserMessage userMessage = UserMessage.from(
                    TextContent.from(PromptConstants.PHOTO_ANALYSIS_PROMPT),
                    ImageContent.from(url)
            );

            ChatResponse chatResponse = chatLanguageModel.chat(userMessage);
            log.info("res:{}",chatResponse.aiMessage().text());

            System.out.println(chatResponse.aiMessage().text());
            return ResponseEntity.ok(chatResponse.aiMessage().text());
        }  catch (Exception e) {
            return ResponseEntity.internalServerError().body("AI服务暂时不可用: " + e.getMessage());
        }
    }

    @PostMapping("/editByUrl")
    public ResponseEntity<?> editByUrl(@RequestParam("url") String url,
                                               @RequestParam("sessionId")String sessionId,
                                               @RequestParam(value = "model", defaultValue = "doubao") String model,
                                               @RequestParam(value = "prompt",defaultValue = PromptConstants.PHOTO_ANALYSIS_PROMPT)
                                               String prompt) {
        if (url.isEmpty()) {
            return ResponseEntity.badRequest().body("请上传一张照片");
        }

        try {

            String edit = (String) llmPhotoEditAgent.invoke(Map.of("url",url));
            log.info("res:{}",edit);
            return ResponseEntity.ok(edit);
        }  catch (Exception e) {
            log.error(e.getMessage(),e);
            return ResponseEntity.internalServerError().body("AI服务暂时不可用: " + e.getMessage());
        }
    }
}
