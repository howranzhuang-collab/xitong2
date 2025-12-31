package com.codeying.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeying.component.PagerFooterVO;
import com.codeying.component.Result;
import com.codeying.entity.AdmissionProject;
import com.codeying.entity.Application;
import com.codeying.entity.Student;
import com.codeying.service.AdmissionProjectService;
import com.codeying.service.ApplicationService;
import com.codeying.service.StudentService;
import com.codeying.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * 学生控制器
 */
@Controller
//@RequestMapping("student") // 移除类级别的映射，避免影响 API 接口
public class StudentController extends BaseController {

    @Autowired
    private StudentService studentService;

    @Autowired
    private AdmissionProjectService admissionProjectService;

    @Autowired
    private ApplicationService applicationService;

    // --- 页面跳转接口 ---

    /**
     * 学生首页
     */
    @GetMapping("/student/index")
    public String index(Model model) {
        return "student-index";
    }

    /**
     * 招生项目列表页
     */
    @GetMapping("/student/project/list")
    public String projectList(Model model, Integer pageIndex, Integer size, String name) {
        if (pageIndex == null) {
            pageIndex = 1;
        }
        if (size == null) {
            size = 10;
        }
        
        // 分页查询
        IPage<AdmissionProject> page = new Page<>(pageIndex, size);
        QueryWrapper<AdmissionProject> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("status", 1); // 只看启用的
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.orderByDesc("create_time");
        
        IPage<AdmissionProject> pageInfo = admissionProjectService.page(page, queryWrapper);
        
        model.addAttribute("projectList", pageInfo.getRecords());
        model.addAttribute("pager", new PagerFooterVO(pageInfo));
        model.addAttribute("name", name);
        
        return "student-project-list";
    }

    /**
     * 我的申请记录页
     */
    @GetMapping("/student/application/list")
    public String applicationList(Model model, Integer pageIndex, Integer size) {
        if (pageIndex == null) {
            pageIndex = 1;
        }
        if (size == null) {
            size = 10;
        }

        // 获取当前登录学生
        Object userObj = getSessionValue("user");
        if (userObj == null || !(userObj instanceof Student)) {
            return "redirect:/login";
        }
        Student student = (Student) userObj;

        // 分页查询
        IPage<Application> page = new Page<>(pageIndex, size);
        QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("student_id", student.getId());
        queryWrapper.orderByDesc("create_time");

        IPage<Application> pageInfo = applicationService.page(page, queryWrapper);
        
        // 补充项目名称信息（简单实现：遍历查询）
        // 实际开发建议使用联表查询或DTO
        for (Application app : pageInfo.getRecords()) {
            if (app.getProjectId() != null) {
                AdmissionProject project = admissionProjectService.getById(app.getProjectId());
                if (project != null) {
                    app.setProjectName(project.getName());
                }
            }
        }

        model.addAttribute("list", pageInfo.getRecords());
        model.addAttribute("pager", new PagerFooterVO(pageInfo));

        return "student-application-list";
    }

    // --- API 接口 (保留原有的 /api/student/* 路径映射，需单独处理或迁移) ---
    // 注意：由于类上的 RequestMapping 改为了 "student"，这里的 API 需要适配
    // 建议：将 API 接口保留在另一个 @RestController 中，或者在这里使用完整路径映射
    // 为了保持兼容性，我们将 API 接口方法显式指定路径 /api/student/...
    
    /**
     * 学生注册 API
     */
    @PostMapping("/api/student/register")
    @ResponseBody
    public Result<Object> register(Student student) {
        // ... (保持原有逻辑不变)
        // 校验必填项
        if (StringUtils.isEmpty(student.getUsername())) {
            return fail("用户名不能为空");
        }
        if (StringUtils.isEmpty(student.getName())) {
            return fail("姓名不能为空");
        }
        if (StringUtils.isEmpty(student.getEmail())) {
            return fail("邮箱不能为空");
        }
        if (StringUtils.isEmpty(student.getPassword())) {
            return fail("密码不能为空");
        }

        // 校验用户名是否已注册
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", student.getUsername());
        if (studentService.count(queryWrapper) > 0) {
            return fail("该用户名已注册");
        }
        
        // 校验邮箱是否已注册
        QueryWrapper<Student> emailQuery = new QueryWrapper<>();
        emailQuery.eq("email", student.getEmail());
        if (studentService.count(emailQuery) > 0) {
            return fail("该邮箱已注册");
        }

        // 补充信息
        student.setId(CommonUtils.newId());
        student.setStudentNo("S" + CommonUtils.newId());
        student.setStatus(1);
        student.setCreateTime(new Date());
        student.setUpdateTime(new Date());
        student.setRole("student");
        student.setRolech("学生");

        boolean result = studentService.save(student);

        if (result) {
            return Result.success("注册成功", student);
        } else {
            return fail("注册失败");
        }
    }

    /**
     * 学生登录 API
     */
    @PostMapping("/api/student/login")
    @ResponseBody
    public Result<Object> login(String username, String password) {
        // ... (保持原有逻辑不变)
        if (StringUtils.isEmpty(username)) {
            return fail("用户名不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            return fail("密码不能为空");
        }

        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username);
        queryWrapper.eq("password", password);
        Student student = studentService.getOne(queryWrapper);

        if (student == null) {
            return fail("用户名或密码错误");
        }

        if (student.getStatus() != 1) {
            return fail("账号已被禁用");
        }

        setSessionValue("user", student);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(student, null, AuthorityUtils.createAuthorityList("ROLE_STUDENT"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return Result.success("登录成功", student);
    }
}
