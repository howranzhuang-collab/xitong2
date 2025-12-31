package com.codeying.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codeying.entity.Application;
import org.apache.ibatis.annotations.Mapper;

/**
 * 申请记录 Mapper
 */
@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {
}
