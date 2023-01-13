package com.imooc.bilibili.domain.minio.dto;

import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <p>在开始处详细描述该类的作用</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/3/29 16:23
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Data
@ToString
@Accessors(chain = true)
public class ObjectInfo {

    /**
     * 所在桶
     */
    private String bucket;

    /**
     * 文件地址
     */
    private String path;
}