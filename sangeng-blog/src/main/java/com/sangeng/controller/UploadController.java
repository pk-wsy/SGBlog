package com.sangeng.controller;

import com.sangeng.domain.ResponseResult;
import com.sangeng.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class UploadController {

    @Autowired
    private UploadService uploadService;
    /**
     * 实现上传头像需求
     * @param img 前端传来的文件，后端需要通过MultipartFile类型接收
     * @return 统一响应格式
     */
    @PostMapping("/upload")
    public ResponseResult upload(@RequestBody MultipartFile img){
        return uploadService.upload(img);
    }
}
