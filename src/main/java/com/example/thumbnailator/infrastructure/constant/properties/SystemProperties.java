package com.example.thumbnailator.infrastructure.constant.properties;

import java.io.File;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import lombok.Getter;
import lombok.Setter;

/**
 * 安全相关配置
 *
 * @author Bennu
 * @date 2020/5/17
 */
@Configuration
@Getter
@Setter
public class SystemProperties {
    
    /**
     * 本地文件目录
     */
    @Value("${setting.local-file-root-path:/thumbnails}")
    private String localFileRootPath = File.separator + "thumbnails";
}
