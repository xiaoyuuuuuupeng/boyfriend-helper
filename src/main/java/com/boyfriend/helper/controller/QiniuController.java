package com.boyfriend.helper.controller;

import com.qiniu.util.Auth;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/qiniu")
@RequiredArgsConstructor
public class QiniuController {

    @Value(
            "${qiniu.AK}"
    )
    private String AK;

    @Value(
            "${qiniu.SK}"
    )
    private String SK;

    @Value(
            "${qiniu.bucketName}"
    )
    private String bucketName;


    @RequestMapping("/upToken")
    public ResponseEntity<Map> upToken(@RequestParam(value = "fileName",required = false)String fileName){
        if (fileName == null || fileName.isEmpty()){
            fileName= UUID.randomUUID().toString()+".png";
        }
        Auth auth = Auth.create(AK, SK);
        String upToken = auth.uploadToken(bucketName, fileName);
        return ResponseEntity.ok(Map.of("fileKey",fileName,"token",upToken));
    }
}
