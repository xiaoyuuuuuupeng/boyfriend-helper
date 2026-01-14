package com.boyfriend.helper.controller;


import com.boyfriend.helper.common.PromptConstants;
import com.boyfriend.helper.model.EditPhotoRequest;
import com.boyfriend.helper.service.PhotoAnalysisService;
import com.boyfriend.helper.service.PhotoPoseGuidanceService;
import com.boyfriend.helper.service.PhotoUploadService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/photo")
@CrossOrigin(origins = "*")
public class PhotoPoseGuidanceController {

    private final PhotoPoseGuidanceService photoPoseGuidanceService;
    private final PhotoUploadService photoUploadService;

    public PhotoPoseGuidanceController(PhotoPoseGuidanceService photoPoseGuidanceService, PhotoUploadService photoUploadService) {
        this.photoPoseGuidanceService = photoPoseGuidanceService;
        this.photoUploadService = photoUploadService;
    }

//    @PostMapping("/edit")
//    public ResponseEntity<?> editPhoto(@RequestParam("file") MultipartFile file,
//                                       @RequestParam(value = "height", defaultValue = "1024") Integer height,
//                                       @RequestParam(value = "width", defaultValue = "1024") Integer width,
//                                       @RequestParam(value = "model", defaultValue = "doubao") String model) {
//        if (file.isEmpty()) {
//            return ResponseEntity.badRequest().body("请上传一张照片");
//        }
//
//        //upload cdn first
//        String url = photoUploadService.upload(file);
//        EditPhotoRequest request = new EditPhotoRequest(height, width, url, model);
//        return photoPoseGuidanceService.editPhoto(request);
//    }


    @PostMapping("/editByUrl")
    public ResponseEntity<?> editByUrl(@RequestParam("url") String url,
                                       @RequestParam(value = "height", defaultValue = "1024") Integer height,
                                       @RequestParam(value = "width", defaultValue = "1024") Integer width,
                                       @RequestParam(value = "model", defaultValue = "doubao") String model,
                                       @RequestParam(value = "prompt",defaultValue = PromptConstants.PHOTO_EDIT_PROMPT)
                                           String prompt) {
        if (url.isEmpty()) {
            return ResponseEntity.badRequest().body("请上传一张照片");
        }
        EditPhotoRequest request = new EditPhotoRequest(height, width, url, model,prompt);
        return photoPoseGuidanceService.editPhoto(request);
    }
}
