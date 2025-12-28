package com.codeying.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.codeying.entity.AdmissionProject;
import com.codeying.mapper.AdmissionProjectMapper;
import com.codeying.service.AdmissionProjectService;
import org.springframework.stereotype.Service;

/**
 * 招生项目服务实现类
 */
@Service
public class AdmissionProjectServiceImpl extends ServiceImpl<AdmissionProjectMapper, AdmissionProject> implements AdmissionProjectService {
}
