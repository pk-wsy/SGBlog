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
     * 实现将图片上传的功能
     * 在写博文栏目下，点击上传图片可以将图片上传至七牛云OSS，并返回图片在OSS中的外链
     * 实现写博文功能的第三个接口
     * @param img
     * @return
     */
    @PostMapping("/upload")
    public ResponseResult uploadImg(@RequestBody MultipartFile img){
        try {
            return uploadService.upload(img);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传上传失败");
        }
    }
}
