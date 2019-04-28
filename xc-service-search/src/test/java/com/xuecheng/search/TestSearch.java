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
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.action.update.UpdateResponse;
import org.elasticsearch.client.IndicesClient;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.*;
import org.elasticsearch.rest.RestStatus;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.elasticsearch.search.sort.SortOrder;
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
 *
 * DSL搜索：
 *   DSL(Domain Specific Language)是ES提出的基于json的搜索方式，在搜索时传入特定的json格式的数据来完成不
 *   同的搜索需求。DSL比URI搜索方式功能强大，在项目中建议使用DSL方式来完成搜索。
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TestSearch {

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
     * 搜索type下的全部记录
     * @throws Exception
     */
    @Test
    public void testSearchAll() throws Exception {

        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 搜索方式
        // 搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});

        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！

            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println(name + "\n" + studymodel + "\n" + price + "\n" + timestamp);

        }
    }

    /**
     * 分页查询
     * ES支持分页查询，传入两个参数：from和size。
     *      form：表示起始文档的下标，从0开始。
     *      size：查询的文档数量。
     * @throws Exception
     */
    @Test
    public void testSearchPage() throws Exception {

        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 设置分页参数
        int page = 2;
        int size = 1;

        // 起始下标，从0开始
        searchSourceBuilder.from((page - 1) * size);
        // 每页显示的记录条数
        searchSourceBuilder.size(size);

        // 搜索方式
        // 搜索全部
        searchSourceBuilder.query(QueryBuilders.matchAllQuery());

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});

        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！

            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println(name + "\n" + studymodel + "\n" + price + "\n" + timestamp);

        }
    }

    /**
     * Term Query
     *  Term Query为精确查询，在搜索时会整体匹配关键字，不再将关键字分词。
     *  搜索的整体 与 某个分词完全相同时则匹配，对应的源字段内容包含搜索的整体(而不是相等关系)
     * @throws Exception
     */
    @Test
    public void testTermQuery() throws Exception {
        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 搜索方式
        // termQuery 精确查询方式
        searchSourceBuilder.query(QueryBuilders.termQuery("name", "spring"));

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});

        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！

            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();
            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println(name + "\n" + studymodel + "\n" + price + "\n" + timestamp);

        }
    }

    /**
     * Id Query
     *      ES提供根据多个id值匹配的方法：
     * @throws Exception
     */
    @Test
    public void testIdQuery() throws Exception {
        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 搜索方式
        // id 精确查询方式
        // 定义id列表
        String[] ids = new String[]{"1", "2"};
        searchSourceBuilder.query(QueryBuilders.termsQuery("_id", ids));

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp"}, new String[]{});

        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！

            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println(name + "\n" + studymodel + "\n" + price + "\n" + timestamp);
        }
    }

    /**
     * Match Query
     *  match Query即全文检索，它的搜索方式是先将搜索字符串分词，再使用各各词条从索引中搜索。
     *  "operator" : "or" 或者 是 "and" 表示各个分词之间是或者关系还是同时满足的关系
     *  "minimum_should_match": "80%" 表示查询的结果包含分词的比例，向下取整数；比如查询的分词结果为3，设置为80%; 匹配的域中必须包含3 * 0.8 = 2.4 = 2 个分词的关键字
     *
     * @throws Exception
     */
    @Test
    public void testMatchQuery() throws Exception {
        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 搜索方式
        // MatchQuery
        /*
        searchSourceBuilder.query(QueryBuilders.matchQuery("description", "spring开发框架")
                .minimumShouldMatch("80%"));
        */

         searchSourceBuilder.query(QueryBuilders.matchQuery("description", "java第一")
                .operator(Operator.AND));

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！

            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println(name + "\n" + studymodel + "\n" + price + "\n" + timestamp+ "\n" + description);
        }
    }


    /**
     * MultiMatch Query 【实际开发中的常客】
     *   可以在域名城后面指定权重，对应的得分会乘上对应的权重
     * @throws Exception
     */
    @Test
    public void testMultiMatchQuery() throws Exception {
        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 搜索方式
        // MultiMatchQuery
        //    minimumShouldMatch 指定至少包含的分词关键字
        //    field 设置权重
        searchSourceBuilder.query(QueryBuilders
                .multiMatchQuery("spring java","name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10));

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！

            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println(name + "\n" + studymodel + "\n" + price + "\n" + timestamp+ "\n" + description);
        }
    }

    /**
     * Bool Query
     * 布尔查询对应于Lucene的BooleanQuery查询，实现将多个查询组合起来。
     * 三个参数：
     *  must：文档必须匹配must所包括的查询条件，相当于 “AND”
     *  should：文档应该匹配should所包括的查询条件其中的一个或多个，相当于 "OR"
     *  must_not：文档不能匹配must_not所包括的该查询条件，相当于“NOT”
     * @throws Exception
     */
    @Test
    public void testBoolQuery() throws Exception {
        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 搜索方式
        // Bool Query查询方式

        // 1. 先定义一个MultiMatchQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring java", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);

        // 2. 再定义一个termQuery
        TermQueryBuilder termQueryBuilder = QueryBuilders.termQuery("studymodel", "201001");

        // 3. 定义个boolQuery构建者
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);
        boolQueryBuilder.must(termQueryBuilder);

        searchSourceBuilder.query(boolQueryBuilder);

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！

            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println(name + "\n" + studymodel + "\n" + price + "\n" + timestamp+ "\n" + description);
        }
    }


    /**
     * 过滤器 【过虑器在布尔查询中使用】
     * 过虑是针对搜索的结果进行过虑，过虑器主要判断的是文档是否匹配，不去计算和判断文档的匹配度得分，所以过
     * 虑器性能比查询要高，且方便缓存，推荐尽量使用过虑器去实现查询或者过虑器和查询共同使用。
     *
     * range：范围过虑，保留大于等于60 并且小于等于100的记录。
     * term ：项匹配过虑，保留studymodel等于"201001"的记录。
     * 注意：range和term一次只能对一个Field设置范围过虑。
     *
     * @throws Exception
     */
    @Test
    public void testBoolFilterQuery() throws Exception {
        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 搜索方式
        // Bool Query查询方式

        // 1. 先定义一个MultiMatchQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring java", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);

        // 2. 定义个boolQuery构建者
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        // 3. 定义过滤器
        boolQueryBuilder.filter(QueryBuilders.termQuery("studymodel", "201001"));
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(80).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！

            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println(name + "\n" + studymodel + "\n" + price + "\n" + timestamp+ "\n" + description);
        }
    }


    /**
     * 排序
     * 可以在字段上添加一个或多个排序，支持在keyword、date、float等类型上添加，text类型的字段上不允许添加排序。
     * @throws Exception
     */
    @Test
    public void testSortQuery() throws Exception {
        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 搜索方式
        // Bool Query查询方式

        // 1. 先定义一个MultiMatchQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring java", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);

        // 2. 定义个boolQuery构建者
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        // 3. 定义过滤器
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));

        searchSourceBuilder.query(boolQueryBuilder);

        // 【添加排序】
        searchSourceBuilder.sort("studymodel", SortOrder.ASC);
        searchSourceBuilder.sort("price", SortOrder.DESC);

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});

        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！

            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            String name = (String) sourceAsMap.get("name");
            String studymodel = (String) sourceAsMap.get("studymodel");
            String description = (String) sourceAsMap.get("description");
            Double price = (Double) sourceAsMap.get("price");
            Date timestamp = dateFormat.parse((String) sourceAsMap.get("timestamp"));

            System.out.println("~~~~~~~~~~~~~~~~~");
            System.out.println(name + "\n" + studymodel + "\n" + price + "\n" + timestamp+ "\n" + description);
        }
    }

    /**
     * 高亮查询
     * 高亮显示可以将搜索结果一个或多个字突出显示，以便向用户展示匹配关键字的位置。
     * @throws Exception
     */
    @Test
    public void testHighlightQuery() throws Exception {
        // 设置要查询的索引库
        SearchRequest searchRequest = new SearchRequest("xc_course");

        // type设置（逐渐被弱化）
        searchRequest.types("doc");

        // 搜索源构建对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // 设置分页参数
        int page = 1;
        int size = 10;

        // 起始下标，从0开始
        searchSourceBuilder.from((page - 1) * size);
        // 每页显示的记录条数
        searchSourceBuilder.size(size);


        // 搜索方式
        // Bool Query查询方式

        // 1. 先定义一个MultiMatchQuery
        MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders
                .multiMatchQuery("spring java", "name", "description")
                .minimumShouldMatch("50%")
                .field("name", 10);

        // 2. 定义个boolQuery构建者
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
        boolQueryBuilder.must(multiMatchQueryBuilder);

        // 3. 定义过滤器
        boolQueryBuilder.filter(QueryBuilders.rangeQuery("price").gte(0).lte(100));
        searchSourceBuilder.query(boolQueryBuilder);

        // 【添加排序】
        searchSourceBuilder.sort("studymodel", SortOrder.ASC);
        searchSourceBuilder.sort("price", SortOrder.DESC);

        // 设置源字段过滤，第一个参数结果集包括哪些字段，第二个参数表示结果集不包括哪些字段，
        searchSourceBuilder.fetchSource(new String[]{"name", "studymodel", "price", "timestamp", "description"}, new String[]{});


        // 【设置高亮】
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span>");
        highlightBuilder.postTags("</span style:\"color:red\">");
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));
        highlightBuilder.fields().add(new HighlightBuilder.Field("description"));

        // 将高亮构建者交给搜索构建者
        searchSourceBuilder.highlighter(highlightBuilder);



        // 向搜索请求对象中设置搜索源
        searchRequest.source(searchSourceBuilder);

        // 执行搜索，向ES发起http请求
        SearchResponse searchResponse = client.search(searchRequest);

        // 搜索结果
        SearchHits hits = searchResponse.getHits();

        // 匹配到的总记录数
        long totalHits = hits.getTotalHits();

        // 得到匹配度高的文档
        SearchHit[] searchHits = hits.getHits();

        System.out.println("总记录数：" + totalHits);
        System.out.println("匹配度高的数：" + searchHits.length);

        // 日期格式化对象
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        for (SearchHit hit : searchHits) {
            // 不能获取被过滤掉的字段信息！
            // 文档的主键(主键不属于文档内的内容字段)
            String id = hit.getId();

            // 源文档内容
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            String name = (String) sourceAsMap.get("name");
            String description = (String) sourceAsMap.get("description");

            System.out.println("~~~~~~~~源文件内容~~~~~~~~~");
            System.out.println("id: " + id + "\n" + name + "\n" + description + "\n");

            System.out.println("~~~~~~~~高亮内容内容~~~~~~~~~");

            // 取出高亮内容
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null) {

                // 取出name高亮字段
                HighlightField nameHighlightField = highlightFields.get("name");
                if (nameHighlightField != null) {
                    Text[] nameFragments = nameHighlightField.getFragments();
                    StringBuilder sbName = new StringBuilder();
                    for (Text nameText : nameFragments) {
                        sbName.append(nameText);
                    }

                    System.out.println("nameHighlight: " + sbName.toString());
                }

                // 取出description中高亮的字段
                HighlightField descriptionHighlightField = highlightFields.get("description");
                if (descriptionHighlightField != null) {
                    Text[] descriptionFragments = descriptionHighlightField.getFragments();
                    StringBuilder sbDescription = new StringBuilder();
                    for (Text descriptionText : descriptionFragments) {
                        sbDescription.append(descriptionText);
                    }
                    System.out.println("descriptionHighlight: " + sbDescription.toString());
                }
            }
        }
    }
}
