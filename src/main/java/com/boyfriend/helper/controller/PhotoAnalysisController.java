package com.boyfriend.helper.controller;

import com.boyfriend.helper.service.PhotoAnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/photo")
@CrossOrigin(origins = "*") // Allow access from all clients for now
public class PhotoAnalysisController {

    private final PhotoAnalysisService photoAnalysisService;

    public PhotoAnalysisController(PhotoAnalysisService photoAnalysisService) {
        this.photoAnalysisService = photoAnalysisService;
    }

    @PostMapping("/analyzeByUrl")
    public ResponseEntity<?> analyzePhotoByUrl(@RequestParam("url") String url,
                                               @RequestParam("sessionId")String sessionId) {
        if (url.isEmpty()) {
            return ResponseEntity.badRequest().body("请上传一张照片");
        }

        try {
            return photoAnalysisService.analyzePhoto(url,sessionId);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("处理照片时出错: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("AI服务暂时不可用: " + e.getMessage());
        }
    }
    @PostMapping("/analyze")
    public ResponseEntity<?> analyzePhoto(@RequestParam("file") MultipartFile file,
                                          @RequestParam("sessionId")String sessionId) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("请上传一张照片");
        }

        try {
            return photoAnalysisService.analyzePhoto("",sessionId);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body("处理照片时出错: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("AI服务暂时不可用: " + e.getMessage());
        }
    }
}
