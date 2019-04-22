package com.xuecheng.api.filesystem;

import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author Liucheng
 * @date 2019/4/19 21:03
 */
public interface FileSystemControllerApi {

    /**
     * 长传文件
     * @param multipartFile 文件
     * @param filetage 文件标签，用于标识来自哪个服务
     * @param businesskey 业务key，比如课程id相关
     * @param metadata 文件元信息，json格式
     * @return
     */
    public UploadFileResult upload(
            MultipartFile multipartFile,
            String filetage,
            String businesskey,
            String metadata
    );
}
