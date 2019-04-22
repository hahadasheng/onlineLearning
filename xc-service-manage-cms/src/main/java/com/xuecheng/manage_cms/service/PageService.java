package com.xuecheng.manage_cms.service;

import com.alibaba.fastjson.JSON;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import com.xuecheng.framework.domain.cms.CmsPage;
import com.xuecheng.framework.domain.cms.CmsSite;
import com.xuecheng.framework.domain.cms.CmsTemplate;
import com.xuecheng.framework.domain.cms.request.QueryPageRequest;
import com.xuecheng.framework.domain.cms.response.CmsCode;
import com.xuecheng.framework.domain.cms.response.CmsPageResult;
import com.xuecheng.framework.domain.cms.response.CmsPostPageResult;
import com.xuecheng.framework.exception.ExceptionCast;
import com.xuecheng.framework.model.response.CommonCode;
import com.xuecheng.framework.model.response.QueryResponseResult;
import com.xuecheng.framework.model.response.QueryResult;
import com.xuecheng.framework.model.response.ResponseResult;
import com.xuecheng.manage_cms.config.RabbitmqConfig;
import com.xuecheng.manage_cms.dao.CmsPageRepository;
import com.xuecheng.manage_cms.dao.CmsTemplateRepository;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsResource;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PageService {

    @Autowired
    private CmsPageRepository cmsPageRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private CmsTemplateRepository cmsTemplateRepository;

    @Autowired
    private GridFsTemplate gridFsTemplate;

    @Autowired
    private GridFSBucket gridFSBucket;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private SiteService siteService;

    /**
     * 页面列表分页查询
     * @param page 当前页码，从1开始
     * @param size 页面显示的条数
     * @param queryPageRequest 查询条件
     * @return 页面列表
     */
    public QueryResponseResult findList(int page, int size, QueryPageRequest queryPageRequest) {

        if (queryPageRequest == null) {
            queryPageRequest = new QueryPageRequest();
        }

        if (page <= 0) {
            page = 1;
        }

        // 为了适应mongodb的接口，将页码减1
        page = page - 1;

        if (size <= 0) {
            size = 20;
        }

        // 条件值
        CmsPage cmsPage = new CmsPage();

        // 站点Id
        if(StringUtils.isNotEmpty(queryPageRequest.getSiteId())) {
            cmsPage.setSiteId(queryPageRequest.getSiteId());
        }

        // 页面别名
        if (StringUtils.isNotEmpty(queryPageRequest.getPageAliase())) {
            cmsPage.setPageAliase(queryPageRequest.getPageAliase());
        }

        // 条件匹配器
        // 页面名称模糊查询，需要自定义字符串的匹配器实现模糊查询
        ExampleMatcher exampleMatcher = ExampleMatcher.matching()
                .withMatcher("pageAliase", ExampleMatcher.GenericPropertyMatchers.contains());

        // 创建条件实例
        Example<CmsPage> example = Example.of(cmsPage, exampleMatcher);

        // 分页对象
        Pageable pageable = new PageRequest(page, size);

        // 分页查询
        Page<CmsPage> all = cmsPageRepository.findAll(example, pageable);

        // 封装返回对象
        QueryResult<CmsPage> cmsPageQueryResult = new QueryResult<CmsPage>();
        cmsPageQueryResult.setList(all.getContent());
        cmsPageQueryResult.setTotal(all.getTotalElements());

        return new QueryResponseResult(CommonCode.SUCCESS, cmsPageQueryResult);
    }

    /**
     * 添加页面
     * @param cmsPage
     * @return
     */
    public CmsPageResult add(CmsPage cmsPage) {

        if (cmsPage == null) {
            // 抛出异常，非法参数异常，指定异常信息的内容
            ExceptionCast.cast(CommonCode.SERVER_ERROR);
        }

        // 校验页面是否存在，根据页面名称、站点id,页面webpath查询
        CmsPage cmsPage1 = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(
                cmsPage.getPageName(),
                cmsPage.getSiteId(),
                cmsPage.getPageWebPath()
        );

        if (cmsPage1 != null) {
            // 页面已经存在，抛出异常！
            ExceptionCast.cast(CmsCode.CMS_ADDPAGE_EXISTSNAME);
        }

        // 添加的主键由spring data mongodb自动生成
        cmsPage.setPageId(null);
        cmsPageRepository.save(cmsPage);

        // 返回结果
        CmsPageResult cmsPageResult = new CmsPageResult(CommonCode.SUCCESS, cmsPage);

        return cmsPageResult;

    }

    /**
     * 根据id 查询页面
     * @param id
     * @return
     */
    public CmsPage getById(String id) {
        Optional<CmsPage> byId = cmsPageRepository.findById(id);

        if (byId.isPresent()) {
            return byId.get();
        }

        return null;
    }

    /**
     *跟新页面信息： 需要防止一些关键信息被修改
     * @param id
     * @param cmsPage
     * @return
     */
    public CmsPageResult update(String id, CmsPage cmsPage) {
        // 根据id 查询页面信息
        CmsPage one = this.getById(id);

        if (one != null) {
            // 更新模板id
            one.setTemplateId(cmsPage.getTemplateId());

            // 更新所属站点
            one.setSiteId(cmsPage.getSiteId());

            // 更新页面别名
            one.setPageAliase(cmsPage.getPageAliase());

            // 更新页面名称
            one.setPageName(cmsPage.getPageName());

            // 跟新访问路径
            one.setPageWebPath(cmsPage.getPageWebPath());

            // 更新物理路径
            one.setPagePhysicalPath(cmsPage.getPagePhysicalPath());

            // 更新数据获取路径
            one.setDataUrl(cmsPage.getDataUrl());

            // 执行更新
            CmsPage save = cmsPageRepository.save(one);

            if (save != null) {
                // 返回成功信息
                return new CmsPageResult(CommonCode.SUCCESS, save);
            }

        }

        return new CmsPageResult(CommonCode.FAIL, null);
    }

    /**
     * 通过ID删除页面
     * @param id
     * @return
     */
    public ResponseResult delete(String id) {
        CmsPage one = this.getById(id);

        if (one != null) {
            cmsPageRepository.deleteById(id);
            return new ResponseResult(CommonCode.SUCCESS);
        }

        return new ResponseResult(CommonCode.FAIL);
    }

    /**
     * 页面静态化
     * @param pageId
     * @return
     */
    public String getPageHtml(String pageId) {

        // 1. 获取页面数据
        CmsPage cmsPage = this.getById(pageId);

        if (cmsPage == null) {
            // 页面不存在
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        // 2. 获取页面模型数据
        Map model = this.getModelByPageId(cmsPage);

        if (model == null) {
            // 获取的页面模型数据为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAISNULL);
        }

        // 3. 获取页面模板
        String templateContent = getTemplateByPageId(cmsPage);

        if (templateContent == null) {
            // 页面内容为空
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        // 执行静态化
        String html = generateHtml(templateContent, model);

        if (org.springframework.util.StringUtils.isEmpty(html)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }
        return html;
    }

    /**
     * 获取页面模型数据
     * @param cmsPage
     * @return
     */
    private Map getModelByPageId(CmsPage cmsPage) {

        // 取出dataUrl
        String dataUrl = cmsPage.getDataUrl();

        if (org.springframework.util.StringUtils.isEmpty(dataUrl)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_DATAURLISNULL);
        }

        // 根据rul请求数据
        ResponseEntity<Map> forEntity = restTemplate.getForEntity(dataUrl, Map.class);
        Map body = forEntity.getBody();
        return body;
    }

    /**
     * 获取页面模板
     * @param cmsPage
     * @return
     */
    private String getTemplateByPageId(CmsPage cmsPage) {
        // 获取模板id
        String templateId = cmsPage.getTemplateId();

        if (org.springframework.util.StringUtils.isEmpty(templateId)) {
            // 页面模板没有关联，抛出异常
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
        }

        Optional<CmsTemplate> optional = cmsTemplateRepository.findById(templateId);

        if (optional.isPresent()) {
            CmsTemplate cmsTemplate = optional.get();

            // 获取模板文件id
            String templateFileId = cmsTemplate.getTemplateFileId();

            // 从GridFSFile取出模板文件的内容
            GridFSFile gridFSFile = gridFsTemplate.findOne(Query.query(Criteria.where("_id").is(templateFileId)));

            if (gridFSFile == null) {
                ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_TEMPLATEISNULL);
            }

            // 获取下载流
            GridFSDownloadStream gridFSDownloadStream = gridFSBucket.openDownloadStream(gridFSFile.getObjectId());

            // 创建GridFsResource
            GridFsResource gridFsResource = new GridFsResource(gridFSFile, gridFSDownloadStream);

            try {
                return IOUtils.toString(gridFsResource.getInputStream(), "utf-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    /**
     * 页面静态化
     * @param template
     * @param model
     * @return
     */
    private String generateHtml(String template, Map model){
        try {

            // 生成配置类
            Configuration configuration = new Configuration(Configuration.getVersion());

            // 模板加载器
            StringTemplateLoader stringTemplateLoader = new StringTemplateLoader();
            stringTemplateLoader.putTemplate("template", template);

            // 配置模板加载器
            configuration.setTemplateLoader(stringTemplateLoader);

            // 获取模板
            Template template1 = configuration.getTemplate("template");
            return FreeMarkerTemplateUtils.processTemplateIntoString(template1, model);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 页面发布
     * @param pageId
     * @return
     */
    public ResponseResult postPage(String pageId) {
        // 执行静态化
        String pageHtml = this.getPageHtml(pageId);
        if (StringUtils.isEmpty(pageHtml)) {
            ExceptionCast.cast(CmsCode.CMS_GENERATEHTML_HTMLISNULL);
        }

        // 保存静态文件 到 GridFS
        CmsPage cmsPage = saveHtml(pageId, pageHtml);

        // 发送消息
        sendPostPage(pageId);
        return new ResponseResult(CommonCode.SUCCESS);
    }

    /**
     * 发送页面发布消息
     * @param pageId
     */
    private void sendPostPage(String pageId) {
        CmsPage cmsPage = this.getById(pageId);

        if (cmsPage == null) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        Map<String, String> msgMap = new HashMap<>();
        msgMap.put("pageId", pageId);

        // 消息内容字符串
        String msg = JSON.toJSONString(msgMap);

        // 获取站点id作为routingKey
        String siteId = cmsPage.getSiteId();

        // 发布消息
        this.rabbitTemplate.convertAndSend(RabbitmqConfig.EX_ROUTING_CMS_POSTPAGE, siteId, msg);
    }

    /**
     * 保存静态页面内容
     * @param pageId
     * @param content
     * @return
     */
    private CmsPage saveHtml(String pageId, String content) {
        // 查询页面【冗余】
        Optional<CmsPage> optional = cmsPageRepository.findById(pageId);

        if (!optional.isPresent()) {
            ExceptionCast.cast(CmsCode.CMS_PAGE_NOTEXISTS);
        }

        CmsPage cmsPage = optional.get();

        // 存储之前先删除,将老的文件删掉，避免
        String htmlFileId = cmsPage.getHtmlFileId();
        if(!StringUtils.isEmpty(htmlFileId)) {
            gridFsTemplate.delete(Query.query(Criteria.where("_id").is(htmlFileId)));
        }

        // 保存文件到GridFS
        InputStream inputStream = IOUtils.toInputStream(content);
        ObjectId objectId = gridFsTemplate.store(inputStream, cmsPage.getPageName());

        // 文件id
        String fileId = objectId.toString();

        // 将文件id存储到cmsPage中,保存到数据库
        cmsPage.setHtmlFileId(fileId);
        cmsPageRepository.save(cmsPage);
        return cmsPage;
    }

    /**
     * 添加页面，如果已经存在，则更新页面
     * @param cmsPage
     * @return
     */
    public CmsPageResult save(CmsPage cmsPage) {
        // 校验页面是否存在，根据页面名称，站点id,页面webpath查询

        CmsPage cmsPageChick = cmsPageRepository.findByPageNameAndSiteIdAndPageWebPath(
                cmsPage.getPageName(), cmsPage.getSiteId(), cmsPage.getPageWebPath()
        );

        if (cmsPageChick != null) {
            // 跟新操作
            return this.update(cmsPageChick.getPageId(), cmsPage);
        } else {
            // 添加
            return this.add(cmsPage);
        }
    }

    /**
     * 一间发布功能服务
     * @param cmsPage
     * @return
     */
    public CmsPostPageResult postPageQuick(CmsPage cmsPage) {

        // 添加页面
        CmsPageResult save = this.save(cmsPage);

        if (!save.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }

        CmsPage cmsPageNew = save.getCmsPage();

        // 要发布的页面id
        String pageId = cmsPageNew.getPageId();

        // 发布页面,调用上面定义的页面发布方法
        ResponseResult responseResult = this.postPage(pageId);

        if (!responseResult.isSuccess()) {
            return new CmsPostPageResult(CommonCode.FAIL, null);
        }

        // 得到页面的url = 站点域名 + 站点webpath + 页面webpath + 页面名称

        // 站点id
        String siteId = cmsPageNew.getSiteId();

        // 查询站点信息
        CmsSite cmsSite = siteService.findCmsSiteById(siteId);
        if (cmsSite == null) {
            ExceptionCast.cast(CmsCode.CMS_SITE_NOTEXISTS);
        }

        // 站点域名
        String siteDomain = cmsSite.getSiteDomain();

        // 站点web路径
        String siteWebPath = cmsSite.getSiteWebPath();

        // 页面web路径
        String pageWebPath = cmsPageNew.getPageWebPath();

        // 页面名称
        String pageName = cmsPageNew.getPageName();

        // 页面的web访问地址
        return new CmsPostPageResult(CommonCode.SUCCESS, siteDomain + siteWebPath + pageWebPath + pageName);

    }
}
