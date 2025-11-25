package com.example.yddmall.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.io.File;

// 工具类，用于获取图片上传的绝对路径
@Component // 注入到 Spring 容器，供上传接口使用
public class UploadPathUtil {

    @Value("${upload.base-path}")
    private String basePath;

    /**
     * 根据用户 ID 获取专属上传目录（绝对路径），目录不存在就创建
     */
    public String getAbsoluteDir(String userId) {
        File dir = new File(basePath, userId);
        if (!dir.exists() && !dir.mkdirs() && !dir.isDirectory()) {
            throw new RuntimeException("创建目录失败:" + dir.getAbsolutePath());
        }
        return dir.getAbsolutePath() + File.separator;
    }
}