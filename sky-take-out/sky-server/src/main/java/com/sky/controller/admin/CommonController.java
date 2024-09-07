package com.sky.controller.admin;

import com.alibaba.druid.sql.dialect.oracle.ast.stmt.OracleInsertStatement;
import com.sky.constant.MessageConstant;
import com.sky.result.Result;
import com.sky.utils.AliOssUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

/**
 * 公共业务接口
 *
 * @author xyzZero3
 * @date 2024/9/6 22:56
 */
@Api(tags = "公共业务接口")
@RestController
@RequestMapping("/admin/common")
@Slf4j
public class CommonController {

    @Autowired
    private AliOssUtil ossUtil;

    @ApiOperation(value = "上传文件")
    @PostMapping("/upload")
    public Result<String> upload(@RequestParam("file") MultipartFile file) {
        log.info("上传的文件：" + file);
        try {
            // 原始文件名
            String originalFilename = file.getOriginalFilename();
            // 截取文件名后缀
            String filePrefix = originalFilename.substring(originalFilename.lastIndexOf("."));
            // 构造新的文件名称
            String objectName = UUID.randomUUID().toString() + filePrefix;

            // 返回文件的访问地址
            String fileUrl = ossUtil.upload(file.getBytes(), objectName);
            return Result.success(fileUrl);
        } catch (IOException e) {
            log.error("文件上传失败：" + e);
        }
        return Result.error(MessageConstant.UPLOAD_FAILED);
    }

}
