package com.codeying.controller;

import com.codeying.component.Result;
import com.codeying.utils.CommonUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * 文件上传控制器
 */
@RestController
@RequestMapping("/api/common")
public class UploadController extends BaseController {

    // 绑定上传路径，这里简单硬编码或读取配置，假设存放在 static/upload 下方便访问
    // 实际生产应存放在OSS或独立文件服务器
    // 这里为了演示方便，存放在 target/classes/static/upload 和 src/main/resources/static/upload
    
    @PostMapping("/upload")
    public Result<Map<String, String>> upload(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return fail("上传文件不能为空");
        }

        try {
            // 获取原文件名
            String originalFilename = file.getOriginalFilename();
            // 获取后缀
            String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 生成新文件名
            String newFileName = CommonUtils.newId() + suffix;
            
            // 日期目录
            String datePath = new SimpleDateFormat("yyyyMMdd").format(new Date());
            
            // 相对路径
            String relativePath = "/upload/" + datePath + "/" + newFileName;
            
            // 物理路径 (获取项目根目录下的 static 目录)
            // 注意：Spring Boot 运行 jar 包时无法直接写入 static，这里仅作为开发环境演示
            // 建议配置 file: 协议的静态资源映射
            String projectPath = System.getProperty("user.dir");
            String uploadPath = projectPath + "/app-springboot/src/main/resources/static/upload/" + datePath + "/";
            
            File dest = new File(uploadPath);
            if (!dest.exists()) {
                dest.mkdirs();
            }
            
            // 保存文件
            File saveFile = new File(uploadPath + newFileName);
            file.transferTo(saveFile);
            
            // 同时复制一份到 target 目录，以便无需重启即可访问 (开发环境 Trick)
            String targetPath = projectPath + "/app-springboot/target/classes/static/upload/" + datePath + "/";
            File targetDir = new File(targetPath);
            if (!targetDir.exists()) {
                targetDir.mkdirs();
            }
            org.springframework.util.FileCopyUtils.copy(saveFile, new File(targetPath + newFileName));

            Map<String, String> data = new HashMap<>();
            data.put("url", relativePath);
            data.put("name", originalFilename);
            
            return Result.success(data);
        } catch (IOException e) {
            e.printStackTrace();
            return fail("文件上传失败: " + e.getMessage());
        }
    }
}
