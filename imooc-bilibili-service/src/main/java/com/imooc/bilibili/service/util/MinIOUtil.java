package com.imooc.bilibili.service.util;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import com.imooc.bilibili.domain.exception.ConditionException;
import com.imooc.bilibili.properties.MinioProperties;
import com.imooc.bilibili.service.SysUploadTaskService;
import io.minio.DownloadObjectArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.*;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>MinIO替代FastDFS</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/3/27 21:57
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Component
@Slf4j
public class MinIOUtil {

    @Autowired
    private MinioClient minioClient;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private SysUploadTaskService sysUploadTaskService;


    @Resource
    private AmazonS3 amazonS3;

    @Autowired
    private MinioProperties minioProperties;

    private static final String BUCKET_NAME = "bilibili";

    private static final String ENDPOINT = "http://127.0.0.1:9000";


    private static final int SLICE_SIZE = 1024 * 1024 * 5;

    public String getFileType(MultipartFile file) {
        return Optional.ofNullable(file)
                .map(MultipartFile::getOriginalFilename)
                .map(fileName -> fileName.substring(fileName.lastIndexOf(".") + 1))
                .orElseThrow(() -> new ConditionException("非法文件！"));
    }

    //上传
    @SneakyThrows
    public String uploadCommonFile(MultipartFile file) {
        return this.uploadCommonFile(file, getFileType(file));
    }

    @SneakyThrows
    public String uploadCommonFile(MultipartFile file, String fileType) {
        return minioClient.putObject(PutObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(file.getName())
                .stream(file.getInputStream(), file.getSize(), -1)
                .contentType(fileType)
                .build()).etag();
    }

    public String getFilePath(MultipartFile file) {
        return ENDPOINT + "/" + BUCKET_NAME + "/" + getUploadKey(file);
    }


