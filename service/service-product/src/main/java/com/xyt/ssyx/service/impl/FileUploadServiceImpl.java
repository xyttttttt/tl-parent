package com.xyt.ssyx.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.xyt.ssyx.service.FileUploadService;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.UUID;

@Service
public class FileUploadServiceImpl implements FileUploadService {
    @Value("${aliyun.endpoint}")
    private String endpoint;
    @Value("${aliyun.keyid}")
    private String assessKeyId;
    @Value("${aliyun.keysecret}")
    private String assessKeySecret;
    @Value("${aliyun.bucketname}")
    private String bucketName;

    @Override
    public String uploadFile(MultipartFile file) {
            // 创建OSSClient实例。
            OSS ossClient = new OSSClientBuilder().build(endpoint,assessKeyId,assessKeySecret);
            try {
                InputStream inputStream = file.getInputStream();

                String objectName = file.getOriginalFilename();
                String s = UUID.randomUUID().toString().replaceAll("-","");
                objectName = s + objectName;
                // 创建PutObjectRequest对象。
                // 第一个 bucket 名称
                // 第二个 上传文件路径 + 名称
                // 第三个 文件输入流
                //对文件上传进行分组，根据当前年/月/日
                //objectName: 2023/7/8/uuid01.ipg
                String currentDateTime = new DateTime().toString("yyyy/MM/dd");
                objectName = currentDateTime + "/" +objectName;
                PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, objectName, inputStream);
                putObjectRequest.setProcess("true");
                // 创建PutObject请求。
                PutObjectResult result = ossClient.putObject(putObjectRequest);
                System.out.println(result.getResponse().getStatusCode());
                System.out.println(result.getResponse().getUri());
                System.out.println(result.getResponse().getErrorResponseAsString());
                //返回上传图片在阿里云路径
                String url = result.getResponse().getUri();
                return url;
            }  catch (Exception ce) {
                System.out.println("Caught an ClientException, which means the client encountered "
                        + "a serious internal problem while trying to communicate with OSS, "
                        + "such as not being able to access the network.");
                System.out.println("Error Message:" + ce.getMessage());
            } finally {
                if (ossClient != null) {
                    ossClient.shutdown();
                }
            }
        return null;
    }
}
