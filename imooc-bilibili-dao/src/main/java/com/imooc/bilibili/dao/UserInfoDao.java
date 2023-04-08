package com.imooc.bilibili.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.imooc.bilibili.domain.UserInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * <p>在开始处详细描述该类的作用</p>
 * <p>描述请遵循 javadoc 规范</p>
 *
 * @author Soxhwhat
 * @date 2023/4/6 14:10
 * @update [序号][日期YYYY-MM-DD] [更改人姓名][变更描述]
 */
@Mapper
public interface UserInfoDao extends BaseMapper<UserInfo> {
}
