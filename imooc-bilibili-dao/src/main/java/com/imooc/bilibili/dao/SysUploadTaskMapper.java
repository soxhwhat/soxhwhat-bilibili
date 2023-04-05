package com.imooc.bilibili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.bilibili.domain.minio.entity.SysUploadTask;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * 分片上传任务记录表数据库访问层
 */
@Repository
@Mapper
public interface SysUploadTaskMapper extends BaseMapper<SysUploadTask> {
}
