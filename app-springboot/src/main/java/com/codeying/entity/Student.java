package com.codeying.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 学生实体类
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("students")
public class Student extends LoginUser implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 角色 */
    @TableField(exist = false)
    private String role = "student";
    
    @TableField(exist = false)
    private String rolech = "学生";

    /** 主键ID */
    @TableId
    private String id;

    /** 学号 */
    @TableField("student_no")
    private String studentNo;

    /** 护照号 */
    @TableField("passport_no")
    private String passportNo;

    /** 姓名 */
    @TableField("name")
    private String name;

    /** 邮箱 */
    @TableField("email")
    private String email;

    /** 密码 */
    @TableField("password")
    private String password;
    
    /** 用户名 */
    @TableField("username")
    private String username;

    /** 状态: 1-启用, 0-禁用 */
    @TableField("status")
    private Integer status;

    /** 创建时间 */
    @TableField("create_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
