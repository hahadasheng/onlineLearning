package com.xuecheng.manage_cms.config;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.gridfs.GridFSBucket;
import com.mongodb.client.gridfs.GridFSBuckets;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MongoConfig {

    @Value("${spring.data.mongodb.database}")
    String db;

    /**
     * mongodb文件下载相关
     * @param mongoClient
     * @return
     */
    @Bean
    public GridFSBucket getGridFsBucket(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase(db);
        GridFSBucket bucket = GridFSBuckets.create(database);

        return bucket;
    }
}
