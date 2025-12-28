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
 * 招生项目实体类
 */
@Data
@TableName("admission_projects")
public class AdmissionProject implements Serializable {

    private static final long serialVersionUID = 1L;

    /** 主键ID */
    @TableId
    private String id;

    /** 项目名称 */
    @TableField("name")
    private String name;

    /** 招生年份 */
    @TableField("year")
    private Integer year;

    /** 项目描述 */
    @TableField("description")
    private String description;

    /** 申请截止日期 */
    @TableField("apply_deadline")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date applyDeadline;

    /** 状态: 1-启用, 0-禁用 */
    @TableField("status")
    private Integer status;

    /** 创建时间 */
    @TableField("create_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date createTime;

    /** 更新时间 */
    @TableField("update_time")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private Date updateTime;
}
