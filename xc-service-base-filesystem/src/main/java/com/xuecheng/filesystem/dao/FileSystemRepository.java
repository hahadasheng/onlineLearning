package com.xuecheng.filesystem.dao;

import com.xuecheng.framework.domain.filesystem.FileSystem;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * @author Liucheng
 * @date 2019/4/19 21:09
 */
public interface FileSystemRepository extends MongoRepository<FileSystem, String> {
}
