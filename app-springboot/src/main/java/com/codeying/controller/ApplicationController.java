package com.codeying.controller;

import com.codeying.component.Result;
import com.codeying.entity.Student;
import com.codeying.service.ApplicationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 申请记录控制器
 */
@RestController
@RequestMapping("/api/application")
public class ApplicationController extends BaseController {

    @Autowired
    private ApplicationService applicationService;

    @Autowired
    private AdmissionProjectService admissionProjectService;

    @Autowired
    private com.codeying.service.StudentService studentService; // Use full package to avoid import conflict if any

    /**
     * 获取申请列表 (Admin only)
     */
    @GetMapping("/list")
    public Result<IPage<Application>> list(@RequestParam(defaultValue = "1") Integer pageIndex,
                                           @RequestParam(defaultValue = "10") Integer pageSize) {
        // 简单权限校验
        String role = (String) session.getAttribute("role");
        if (!"admin".equals(role)) {
            return fail("无权访问");
        }

        IPage<Application> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageIndex, pageSize);
        com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<Application> queryWrapper = new com.baomidou.mybatisplus.core.conditions.query.QueryWrapper<>();
        queryWrapper.orderByDesc("create_time");

        IPage<Application> result = applicationService.page(page, queryWrapper);
        
        // 填充额外信息
        for (Application app : result.getRecords()) {
            if (app.getProjectId() != null) {
                com.codeying.entity.AdmissionProject project = admissionProjectService.getById(app.getProjectId());
                if (project != null) {
                    app.setProjectName(project.getName());
                }
            }
            if (app.getStudentId() != null) {
                Student student = studentService.getById(app.getStudentId());
                if (student != null) {
                    app.setStudentName(student.getName());
                }
            }
        }

        return Result.success(result);
    }

    /**
     * 提交申请
     * POST /api/application/submit
     * @param params 包含 projectId 和 documents (List<String>)
     */
    @PostMapping("/submit")
    public Result<Object> submit(@RequestBody Map<String, Object> params) {
        // 1. 获取当前登录用户
        Object userObj = getSessionValue("user");
        if (userObj == null || !(userObj instanceof Student)) {
            return fail("请先登录学生账号");
        }
        Student student = (Student) userObj;

        // 2. 获取参数
        String projectId = (String) params.get("projectId");
        Object documentsObj = params.get("documents");
        
        if (StringUtils.isEmpty(projectId)) {
            return fail("项目ID不能为空");
        }
        
        String documentsStr = "";
        if (documentsObj != null) {
            // 简单处理，如果是List则转为字符串存储，实际可根据需求存JSON
            documentsStr = documentsObj.toString(); 
        }

        try {
            boolean success = applicationService.submitApplication(student.getId(), projectId, documentsStr);
            if (success) {
                return Result.success("申请提交成功");
            } else {
                return fail("申请提交失败");
            }
        } catch (Exception e) {
            return fail(e.getMessage());
        }
    }
}
