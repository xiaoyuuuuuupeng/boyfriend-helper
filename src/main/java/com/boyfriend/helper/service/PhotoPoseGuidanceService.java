package com.boyfriend.helper.service;

import com.alibaba.cloud.ai.dashscope.image.DashScopeImageModel;
import com.alibaba.cloud.ai.dashscope.image.DashScopeImageOptions;
import com.boyfriend.helper.model.EditPhotoRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.image.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Map;

@Service
@Slf4j
public class PhotoPoseGuidanceService {

    //prompt:
    //  template:
    //    photo-edit:
    @Value("${prompt.template.photo-edit}")
    private String photoEditPromptTemplate;

    private final DashScopeImageModel imageModel;

    public PhotoPoseGuidanceService(DashScopeImageModel imageModel){
        this.imageModel = imageModel;
    }

    public ResponseEntity<?> editPhoto(EditPhotoRequest request) {

        DashScopeImageOptions options = DashScopeImageOptions.builder()
                .height(request.height())
                .width(request.width())
                .baseImageUrl(request.url())
                .n(3)
                .negativePrompt("不显示任何具体的身体部位、面部或道具细节")
                .build();

        log.info("modeName:{}", imageModel.getOptions().getModel());
        try {
            ImageResponse response = imageModel.call(new ImagePrompt(photoEditPromptTemplate, options));
            log.info("response:{}", response);
            log.info("url:{}", response.getResult().getOutput());
            return ResponseEntity.ok(response.getResult().getOutput());
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
