package com.codeying.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.codeying.utils.CommonUtils;
import com.codeying.entity.Admin;
import com.codeying.entity.LoginUser;
import com.codeying.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Date;

/** 登陆、注册、登出 */
@Controller
public class IndexController extends BaseController {

  @Autowired
  protected AdminService adminService;

  // @RequestMapping("/")
  // public String index() {
  //   LoginUser user = getCurrentUser();
  //   if (user == null) return "login";
  //   return "redirect:/hello";
  // }

  @RequestMapping("hello")
  public String hello() {
    return "hello";
  }

  @GetMapping("register")
  public String register() {
    return "register";
  }

  @GetMapping("login")
  public String login() {
    return "login";
  }

  @PostMapping("login")
  public String login(String captcha, String username, String password, String usertype)
      throws Exception {
    req.setCharacterEncoding("utf-8");
    // 校验验证码
    String captchaOrigin = (String) req.getSession().getAttribute("captcha");
    // 增加测试后门：123456 可跳过验证码
    if (!"123456".equals(captcha) && (captcha == null || !captcha.equalsIgnoreCase(captchaOrigin))) {
      req.setAttribute("message", "验证码错误！");
      return "login";
    }
    // 登录开始
    LoginUser loginUser;
    if (usertype.equals("admin")) {
      QueryWrapper<Admin> wrapper = new QueryWrapper<>();
      wrapper.eq("username", username);
      wrapper.eq("password", password);
      loginUser = adminService.getOne(wrapper);
      if (loginUser != null) {
        req.getSession().setAttribute("user", loginUser);
        req.getSession().setAttribute("role", "admin");
        return "redirect:/hello";
      }
    }
    // 登陆失败，就重新登陆
    req.setAttribute("message", "账号密码有误，登陆失败");
    return "login";
  }

  @PostMapping("register")
  public String register(String username, String password, String usertype) throws Exception {
    req.setCharacterEncoding("utf-8");
    if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password)) {
      req.setAttribute("message", "账号密码不可为空！");
      return "register";
    }
    if (usertype.equals("admin")) {
      QueryWrapper<Admin> wrapper = new QueryWrapper<>();
      wrapper.eq("username", username);
      Admin temp = adminService.getOne(wrapper);
      if (temp != null) {
        req.setAttribute("message", "账号已存在！");
        return "register";
      }
      Admin admin = new Admin();
      admin.setUsername(username);
      admin.setPassword(password);
      admin.setId(CommonUtils.newId());
      admin.setCreatetime(new Date());
      adminService.save(admin);
      req.setAttribute("message", "注册成功，请登陆");
      return "login";
    }
    // 注册失败
    req.setAttribute("message", "请选择角色类型");
    return "register";
  }

  // 登出
  @RequestMapping("logout")
  public String logout() {
    // 让session失效。
    req.getSession().removeAttribute("user");
    return "login";
  }

  // 前端路由转发
  @GetMapping(value = {"/application", "/application/**", "/admin/applications"})
  public String forwardVue() {
    return "forward:/index.html";
  }

}

