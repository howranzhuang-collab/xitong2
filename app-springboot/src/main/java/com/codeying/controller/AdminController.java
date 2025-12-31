package com.codeying.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.codeying.component.Result;
import com.codeying.component.PagerFooterVO;
import com.codeying.entity.Admin;
import com.codeying.entity.AdmissionProject;
import com.codeying.entity.Application;
import com.codeying.entity.Student;
import com.codeying.service.AdminService;
import com.codeying.service.AdmissionProjectService;
import com.codeying.service.ApplicationService;
import com.codeying.service.StudentService;
import com.codeying.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 管理员控制器 关于管理员的增删改查操作都在这 */
@Controller
@RequestMapping("admin")
public class AdminController extends BaseController {

  @Autowired
  protected AdminService adminService;

  @Autowired
  protected StudentService studentService;

  @Autowired
  protected ApplicationService applicationService;

  @Autowired
  protected AdmissionProjectService admissionProjectService;

  // 管理员列表页
  @RequestMapping("list")
  public String list(Model model, Integer pageIndex, Integer size, String username, String name) {
    if (pageIndex == null) {
      pageIndex = 1; // 默认访问第一页（分页）
    }
    if (size == null) {
      size = 15; // 默认每页15条
    }
    //查询条件
    QueryWrapper<Admin> paramMap = new QueryWrapper<>();
    paramMap.like(!StringUtils.isEmpty(username),"username", username);
    paramMap.like(!StringUtils.isEmpty(name),"name", name);
    paramMap.orderByDesc("id");
    // 开始分页
    IPage<Admin> pageInfo = new Page<Admin>().setCurrent(pageIndex).setSize(size);
    pageInfo = adminService.page(pageInfo, paramMap);
    //返回前端
    model.addAttribute("adminList", pageInfo.getRecords());
    model.addAttribute("pager",  new PagerFooterVO(pageInfo));
    //查询条件回显
    model.addAttribute("username", username);
    model.addAttribute("name", name);
    return "pages/admin-list";
  }

  // 管理员新增编辑页
  @RequestMapping("edit")
  public String edit(String id, Model model) {
    if(StringUtils.isEmpty(id)){
      return "pages/admin-add";
    }
    // 根据id获取到记录
    Admin entity = adminService.getById(id);
    model.addAttribute("item", entity);
    return "pages/admin-edit";
  }

  // 管理员详情页
  @RequestMapping("detail")
  public String detail(String id, Model model) {
    Admin entity = adminService.getById(id);
    model.addAttribute("item", entity);
    return "pages/admin-detail";
  }

  // 管理员保存
  @RequestMapping("save")
  @ResponseBody
  public Result<Object> save(Admin entityTemp) {
    String id = entityTemp.getId(); //主键
    if (id == null || id.isEmpty()) { // 新增
      entityTemp.setId(CommonUtils.newId());
      entityTemp.setCreatetime(new Date());
      // 唯一校验
      QueryWrapper<Admin> wrapperusername = new QueryWrapper<>();
      wrapperusername.eq("username", entityTemp.getUsername());
      if (!adminService.list(wrapperusername).isEmpty()) {
        return fail("用户名 已存在！");
      }
      adminService.save(entityTemp);
    } else {
      adminService.updateById(entityTemp);
      if (getCurrentUser().getId().equals(id)) {
        setSessionValue("user", adminService.getById(id));
      }
    }
    return Result.success("保存成功", null); // 返回保存成功
  }

  // 管理员删除
  @RequestMapping("delete")
  @ResponseBody
  public Result<Object> delete(String id) {
    // 根据ID删除记录
    boolean res = adminService.removeById(id);
    return res ? success() : fail();
  }

  // --- 学生管理 ---

  // 学生列表页
  @RequestMapping("student/list")
  public String studentList(Model model, Integer pageIndex, Integer size, String username, String name) {
    if (pageIndex == null) {
      pageIndex = 1;
    }
    if (size == null) {
      size = 15;
    }
    //查询条件
    QueryWrapper<Student> paramMap = new QueryWrapper<>();
    paramMap.like(!StringUtils.isEmpty(username),"username", username);
    paramMap.like(!StringUtils.isEmpty(name),"name", name);
    paramMap.orderByDesc("create_time");
    
    // 开始分页
    IPage<Student> pageInfo = new Page<Student>().setCurrent(pageIndex).setSize(size);
    pageInfo = studentService.page(pageInfo, paramMap);
    
    //返回前端
    model.addAttribute("list", pageInfo.getRecords());
    model.addAttribute("pager", new PagerFooterVO(pageInfo));
    //查询条件回显
    model.addAttribute("username", username);
    model.addAttribute("name", name);
    return "pages/admin-student-list";
  }

  // 学生删除
  @RequestMapping("student/delete")
  @ResponseBody
  public Result<Object> studentDelete(String id) {
    boolean res = studentService.removeById(id);
    return res ? success() : fail();
  }

  // --- 申请记录管理 ---

  // 申请记录列表页
  @RequestMapping("application/list")
  public String applicationList(Model model, Integer pageIndex, Integer size) {
    if (pageIndex == null) {
      pageIndex = 1;
    }
    if (size == null) {
      size = 15;
    }
    
    // 分页查询
    IPage<Application> pageInfo = new Page<Application>().setCurrent(pageIndex).setSize(size);
    QueryWrapper<Application> queryWrapper = new QueryWrapper<>();
    queryWrapper.orderByDesc("create_time");
    
    pageInfo = applicationService.page(pageInfo, queryWrapper);
    
    // 处理数据，补充学生姓名和项目名称（这里简单做，实际建议联表查询或VO）
    // 为了简单演示，我们暂不查询关联表，直接展示ID，或者您可以在Service层做VO转换
    // 这里我们简单做一个VO转换的示例，或者直接在页面显示ID
    // 鉴于时间，我们先传递原始数据，后续优化
    
    model.addAttribute("list", pageInfo.getRecords());
    model.addAttribute("pager", new PagerFooterVO(pageInfo));
    
    return "pages/admin-application-list";
  }

  // 申请审核
  @RequestMapping("application/audit")
  @ResponseBody
  public Result<Object> applicationAudit(String id, Integer status) {
    Application app = applicationService.getById(id);
    if (app == null) {
      return fail("申请记录不存在");
    }
    app.setStatus(status);
    app.setUpdateTime(new Date());
    boolean res = applicationService.updateById(app);
    return res ? success() : fail();
  }

  // --- 招生项目管理 ---

}

