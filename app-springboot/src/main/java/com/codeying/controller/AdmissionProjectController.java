package com.codeying.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeying.component.ApiResult;
import com.codeying.entity.AdmissionProject;
import com.codeying.service.AdmissionProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 招生项目接口控制器
 */
@RestController
@RequestMapping("api/project")
public class AdmissionProjectController extends BaseController {

    @Autowired
    private AdmissionProjectService admissionProjectService;

    /**
     * 获取招生项目列表（分页）
     * 无需登录即可访问
     * @param pageIndex 当前页码，默认1
     * @param pageSize 每页数量，默认10
     * @param name 项目名称搜索（可选）
     * @return 项目列表
     */
    @GetMapping("list")
    public ApiResult<IPage<AdmissionProject>> list(
            @RequestParam(defaultValue = "1") Integer pageIndex,
            @RequestParam(defaultValue = "10") Integer pageSize,
            String name) {
        
        // 构建分页对象
        IPage<AdmissionProject> page = new Page<>(pageIndex, pageSize);
        
        // 构建查询条件
        QueryWrapper<AdmissionProject> queryWrapper = new QueryWrapper<>();
        // 只查询启用的项目
        queryWrapper.eq("status", 1);
        // 按名称模糊查询
        if (!StringUtils.isEmpty(name)) {
            queryWrapper.like("name", name);
        }
        // 按创建时间倒序
        queryWrapper.orderByDesc("create_time");
        
        // 执行分页查询
        IPage<AdmissionProject> result = admissionProjectService.page(page, queryWrapper);
        
        return ApiResult.successData(result);
    }
}
