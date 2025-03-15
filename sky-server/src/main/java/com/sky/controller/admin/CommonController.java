package com.sky.controller.admin;

import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@RestController
@RequestMapping("/admin/common")
@Api("通用接口")
@Slf4j
public class CommonController {
    private final AliOssUtil aliOssUtil;

    public CommonController(AliOssUtil aliOssUtil) {
        this.aliOssUtil = aliOssUtil;
    }

    @PostMapping("/upload")
    @ApiOperation("文件上传")
    public Result<String> upload(MultipartFile file) {
        log.info("文件上传:{}",file);
//        String orginnalFilename = file.getOriginalFilename();
//        String homeDirectory = System.getProperty("user.home"); // 获取用户主目录
//        // 存储到本地
//        file.transferTo(new File(homeDirectory + "/Desktop/images/"+orginnalFilename));

        try {
            String originalFilename = file.getOriginalFilename();
            //截取文件后缀
            String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
            String ObjectName = UUID.randomUUID().toString()+extension;
            aliOssUtil.upload(file.getBytes(), ObjectName);
            //文件请求路径
            String filePath = aliOssUtil.upload(file.getBytes(), ObjectName);
            return Result.success(filePath);
        } catch (IOException e) {
            log.info("文件上传失败");
        }
        return Result.error("上传失败");
    }
}
