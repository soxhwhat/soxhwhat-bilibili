package com.imooc.bilibili.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.imooc.bilibili.domain.minio.dto.TaskInfoDTO;
import com.imooc.bilibili.domain.minio.entity.SysUploadTask;
import com.imooc.bilibili.domain.minio.param.InitTaskParam;

import java.util.Map;

public interface SysUploadTaskService extends IService<SysUploadTask> {

    SysUploadTask getByIdentifier(String identifier);

    /**
     * 根据md5标识获取分片上传任务
     * @param md5
     * @return
     */
    SysUploadTask getUploadTaskByMd5(String md5);

    /**
     * 初始化分片上传任务
     * @param param
     * @return
     */
    TaskInfoDTO initTask(InitTaskParam param);

    /**
     * 获取文件地址
     * @param bucket
     * @param objectKey
     * @return
     */
    String path (String bucket, String objectKey);

    /**
     * 获取上传进度
     * @param identifier
     * @return
     */
    TaskInfoDTO getTaskInfo (String identifier);

    /**
     * 生成预签名上传url
     * @param bucket
     * @param objectKey
     * @param params
     * @return
     */
    String genPreSignUploadUrl (String bucket, String objectKey, Map<String, String> params);

    /**
     * 合并分片
     * @param identifier
     */
    void merge (String identifier);

}
