package com.xuecheng.search;

import org.elasticsearch.action.DocWriteResponse;
import org.elasticsearch.action.admin.indices.create.CreateIndexRequest;
import org.elasticsearch.action.admin.indices.create.CreateIndexResponse;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexResponse;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.index.IndexResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.rest.RestStatus;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Liucheng
 * @date 2019/4/22 16:26
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestIndex {

    /**
     * 高级client, 但是功能还不够完善
     */
    @Autowired
    RestHighLevelClient client;

    /**
     * 低级client，完成高级client中还不完善的api
     */
    @Autowired
    RestClient restClient;

    /**
     * 创建索引库
     * @throws Exception
     */
    @Test
    public void testCreateIndex() throws Exception {

        // 创建索引请求对象，并设置索引名称
        CreateIndexRequest createIndexRequest = new CreateIndexRequest("xc_course");

        // 设置索引参数
        createIndexRequest.settings(Settings.builder()
                        .put("number_of_shards", 1)
                        .put("number_of_replicas", 0));

        // 设置映射字段信息
        createIndexRequest.mapping("doc",
                "{ " +
                        " \"properties\":{  " +
                        "   \"name\":{ " +
                        "       \"type\":\"text\", " +
                        "       \"analyzer\":\"ik_max_word\", " +
                        "       \"search_analyzer\":\"ik_smart\" " +
                        "  }, " +
                        "   \"description\":{ " +
                        "       \"type\":\"text\", " +
                        "       \"analyzer\":\"ik_max_word\", " +
                        "       \"search_analyzer\":\"ik_smart\" " +
                        "  }, " +
                        "   \"studymodel\":{ " +
                        "       \"type\":\"keyword\" " +
                        "  }, " +
                        "   \"price\": { " +
                        "       \"type\": \"float\" " +
                        "  }, " +
                        "   \"timestamp\": { " +
                        "       \"type\": \"date\", " +
                        "       \"format\": \"yyyy-MM-dd HH:mm:ss || yyyy-MM-dd || epoch_millis\" " +
                        "  }, " +
                        "   \"pic\":{ " +
                        "       \"type\":\"text\", " +
                        "       \"index\":false " +
                        "  } " +
                        " } " +
                        "}", XContentType.JSON);

        // 创建索引操作客户端
        IndicesClient indices = client.indices();

        // 创建响应对象
        CreateIndexResponse createIndexResponse = indices.create(createIndexRequest);

        // 得到响应结果
        boolean acknowledged = createIndexResponse.isAcknowledged();

        System.out.println(acknowledged);

    }

    /**
     * 删除索引库
     * @throws Exception
     */
    @Test
    public void testDeleteIndex() throws Exception {

        // 删除索引请求对象
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest("xc_course");

        // 删除索引
        DeleteIndexResponse deleteIndexResponse = client.indices().delete(deleteIndexRequest);

        // 获取删除索引响应的结果
        boolean acknowledged = deleteIndexResponse.isAcknowledged();

        System.out.println(acknowledged);
    }

    /**
     * 添加文档
     * @throws Exception
     */
    @Test
    public void testAddDoc() throws Exception {
        // 准备json数据
        Map<String, Object> jsonMap = new HashMap<>();

        jsonMap.put("name", "spring cloud实战");
        jsonMap.put("description", "本课程主要从四个章节进行讲解：1.微服务架构入门 2.spring cloud 基础入门 3.实战Spring Boot 4.注册中心eureka。");
        jsonMap.put("studymodel", "201001");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:22");
        jsonMap.put("timestamp", dateFormat.format(new Date()));
        jsonMap.put("price", 5.6f);
        jsonMap.put("pic", "/group1/M0/D2/ka8f3nawq3r9asdjf003jfa0.png");

        // 索引请求对象
        IndexRequest indexRequest = new IndexRequest("xc_course", "doc");

        // 指定索引文档内容
        indexRequest.source(jsonMap);

        // 索引响应对象
        IndexResponse indexResponse = client.index(indexRequest);

        // 获取响应结果
        DocWriteResponse.Result result = indexResponse.getResult();

        System.out.println(result);
    }

    /**
     * 查询文档
     * @throws Exception
     */
    @Test
    public void getDoc() throws Exception {
        GetRequest getRequest = new GetRequest(
                "xc_course",
                "doc",
                "4vthRGoBvGLYtaMFxndN"
        );

        GetResponse getResponse = client.get(getRequest);
        if (getResponse.isExists()) {
            Map<String, Object> sourceAsMap = getResponse.getSourceAsMap();
            System.out.println(sourceAsMap);
        } else {
            System.out.println("查询结果不存在");
        }
    }

    /**
     * 更新文档，局部更新方法，不会全局替换，会更新索引
     */
    @Test
    public void updateDoc() throws Exception {

        UpdateRequest updateRequest = new UpdateRequest("xc_course", "doc", "4vthRGoBvGLYtaMFxndN");

        Map<String, String> map = new HashMap<>();

        map.put("name", "spring cloud 实战<更新>");

        updateRequest.doc(map);

        UpdateResponse update = client.update(updateRequest);

        RestStatus status = update.status();

        System.out.println(status);
    }

    /**
     * 删除：
     *      搜索匹配删除还没有具体的api,可以采用先搜索出文档id，根据文档id删除
     * @throws Exception
     */
    @Test
    public void testDelDoc() throws Exception {

        // 待删除文档id
        String id = "4vthRGoBvGLYtaMFxndN";

        // 删除索引请求对象
        DeleteRequest deleteRequest = new DeleteRequest("xc_course", "doc", id);

        // 响应对象
        DeleteResponse deleteResponse = client.delete(deleteRequest);

        // 获取响应结果
        DocWriteResponse.Result result = deleteResponse.getResult();

        System.out.println(result);

    }















}
