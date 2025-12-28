-- Ensure the table exists
CREATE TABLE IF NOT EXISTS students (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    student_no VARCHAR(50) NOT NULL COMMENT '学号',
    passport_no VARCHAR(50) COMMENT '护照号',
    name VARCHAR(100) NOT NULL COMMENT '姓名',
    email VARCHAR(100) COMMENT '邮箱',
    password VARCHAR(100) NOT NULL COMMENT '密码',
    status INT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id),
    UNIQUE KEY uk_student_no (student_no)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生表';

-- 创建管理员表
CREATE TABLE IF NOT EXISTS tb_admin (
    id varchar(32) comment '管理员主键',
    username varchar(20) comment '用户名',
    password varchar(20) comment '密码',
    name varchar(12) comment '姓名',
    tele varchar(11) comment '电话',
    createtime datetime comment '创建时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT '管理员表';

-- 招生项目表
CREATE TABLE IF NOT EXISTS admission_projects (
    id VARCHAR(32) NOT NULL COMMENT '主键ID',
    name VARCHAR(100) NOT NULL COMMENT '项目名称',
    year INT COMMENT '招生年份',
    description TEXT COMMENT '项目描述',
    apply_deadline DATETIME COMMENT '申请截止日期',
    status INT DEFAULT 1 COMMENT '状态: 1-启用, 0-禁用',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='招生项目表';
