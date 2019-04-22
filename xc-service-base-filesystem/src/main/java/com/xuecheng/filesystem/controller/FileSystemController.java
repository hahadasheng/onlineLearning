package com.xuecheng.filesystem.controller;

import com.xuecheng.api.filesystem.FileSystemControllerApi;
import com.xuecheng.filesystem.service.FileSystemService;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 文件上传服务
 * @author Liucheng
 * @date 2019/4/19 23:22
 */
@RestController
@RequestMapping("/filesystem")
public class FileSystemController implements FileSystemControllerApi {

    @Autowired
    FileSystemService fileSystemService;
    /**
     *
     * @param multipartFile 文件
     * @param filetage 文件标签，用于标识来自哪个服务
     * @param businesskey 业务key，比如课程id相关
     * @param metadata 文件元信息，json格式
     * @return
     */
    @Override
    @PostMapping("/upload")
    public UploadFileResult upload(MultipartFile multipartFile, String filetage, String businesskey, String metadata) {
        return fileSystemService.upload(multipartFile, filetage, businesskey, metadata);
    }
}
