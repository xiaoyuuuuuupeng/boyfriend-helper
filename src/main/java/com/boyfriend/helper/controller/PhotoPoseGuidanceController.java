package com.boyfriend.helper.controller;


import com.boyfriend.helper.service.PhotoAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class PhotoPoseGuidanceController {

    private final PhotoPoseGuidanceService photoPoseGuidanceService;

    public PhotoPoseGuidanceController(PhotoPoseGuidanceService photoPoseGuidanceService) {
        this.photoPoseGuidanceService = photoPoseGuidanceService;
    }

    @PostMapping("/analyze")
    public ResponseEntity<String> analyzePhoto(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请上传一张照片");
        }

        try {
            String analysis = photoAnalysisService.analyzePhoto(file);
            return ResponseEntity.ok(analysis);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("处理照片时出错: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("AI服务暂时不可用: " + e.getMessage());
        }
    }
}
