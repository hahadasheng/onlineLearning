package com.xuecheng.filesystem.service;

import com.alibaba.fastjson.JSON;
import com.xuecheng.filesystem.dao.FileSystemRepository;
import com.xuecheng.framework.domain.filesystem.FileSystem;
import com.xuecheng.framework.domain.filesystem.response.FileSystemCode;
import com.xuecheng.framework.domain.filesystem.response.UploadFileResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import org.apache.commons.lang3.StringUtils;
import org.csource.fastdfs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 文件上传服务
 * @author Liucheng
 * @date 2019/4/19 21:10
 */
@Service
public class FileSystemService {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileSystemService.class);

    @Value("${xuecheng.fastdfs.tracker_servers}")
    String trackerServers;

    @Value("${xuecheng.fastdfs.connect_timeout_in_seconds}")
    int connectTimeoutInSeconds;

    @Value("${xuecheng.fastdfs.network_timeout_in_seconds}")
    int networkTimeoutInSeconds;

    @Value("${xuecheng.fastdfs.charset}")
    String charset;

    @Autowired
    FileSystemRepository fileSystemRepository;

    /**
     * 加载fdfs的配置
     */
    private void initFastDfsdConfig() {
        try {
            ClientGlobal.initByTrackers(trackerServers);
            ClientGlobal.setG_connect_timeout(connectTimeoutInSeconds);
            ClientGlobal.setG_network_timeout(networkTimeoutInSeconds);
            ClientGlobal.setG_charset(charset);
        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_INITFDFSERROR);
        }
    }

    /**
     * 上传文件到fastDFS
     * @param file
     * @return
     */
    public String fastDfsUpload(MultipartFile file) {
        try {
            // 加载fastDFS的配置
            initFastDfsdConfig();

            // 创建tracker client 用于获取 tracker/storage 的 server
            TrackerClient trackerClient = new TrackerClient();

            // 获取trackerServer
            TrackerServer trackerServer = trackerClient.getConnection();

            // 获取storage server
            StorageServer storageServer = trackerClient.getStoreStorage(trackerServer);

            // 创建storage client
            StorageClient storageClient = new StorageClient(trackerServer, storageServer);

            // 获取文件字节数据
            byte[] fileBytes = file.getBytes();

            // 获取文件原始名
            String originalFilename = file.getOriginalFilename();

            // 获取文件扩展名
            String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);

            // 返回文件id
            String[] strings = storageClient.upload_file(fileBytes, extName, null);

            return strings[0] + "/" + strings[1];

        } catch (Exception e) {
            e.printStackTrace();
            ExceptionCast.cast(FileSystemCode.FS_UPLOADE_FAILED);
        }

        return null;
    }

    /**
     * 上传文件服务
     * @param file 原始文件对象
     * @param filetag 标识文件所属的服务
     * @param businessKey 标识文件业务相关
     * @param metadata 文件元信息
     * @return
     */
    public UploadFileResult upload(
            MultipartFile file,
            String filetag,
            String businessKey,
            String metadata) {

        // 如果文件不存在，抛出异常
        if (file == null) {
            ExceptionCast.cast(FileSystemCode.FS_UPLOADFILE_FILEISNULL);
        }

        // 上传文件到fdfs
        String fileId = fastDfsUpload(file);

        // 创建文件信息对象
        FileSystem fileSystem = new FileSystem();
        // 文件id
        fileSystem.setFileId(fileId);
        // 文件在文件系统中的路径
        fileSystem.setFilePath(fileId);
        // 业务标识
        fileSystem.setBusinesskey(businessKey);
        // 标签
        fileSystem.setFiletag(filetag);
        // 元数据
        if (StringUtils.isNotEmpty(metadata)) {
            try {
                Map map = JSON.parseObject(metadata, Map.class);
                fileSystem.setMetadata(map);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        // 名称
        fileSystem.setFileName(file.getOriginalFilename());
        // 大小
        fileSystem.setFileSize(file.getSize());
        // 文件类型
        fileSystem.setFileType(file.getContentType());

        // 将文件信息保存到mongodb数据库中
        fileSystemRepository.save(fileSystem);

        // 返回信息
        return new UploadFileResult(CommonCode.SUCCESS, fileSystem);
    }
}
