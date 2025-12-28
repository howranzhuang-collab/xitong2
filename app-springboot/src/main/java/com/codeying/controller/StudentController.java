package com.codeying.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.codeying.component.ApiResult;
import com.codeying.entity.Student;
import com.codeying.service.StudentService;
import com.codeying.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 学生控制器
 */
@RestController
@RequestMapping("api/student")
public class StudentController extends BaseController {

    @Autowired
    private StudentService studentService;

    /**
     * 学生注册
     * @param student 包含姓名、邮箱、密码
     * @return 注册结果
     */
    @PostMapping("register")
    public ApiResult<Object> register(Student student) {
        // 校验必填项
        if (StringUtils.isEmpty(student.getName())) {
            return fail("姓名不能为空");
        }
        if (StringUtils.isEmpty(student.getEmail())) {
            return fail("邮箱不能为空");
        }
        if (StringUtils.isEmpty(student.getPassword())) {
            return fail("密码不能为空");
        }

        // 校验邮箱是否已注册
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", student.getEmail());
        if (studentService.count(queryWrapper) > 0) {
            return fail("该邮箱已注册");
        }

        // 补充信息
        student.setId(CommonUtils.newId());
        // 生成学号，这里简单使用时间戳+随机数，或者复用ID
        student.setStudentNo("S" + CommonUtils.newId());
        student.setStatus(1);
        student.setCreateTime(new Date());
        student.setUpdateTime(new Date());
        student.setRole("student");
        student.setRolech("学生");

        // 保存到数据库
        boolean result = studentService.save(student);

        if (result) {
            return ApiResult.successMsgAndData("注册成功", student);
        } else {
            return fail("注册失败");
        }
    }

    /**
     * 学生登录
     * @param email 邮箱
     * @param password 密码
     * @return 登录结果
     */
    @PostMapping("login")
    public ApiResult<Object> login(String email, String password) {
        if (StringUtils.isEmpty(email)) {
            return fail("邮箱不能为空");
        }
        if (StringUtils.isEmpty(password)) {
            return fail("密码不能为空");
        }

        // 查询用户
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("email", email);
        queryWrapper.eq("password", password);
        Student student = studentService.getOne(queryWrapper);

        if (student == null) {
            return fail("邮箱或密码错误");
        }

        if (student.getStatus() != 1) {
            return fail("账号已被禁用");
        }

        // 存入 Session (兼容旧代码)
        setSessionValue("user", student);

        // 设置 Spring Security 上下文
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(student, null, AuthorityUtils.createAuthorityList("ROLE_STUDENT"));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // 显式保存到 Session，确保 Spring Security 能够识别
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, SecurityContextHolder.getContext());

        return ApiResult.successMsgAndData("登录成功", student);
    }

    /**
     * 获取当前登录用户信息
     * 支持 GET 和 POST
     * @return 当前用户信息
     */
    @RequestMapping(value = "info", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult<Object> info() {
        Object userObj = getSessionValue("user");
        if (userObj == null) {
            return fail("未登录");
        }
        
        if (userObj instanceof Student) {
            Student sessionStudent = (Student) userObj;
            // 从数据库查询最新信息
            Student latestStudent = studentService.getById(sessionStudent.getId());
            if (latestStudent != null) {
                return ApiResult.successData(latestStudent);
            }
        }
        
        // 兜底返回Session中的信息
        return ApiResult.successData(userObj);
    }
}
