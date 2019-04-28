package com.xuecheng.search.service;

import com.xuecheng.framework.domain.course.CoursePub;
import com.xuecheng.framework.domain.search.CourseSearchParam;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author Liucheng
 * @date 2019/4/23 19:42
 */
@Service
public class EsCourseService {

    /**
     * 日志配置
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(EsCourseService.class);

    /**
     * 需要被依赖注入的值
     */
    @Value("${xuecheng.elasticsearch.course.index}")
    private String es_index;

    @Value("${xuecheng.elasticsearch.course.type}")
    private String es_type;

    @Value("${xuecheng.elasticsearch.course.source_field}")
    private String source_field;

    @Autowired
    private RestHighLevelClient client;

    /**
     *
     * @param page
     * @param size
     * @param courseSearchParam
     * @return
     */
    public QueryResponseResult findList(int page, int size, CourseSearchParam courseSearchParam) {

        // 设置索引
        SearchRequest searchRequest = new SearchRequest(es_index);

        // 设置类型
        searchRequest.types(es_type);

        // 创建搜索构建者对象
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

        // source源字段过滤
        String[] sourceFields = source_field.split(",");
        // 第一个参数为需要的字段，第二个参数为不需要的字段
        searchSourceBuilder.fetchSource(sourceFields, new String[]{});

        // 构建布尔查询构建者对象
        BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();

        // 【关键字】
        if (StringUtils.isNotEmpty(courseSearchParam.getKeyword())) {
            // 匹配关键字,【可以抽离到配置文件中】
            String[] s = {"name", "teachplan", "description"};
            MultiMatchQueryBuilder multiMatchQueryBuilder = QueryBuilders.multiMatchQuery(courseSearchParam.getKeyword(), s);

            // 设置匹配占比
            multiMatchQueryBuilder.minimumShouldMatch("70%");

            // 提升另外两个字段的Boost值
            multiMatchQueryBuilder.field("name", 10);

            // 布尔查询设置
            boolQueryBuilder.must(multiMatchQueryBuilder);
        }

        // 【设置过滤器】
        // 一级分类
        if (StringUtils.isNotEmpty(courseSearchParam.getMt())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("mt", courseSearchParam.getMt()));
        }
        // 二级分类
        if (StringUtils.isNotEmpty(courseSearchParam.getSt())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("st", courseSearchParam.getSt()));
        }
        // 难度等级
        if (StringUtils.isNotEmpty(courseSearchParam.getGrade())) {
            boolQueryBuilder.filter(QueryBuilders.termQuery("grade", courseSearchParam.getGrade()));
        }

        // 装配布尔查询
        searchSourceBuilder.query(boolQueryBuilder);

        // 【分页】
        if (page <= 0) {
            page = 1;
        }

        if (size <= 0) {
            size = 20;
        }

        int start = (page - 1) * size;
        searchSourceBuilder.from(start);
        searchSourceBuilder.size(size);

        // 【高亮设置】
        HighlightBuilder highlightBuilder = new HighlightBuilder();
        highlightBuilder.preTags("<span>");
        highlightBuilder.postTags("</span style=\"color:red\">");
        // 设置高亮的字段
        highlightBuilder.fields().add(new HighlightBuilder.Field("name"));

        // 装配高亮设置
        searchSourceBuilder.highlighter(highlightBuilder);

        // 装配最终搜索条件
        searchRequest.source(searchSourceBuilder);
        SearchResponse searchResponse = null;

        try {
            searchResponse = client.search(searchRequest);
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error("xuecheng search error..{}",e.getMessage());
            return new QueryResponseResult(CommonCode.FAIL, new QueryResult<CoursePub>());
        }

        // 结果集处理
        SearchHits hits = searchResponse.getHits();
        SearchHit[] searchHits = hits.getHits();

        // 总记录数
        long totalHits = hits.getTotalHits();

        // 数据列表
        List<CoursePub> list = new ArrayList<>();

        for (SearchHit hit : searchHits) {

            CoursePub coursePub = new CoursePub();

            // 取出源 source
            Map<String, Object> sourceAsMap = hit.getSourceAsMap();

            // 【取出高亮字段内容】
            // 取出名称
            coursePub.setName(null);
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            if (highlightFields != null) {
                HighlightField nameField = highlightFields.get("name");
                if (nameField != null) {
                    Text[] nameFragments = nameField.getFragments();
                    StringBuilder sbName = new StringBuilder();
                    for (Text everyName : nameFragments) {
                        sbName.append(everyName.string());
                    }
                    String name = sbName.toString();
                    coursePub.setName(name);
                }
            }

            // 没有高亮的name
            if (coursePub.getName() == null) {
                String name = (String) sourceAsMap.get("name");
                coursePub.setName(name);
            }

            // 图片
            String pic = (String) sourceAsMap.get("pic");
            coursePub.setPic(pic);

            // 价格
            Double price = null;
            try {

                if (sourceAsMap.get("price") != null) {
                    price = (Double) sourceAsMap.get("price");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            coursePub.setPrice(price);

            Double price_old = null;
            try {

                if (sourceAsMap.get("price_old") != null) {
                    price_old = (Double) sourceAsMap.get("price_old");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            coursePub.setPrice_old(price_old);

            list.add(coursePub);
        }

        QueryResult<CoursePub> queryResult = new QueryResult<CoursePub>();
        queryResult.setList(list);
        queryResult.setTotal(totalHits);
        queryResult.setPage(page);

        return new QueryResponseResult(CommonCode.SUCCESS, queryResult);
    }
}
