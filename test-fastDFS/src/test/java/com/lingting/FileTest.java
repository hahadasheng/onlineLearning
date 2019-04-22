package com.lingting;

import org.csource.fastdfs.*;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

/**
 * @author Liucheng
 * @date 2019/4/18 18:58
 */
public class FileTest {

    /**
     * 上传文件测试
     * @throws Exception
     */
    @Test
    public void testUpload() throws Exception {

        // 1. 加载配置文件，配置文件的内容就是tracker服务的地址
        ClientGlobal.initByProperties("config/fastdfs-client.properties");

        // 2. 创建一个TrackerClient对象。
        TrackerClient tc = new TrackerClient();

        // 3. 使用TrackerClient对象创建连接，获得一个TrackServer对象
        TrackerServer ts = tc.getConnection();

        // 4. 创建一个StorageServer的引用，值为null
        StorageServer ss = tc.getStoreStorage(ts);

        // 5、创建一个 StorageClient 对象，需要两个参数 TrackerServer 对象、StorageServer 的引用
        StorageClient sc = new StorageClient(ts, ss);

        // 6、使用 StorageClient 对象上传图片。扩展名不带“.”

        String[] strings = sc.upload_file("G:\\zhizi.jpg", "jpg", null);

        // 7、返回数组。包含组名和图片的路径。
        for (String string : strings) {
            System.out.println(string);
        }
        /**
         * group1/M00/00/00/wKgZhVy4W7WAerp1AAje9oEw8TM980.jpg
         * http://192.168.25.133/group1/M00/00/00/wKgZhVy4W7WAerp1AAje9oEw8TM980.jpg
         */
    }

    /**
     * 测试查询
     * @throws Exception
     */
    @Test
    public void testQueryFile() throws Exception {
        ClientGlobal.initByProperties("config/fastdfs-client.properties");

        TrackerClient tracker = new TrackerClient();
        TrackerServer trackerServer = tracker.getConnection();
        StorageServer storageServer = null;

        StorageClient storageClient = new StorageClient(trackerServer, storageServer);
        FileInfo fileInfo = storageClient.query_file_info("group1", "M00/00/00/wKgZhVy4W7WAerp1AAje9oEw8TM980.jpg");

        System.out.println(fileInfo);

    }

    /**
     * 测试下载文件
     * @throws Exception
     */
    @Test
    public void testDownloadFile() throws Exception {

        ClientGlobal.initByProperties("config/fastdfs-client.properties");

        TrackerClient tracker = new TrackerClient();

        TrackerServer trackerServer = tracker.getConnection();

        StorageServer storageServer = null;

        StorageClient storageClient = new StorageClient(trackerServer, storageServer);

        byte[] result = storageClient.download_file("group1", "M00/00/00/wKgZhVy4W7WAerp1AAje9oEw8TM980.jpg");

        FileOutputStream fileOutputStream = new FileOutputStream(new File("g:\\zhizi1.jpg"));
        fileOutputStream.write(result);
        fileOutputStream.close();
    }
}
