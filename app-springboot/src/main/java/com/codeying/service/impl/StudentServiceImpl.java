package com.codeying.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeying.entity.Student;
import com.codeying.mapper.StudentMapper;
import com.codeying.service.StudentService;
import org.springframework.stereotype.Service;

/**
 * 学生服务实现类
 */
@Service
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
}
