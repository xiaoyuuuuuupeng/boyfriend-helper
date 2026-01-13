package com.boyfriend.helper.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.MimeType;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PhotoAnalysisService {

    private final ChatClient chatClient;

    public PhotoAnalysisService(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder.build();
    }

    public String analyzePhoto(MultipartFile photo) throws IOException {
        Resource resource = new ByteArrayResource(photo.getBytes());
        MimeType mimeType = MimeTypeUtils.parseMimeType(photo.getContentType());

        String promptText = """
                你是一个专业的摄影师助手，你的任务是帮助那些"不会拍照的男朋友"提高摄影技术。
                请分析这张用户上传的照片：
                1. 构图 (Composition)
                2. 光线 (Lighting)
                3. 角度 (Angle)
                
                请给出3个具体、可操作的建议，告诉他下次如何拍得更好。
                语气要幽默、鼓励，但针针见血。
                """;



        return "";
    }
}
