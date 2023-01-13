package com.imooc.bilibili.api;

import com.imooc.bilibili.domain.minio.dto.Result;
import com.imooc.bilibili.domain.minio.dto.TaskInfoDTO;
import com.imooc.bilibili.domain.minio.entity.SysUploadTask;
import com.imooc.bilibili.domain.minio.param.InitTaskParam;
import com.imooc.bilibili.service.SysUploadTaskService;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * <p>在开始处详细描述该类的作用</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/3/29 16:26
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@RestController
@RequestMapping("/minio")
public class MinIOApi {

    @Resource
    private SysUploadTaskService sysUploadTaskService;

    /**
     * 获取上传进度
     * 主要流程就是查询数据库记录，存在上传任务再通过amazon s3的sdk方法：amazonS3.doesObjectExist，判断是否存在文件对象，存在则说明已经合并完成。
     */
    @GetMapping("/{identifier}")
    public Result<TaskInfoDTO> taskInfo (@PathVariable("identifier") String identifier) {
        return Result.ok(sysUploadTaskService.getTaskInfo(identifier));
    }

    /**
     * 创建一个上传任务
     * 当接口1返回的数据为null时，调用此接口初始化一个上传任务。
     */
    @PostMapping
    public Result<TaskInfoDTO> initTask (@Valid @RequestBody InitTaskParam param, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            return Result.error(bindingResult.getFieldError().getDefaultMessage());
        }
        return Result.ok(sysUploadTaskService.initTask(param));
    }

    /**
     * 获取每个分片的预签名上传地址
     *
     * 前端在校验当前分片未上传时，调用该接口，获取到一个分片的上传地址，将分片的文件流直接通过PUT请求上传到该地址。该接口也是对amazon sdk的方法进行包装：amazonS3.generatePresignedUrl。
     * @param identifier
     * @param partNumber
     * @return
     */
    @GetMapping("/{identifier}/{partNumber}")
    public Result preSignUploadUrl (@PathVariable("identifier") String identifier, @PathVariable("partNumber") Integer partNumber) {
        SysUploadTask task = sysUploadTaskService.getByIdentifier(identifier);
        if (task == null) {
            return Result.error("分片任务不存在");
        }
        Map<String, String> params = new HashMap<>();
        params.put("partNumber", partNumber.toString());
        params.put("uploadId", task.getUploadId());
        return Result.ok(sysUploadTaskService.genPreSignUploadUrl(task.getBucketName(), task.getObjectKey(), params));
    }

    /**
     * 合并分片
     *
     * 当所有分片完成上传时，调用该接口。该接口是对amazon sdk的方法：amazonS3.completeMultipartUpload进行封装。在合并的校验逻辑中，仅仅是对分片数量是否一致做了校验，理论上应该通过已上传分片的eTag计算总文件的MD5是否与数据库中存储的一致。但我通过etag计算出来的md5与直接前端通过文件流计算出来的md5不一致，所以只能采用这种方式了。
     * @param identifier
     * @return
     */
    @PostMapping("/merge/{identifier}")
    public Result merge (@PathVariable("identifier") String identifier) {
        sysUploadTaskService.merge(identifier);
        return Result.ok();
    }
}
