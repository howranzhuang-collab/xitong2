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
     * 提交申请 (统一接口：记录+文件)
     * POST /api/application/submit
     */
    @PostMapping("/submit")
    public Result<Object> submit(
            @RequestParam("projectId") String projectId,
            @RequestParam(value = "files", required = false) org.springframework.web.multipart.MultipartFile[] files
    ) {
        // 1. 获取当前登录用户 (From SecurityContext)
        Student student = null;
        org.springframework.security.core.Authentication auth = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Student) {
            student = (Student) auth.getPrincipal();
        }
        
        // 兼容 Session 方式 (Double Check)
        if (student == null) {
            Object userObj = getSessionValue("user");
            if (userObj instanceof Student) {
                student = (Student) userObj;
            }
        }
        
        if (student == null) {
            return fail("请先登录学生账号");
        }

        if (StringUtils.isEmpty(projectId)) {
            return fail("项目ID不能为空");
        }
        
        // 2. 处理文件上传
        List<String> documentPaths = new java.util.ArrayList<>();
        if (files != null && files.length > 0) {
            try {
                // 物理存储路径：项目根目录/uploads/applications/
                String projectRoot = System.getProperty("user.dir");
                String uploadDir = projectRoot + "/uploads/applications/";
                java.io.File dir = new java.io.File(uploadDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }

                for (org.springframework.web.multipart.MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String originalFilename = file.getOriginalFilename();
                        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
                        String newFileName = com.codeying.utils.CommonUtils.newId() + suffix;
                        
                        java.io.File dest = new java.io.File(uploadDir + newFileName);
                        file.transferTo(dest);
                        
                        // 记录相对路径 (需配合 WebConfig 资源映射)
                        documentPaths.add("/uploads/applications/" + newFileName);
                    }
                }
            } catch (java.io.IOException e) {
                e.printStackTrace();
                return fail("文件上传失败: " + e.getMessage());
            }
        }

        String documentsStr = documentPaths.toString(); // e.g., ["/uploads/...", "/uploads/..."]

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
