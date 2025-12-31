package com.codeying.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeying.entity.Application;
import com.codeying.mapper.ApplicationMapper;
import com.codeying.service.ApplicationService;
import com.codeying.utils.CommonUtils;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 申请记录服务实现类
 */
@Service
public class ApplicationServiceImpl extends ServiceImpl<ApplicationMapper, Application> implements ApplicationService {

    @Override
    public boolean submitApplication(String studentId, String projectId, String documents) {
        // 校验是否已经申请过该项目 (DB也有唯一索引作为兜底)
        QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", studentId);
        queryWrapper.eq("project_id", projectId);
        if (this.count(queryWrapper) > 0) {
            throw new RuntimeException("您已经申请过该项目，请勿重复申请");
        }

        Application application = new Application();
        application.setId(CommonUtils.newId());
        application.setStudentId(studentId);
        application.setProjectId(projectId);
        application.setDocuments(documents);
        application.setStatus("pending"); // 默认待审核
        application.setCreateTime(new Date());
        application.setUpdateTime(new Date());

        return this.save(application);
    }
}
