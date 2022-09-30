package com.sangeng.service.impl;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.sangeng.domain.ResponseResult;
import com.sangeng.enums.AppHttpCodeEnum;
import com.sangeng.exception.SystemException;
import com.sangeng.service.UploadService;
import com.sangeng.utils.PathUtils;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;

@Service("uploadService")
@ConfigurationProperties(prefix = "oss")
@Data
public class UploadServiceImpl implements UploadService {
    //将一对密钥和仓库变量放在成员变量位置，方便通过Lombok提供set方法，进而方便之后读取配置文件对其进行初始化
    private String accessKey;
    private String secretKey;
    private String bucket;
    //仓库的测试地址
    private String location;

    @Override
    public ResponseResult upload(MultipartFile img) {
        //对于前台发送过来的文件进行相应的判断与校验，这里不同的项目有不同的需求，具体根据业务需求来
        //比如这里我们的项目要求上传的文件只能是.png格式，就可以根据此逻辑进行相应校验
        String originalFilename = img.getOriginalFilename();

        if(!originalFilename.endsWith(".png")){
            //抛出我们定义的统一异常，会以我们规定的响应格式返回至前端
            throw new SystemException(AppHttpCodeEnum.FILE_TYPE_ERROR);
        }

        //获取文件上传后的路径，由于代码较多且会多次使用，我们封装到工具类中
        String filePath = PathUtils.generateFilePath(originalFilename);
        //将文件和文件目的路径传入到七牛云提供的API中，实现上传
        String url = uploadOss(img, filePath);
        return ResponseResult.okResult(url);
    }

    /**
     * 调用七牛云OSS的API，实现将文件上传至七牛云
     * @param img 前端传来的文件，用于获取文件流
     * @param filePath 文件在七牛云中的文件路径
     * @return 统一响应格式
     */
    public String uploadOss(MultipartFile img, String filePath){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.autoRegion());
        //...其他参数参考类注释

        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
//        String accessKey = "your access key";
//        String secretKey = "your secret key";
//        String bucket = "sg-blog";

        //默认不指定key的情况下，以文件内容的hash值作为文件名
        //我们需要修改这个对象，不能写死，要不然上传同名的文件会被覆盖
        //在这个项目中我们想要文件最终的存储形式是在日期的三级文件夹下（年/月/日），以UUID为文件名，以文件格式为后缀的文件
        String key = filePath;

        try {
//            byte[] uploadBytes = "hello qiniu cloud".getBytes("utf-8");
//            ByteArrayInputStream byteInputStream=new ByteArrayInputStream(uploadBytes);

            //由于文件不是在服务器端本地，是客户端以流的形式传输过来
            //因此我们可以直接获得传输过来的文件流，之后发送到七牛云OSS
            InputStream inputStream = img.getInputStream();

            //accessKey、secretKey以及bucket具体的值也不会写死，会通过配置文件的方式进行赋值，这样会便于修改与管理
            // 因此在这里需要对这三个对象采用读取配置文件的方式赋值，需要利用@ConfigurationProperties注解
            // 注意：1. 在注解中填入的前缀是在配置文件的前缀，配置文件的配置除去前缀后的key值应当与变量名称相同，这样才会进行同名赋值
            // 2.实现对变量的成功赋值的前提是，这三个变量要有对应的set方法，这样底层会通过反射为其赋值
            // 综上，我们需要把这三个变量放在成员变量位置，并提供对应的set方法
            Auth auth = Auth.create(accessKey, secretKey);
            String upToken = auth.uploadToken(bucket);

            try {
                Response response = uploadManager.put(inputStream,key,upToken,null, null);
                //解析上传成功的结果
                DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
                System.out.println(putRet.key);
                System.out.println(putRet.hash);
                //如果成功了，这里需要返回文件外链的地址
                //文件外链是由仓库中的测试地址+文件名key组成
                //正常测试地址也不能写死，也可以让他读取配置文件
                return location+key;
            } catch (QiniuException ex) {
                Response r = ex.response;
                System.err.println(r.toString());
                try {
                    System.err.println(r.bodyString());
                } catch (QiniuException ex2) {
                    //ignore
                }
            }
        } catch (Exception ex) {
            //ignore
        }
        return null;
    }
}
