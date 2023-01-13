package com.imooc.bilibili.domain.minio.dto;

import cn.hutool.core.bean.BeanUtil;
import com.amazonaws.services.s3.model.PartSummary;
import com.imooc.bilibili.domain.minio.entity.SysUploadTask;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * <p>在开始处详细描述该类的作用</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/3/29 16:09
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Data
@ToString
@Accessors(chain = true)
public class TaskRecordDTO extends SysUploadTask {
    /**
     * id	long	任务id
     * uploadId	string	minio的uploadId
     * fileIdentifier	string	文件唯一标识（MD5）
     * fileName	string	文件名称
     * bucketName	string	所属桶名
     * objectKey	string	文件的key
     * totalSize	long	文件大小（byte）
     * chunkSize	long	每个分片大小（byte）
     * chunkNum	int	分片数量
     * exitPartList	PartSummary[]	已上传完的分片 （finished为true时，该字段为null）
     */


    private List<PartSummary> exitPartList;

    public static TaskRecordDTO convertFromEntity (SysUploadTask task) {
        TaskRecordDTO dto = new TaskRecordDTO();
        BeanUtil.copyProperties(task, dto);
        return dto;
    }
}