    public void deleteFile(String path) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs.builder()
                .bucket(BUCKET_NAME)
                .object(path)
                .build());
    }

    public String getUploadKey(MultipartFile file) {
        Date currentDate = new Date();
        String bucketName = minioProperties.getBucketName();
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        String key = StrUtil.format("{}/{}.{}", DateUtil.format(currentDate, "YYYY-MM-dd"), IdUtil.randomUUID(), suffix);
        return key;
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
//    public String uploadFileBySlice(MultipartFile file, String fileMd5, Integer sliceNo, Integer totalSliceNo) {
//        if (file == null || sliceNo == null || totalSliceNo == null) {
//            throw new ConditionException("参数异常！");
//        }
//        /**
//         * 这段代码的目的是为了生成三个键值，用于记录文件上传的状态。
//         * 其中，PATH_KEY + fileMd5 用于记录文件在服务器上的存储路径；
//         * UPLOADED_SIZE_KEY + fileMd5 用于记录已上传的文件大小；
//         * UPLOADED_NO_KEY + fileMd5 用于记录已上传的文件块数。
//         * 这些键值可以帮助程序在上传大文件时进行断点续传和上传进度显示等功能。
//         */
//        String pathKey = PATH_KEY + fileMd5;
//        String uploadedSizeKey = UPLOADED_SIZE_KEY + fileMd5;
//        String uploadedNoKey = UPLOADED_NO_KEY + fileMd5;
//        String uploadedSizeStr = redisTemplate.opsForValue().get(uploadedSizeKey);
//        Long uploadedSize = StringUtil.isNullOrEmpty(uploadedSizeStr) ? 0L : Long.parseLong(uploadedSizeStr);
//
//        if (sliceNo == 1) {
//            String path = uploadCommonFile(file);
//            if (StringUtil.isNullOrEmpty(path)) {
//                throw new ConditionException("上传失败！");
//            }
//            redisTemplate.opsForValue().set(pathKey, path);
//            redisTemplate.opsForValue().set(uploadedNoKey, String.valueOf(1));
//
//        } else {
//            String path = redisTemplate.opsForValue().get(pathKey);
//            if (StringUtil.isNullOrEmpty(path)) {
//                throw new ConditionException("上传失败！");
//            }
//            redisTemplate.opsForValue().increment(uploadedNoKey);
//        }
//        uploadedSize += file.getSize();
//        redisTemplate.opsForValue().set(uploadedSizeKey, String.valueOf(uploadedSize));
//        String uploadedNoStr = redisTemplate.opsForValue().get(uploadedNoKey);
//        String path = "";
//        if (sliceNo.equals(totalSliceNo)) {
//            path = redisTemplate.opsForValue().get(pathKey);
//            List<String> keyList = Arrays.asList(pathKey, uploadedSizeKey, uploadedNoKey);
//            redisTemplate.delete(keyList);
//        }
//        Map<String, String> params = initTask(file);
//        ListPartsRequest listPartsRequest = new ListPartsRequest(BUCKET_NAME, params.get("key"), params.get("uploadId"));
//        PartListing partListing = amazonS3.listParts(listPartsRequest);
//        List<PartSummary> parts = partListing.getParts();
//        CompleteMultipartUploadRequest completeMultipartUploadRequest = new CompleteMultipartUploadRequest()
//                .withUploadId(params.get("uploadId"))
//                .withBucketName(BUCKET_NAME)
//                .withKey(params.get("key"))
//                .withPartETags(parts.stream().map(part -> new PartETag(part.getPartNumber(), part.getETag())).collect(Collectors.toList()));
//
//        amazonS3.completeMultipartUpload(completeMultipartUploadRequest);
//        return path;
//    }

    public void convertFileToSlices(MultipartFile multipartFile) throws Exception {
        String fileType = this.getFileType(multipartFile);
        //生成临时文件，将MultipartFile转为File
        File file = this.multipartFileToFile(multipartFile);
        long fileLength = file.length();
        int count = 1;
        for (int i = 0; i < fileLength; i += SLICE_SIZE) {
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "r");
            randomAccessFile.seek(i);
            byte[] bytes = new byte[SLICE_SIZE];
            int len = randomAccessFile.read(bytes);
            String path = System.getProperty("user.dir") + count + "." + fileType;
            File slice = new File(path);
            FileOutputStream fos = new FileOutputStream(slice);
            fos.write(bytes, 0, len);
            fos.close();
            randomAccessFile.close();
            count++;
        }
        //删除临时文件
        file.delete();
    }

    public File multipartFileToFile(MultipartFile multipartFile) throws Exception {
        String originalFileName = multipartFile.getOriginalFilename();
        String[] fileName = originalFileName.split("\\.");
        File file = File.createTempFile(fileName[0], "." + fileName[1]);
        multipartFile.transferTo(file);
        return file;
    }

    @SneakyThrows
    public String uploadFileBySlice(MultipartFile file) {
        String uploadKey = getUploadKey(file);
        long size = file.getSize();
        List<Long> positions = new ArrayList<>();
        long filePosition = 0;
        while (filePosition < size) {
            positions.add(filePosition);
            filePosition += Math.min(SLICE_SIZE, (size - filePosition));
        }
        // 创建一个列表保存所有分传的 PartETag, 在分段完成后会用到
        List<PartETag> partETags = new ArrayList<>();
        // 第一步，初始化，声明下面将有一个 Multipart Upload
        InitiateMultipartUploadRequest initRequest = new InitiateMultipartUploadRequest(BUCKET_NAME, uploadKey);
        InitiateMultipartUploadResult initResponse = amazonS3.initiateMultipartUpload(initRequest);
        try {
            // MultipartFile 转 File
            File toFile = multipartFileToFile(file);
            // 使用CountDownLatch来协调多个线程之间的同步
            final CountDownLatch latch = new CountDownLatch(positions.size());
            ThreadPoolTaskExecutor threadPoolTaskExecutor = new ThreadPoolTaskExecutor();
            threadPoolTaskExecutor.setCorePoolSize(3); //核心池大小
            threadPoolTaskExecutor.setMaxPoolSize(10); //最大线程数
            threadPoolTaskExecutor.setQueueCapacity(10); //队列程度
            threadPoolTaskExecutor.setKeepAliveSeconds(60); //线程空闲时间
            threadPoolTaskExecutor.setThreadNamePrefix("子线程-");//线程前缀名称
            threadPoolTaskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy()); //配置拒绝策略
            threadPoolTaskExecutor.initialize(); //初始化
            for (int i = 0; i < positions.size(); i++) {
                int finalI = i;
                threadPoolTaskExecutor.execute(() -> {
                    try {
                        long time1 = System.currentTimeMillis();
                        UploadPartRequest uploadRequest = new UploadPartRequest()
                                .withBucketName(BUCKET_NAME)
                                .withKey(uploadKey)
                                .withUploadId(initResponse.getUploadId())
                                .withPartNumber(finalI + 1)
                                .withFileOffset(positions.get(finalI))
                                .withFile(toFile)
                                .withPartSize(Math.min(SLICE_SIZE, (size - positions.get(finalI))));
                        // 第二步，上传分段，并把当前段的 PartETag 放到列表中
                        partETags.add(amazonS3.uploadPart(uploadRequest).getPartETag());
                        long time2 = System.currentTimeMillis();
                        log.info("第{}段上传耗时：{}", finalI + 1, (time2 - time1));
                    } catch (Exception e) {
                        log.error("Failed to upload, " + e.getMessage());
                    } finally {
                        latch.countDown();
                    }
                });
            }
            latch.await();
            // 第三步，完成上传，合并分段
            CompleteMultipartUploadRequest compRequest = new CompleteMultipartUploadRequest(BUCKET_NAME, uploadKey,
                    initResponse.getUploadId(), partETags);
            amazonS3.completeMultipartUpload(compRequest);
            return getFilePath(file);
        } catch (Exception e) {
            amazonS3.abortMultipartUpload(new AbortMultipartUploadRequest(BUCKET_NAME, uploadKey, initResponse.getUploadId()));
            log.error("Failed to upload, " + e.getMessage());
        }
        return "JsonResponse.fail();";
    }
}
