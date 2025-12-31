package com.codeying.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 申请记录实体类
 */
@Data
@TableName("applications")
public class Application implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId
    private String id;

    /** 学生ID */
    @TableField("student_id")
    private String studentId;

    /** 项目ID */
    @TableField("project_id")
    private String projectId;

    /** 状态: 0-待审核, 1-通过, 2-拒绝 */
    @TableField("status")
    private Integer status;

    /** 申请材料(JSON格式) */
    @TableField("documents")
    private String documents;

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

    /** 学生姓名 (非数据库字段) */
    @TableField(exist = false)
    private String studentName;

    /** 项目名称 (非数据库字段) */
    @TableField(exist = false)
    private String projectName;
}
