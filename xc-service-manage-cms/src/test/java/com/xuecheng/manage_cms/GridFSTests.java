package com.xuecheng.manage_cms;

import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GridFSTests {

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    /**
     * 向mongodb中存储文件
     *
     */
    @Test
    public void gridFsTest() throws Exception {
        // 要存储的文件
        File file = new File("E:\\ALearnCityProject\\ServiceWorkSpace\\xc-test-freemarker\\src\\main\\resources\\templates\\index_banner.ftl");

        // 定义输入流
        FileInputStream inputStream = new FileInputStream(file);

        // 向GridFS存储文件
        ObjectId objectId = gridFsTemplate.store(inputStream, "index_banner.ftl");

        // 得到文件ID
        String fileId = objectId.toString();
        System.out.println(fileId);
        // 5cb29994d8ec453054127992
    }

    /**
     * 从mongodb中查询并下载文件
     * 5cb28e19d8ec4501881b9fb5
     */
    @Test
    public void downloadFile() throws Exception {

        // 文件 _id Object类型
        String fileId = "5cb28e19d8ec4501881b9fb5";

        // 根据id查询文件
        GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(fileId)));

        // 打开下载流对象
        GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

        // 创建gridFsResource，用于获取流对象
        GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

        // 获取流中的数据并输出到文件中
        FileOutputStream fileOutputStream = new FileOutputStream(new File("E:\\ALearnCityProject\\ServiceWorkSpace\\xc-service-manage-cms\\src\\main\\resources\\files\\index_banner.ftl"));
        int copy = IOUtils.copy(gridFsResource.getInputStream(), fileOutputStream);
        System.out.println(copy);
    }

    /**
     * 删除文件
     */
    @Test
    public void deleteTest() throws Exception {
        // 根据文件id删除fs.files和fs.chunks中的记录
        gridFsTemplate.delete(Query.query(Criteria.where("_id").is("5cb29994d8ec453054127992")));
    }


}
