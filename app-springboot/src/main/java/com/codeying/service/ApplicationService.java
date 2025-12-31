package com.codeying.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.codeying.entity.Application;

import java.util.List;

/**
 * 申请记录服务接口
 */
public interface ApplicationService extends IService<Application> {
    
    /**
     * 提交申请
     * @param studentId 学生ID
     * @param projectId 项目ID
     * @param documents 申请材料(JSON/字符串)
     * @return 提交结果
     */
    boolean submitApplication(String studentId, String projectId, String documents);
}
