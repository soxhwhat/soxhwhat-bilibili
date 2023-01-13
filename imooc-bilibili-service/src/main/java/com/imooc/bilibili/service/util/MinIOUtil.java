package com.imooc.bilibili.service.util;

import com.imooc.bilibili.domain.exception.ConditionException;
import com.imooc.bilibili.domain.minio.param.InitTaskParam;
import com.imooc.bilibili.service.SysUploadTaskService;
import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.netty.util.internal.StringUtil;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>MinIO替代FastDFS</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/3/27 21:57
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Component
public class MinIOUtil {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SysUploadTaskService sysUploadTaskService;

    private static final String BUCKET_NAME = "bilibili";

    private static final String ENDPOINT = "http://127.0.0.1:9000";


    private static final String PATH_KEY = "path-key:";

    private static final String UPLOADED_SIZE_KEY = "uploaded-size-key:";

    private static final String UPLOADED_NO_KEY = "uploaded-no-key:";

    private static final int SLICE_SIZE = 1024 * 1024 * 2;

    public String getFileType(MultipartFile file) {
        if (file == null) {
            throw new ConditionException("非法文件！");
        }
        String fileName = file.getOriginalFilename();
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index + 1);
    }

    //上传
    @SneakyThrows
    public String uploadCommonFile(MultipartFile file) {
        return this.uploadCommonFile(file, getFileType(file));
    }

    @SneakyThrows
    public String uploadCommonFile(MultipartFile file, String fileType) {
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(file.getName())
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(fileType)
                .build());
        return ENDPOINT + "/" + BUCKET_NAME + "/" + file.getName();
    }

    /**
     * 断点续传
     *
     * @param file
     * @return
     */
    @SneakyThrows
    public String uploadAppendFile(MultipartFile file) {
        String fileType = this.getFileType(file);
        minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(file.getName())
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(fileType)
                .build());
        return "";
    }

    @SneakyThrows
    public void downLoadFile(String path) {
        minioClient.downloadObject(DownloadObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }


    @SneakyThrows
    public String uploadFileBySlice(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) {
        if(file == null || sliceNo == null || totalSliceNo == null){
            throw new ConditionException("参数异常！");
        }
        /**
         * 这段代码的目的是为了生成三个键值，用于记录文件上传的状态。
         * 其中，PATH_KEY + fileMd5 用于记录文件在服务器上的存储路径；
         * UPLOADED_SIZE_KEY + fileMd5 用于记录已上传的文件大小；
         * UPLOADED_NO_KEY + fileMd5 用于记录已上传的文件块数。
         * 这些键值可以帮助程序在上传大文件时进行断点续传和上传进度显示等功能。
         */
        String pathKey = PATH_KEY + fileMd5;
        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;
        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
        Long uploadedSize = !StringUtil.isNullOrEmpty(uploadedSizeStr) ? 0L : Long.parseLong(uploadedSizeStr);

        if (sliceNo == 1) {
            InitTaskParam initTaskParam = new InitTaskParam().setIdentifier(fileMd5).setChunkSize(file.getSize() / totalSliceNo).setTotalSize(file.getSize()).setFileName(file.getOriginalFilename());
            sysUploadTaskService.initTask(initTaskParam);
        }
        return "test";
    }


}
