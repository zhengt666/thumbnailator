package com.example.thumbnailator.application.service;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import net.coobird.thumbnailator.Thumbnails;

import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.multipart.MultipartFile;

import com.example.thumbnailator.infrastructure.common.exception.ThumbnailsException;
import com.example.thumbnailator.infrastructure.constant.FileConstant;
import com.example.thumbnailator.infrastructure.constant.FileConstant.ImageType;
import com.example.thumbnailator.infrastructure.utils.DateTimeUtils;
import com.example.thumbnailator.infrastructure.utils.FileUtils;
import com.example.thumbnailator.infrastructure.utils.ImageUtils;
import com.example.thumbnailator.infrastructure.utils.ZipUtils;

/**
 * ThumbnailsService.java
 *
 * @author taozheng
 * @date 2021/10/22
 */
@Service
public class ThumbnailsService {
    
    /**
     * 解压zip,压缩图片
     *
     * @param file        zip文件
     * @param maxkb       压缩最大大小
     * @param photoHeight 图片高
     * @param photoWidth  图片宽
     * @param quality     质量
     * @param fileType    文件类型
     */
    public void compressPhoto(MultipartFile file, Integer maxkb, Integer photoHeight, Integer photoWidth,
            Float quality,
            String fileType) throws IOException {
        if (FileConstant.FileType.ZIP.equalsIgnoreCase(file.getContentType())) {
            ThumbnailsException.error("仅支持zip压缩包格式");
        }
        List<String> imageTypes = getImageTypes();
        if (!imageTypes.contains(fileType)) {
            ThumbnailsException.error(String.format("暂不支持此格式“%s”转换", fileType));
        }
        File zip = null;
        try {
            //将zip保存至服务器
            LocalDateTime now = LocalDateTime.now();
            String tempPath = FileUtils.getTempPath(DateTimeUtils.getFormatDateTimeExcludeHms(now), UUID.randomUUID().toString());
            String zipPath = tempPath + File.separator + file.getOriginalFilename();
            zip = new File(zipPath);
            file.transferTo(zip);
            //解压文件
            String destDirPath = ZipUtils.zipUncompress(zipPath);
            //压缩后，存放压缩图片目录
            String compressPath = destDirPath + File.separator + "compress";
            File compressFile = new File(compressPath);
            if (!compressFile.exists()) {
                compressFile.mkdir();
            }
            //获取目录下所有文件
            List<File> imageList = FileUtils.getFiles(destDirPath);
            //不为空。开始压缩
            if (!CollectionUtils.isEmpty(imageList)) {
                imageList.forEach(i -> {
                    try {
                        //压缩转换成bytes
                        byte[] bytes = ImageUtils.compressPhotoV2(i, maxkb, photoWidth, photoHeight, quality, fileType);
                        //创建压缩图片
                        //String replace = i.getName().replace("png", "jpg");
                        //String fileName = compressPath + File.separator + replace;
                        String fileName = compressPath + File.separator + i.getName();
                        ImageUtils.createImage(bytes, fileName, fileType);
                    } catch (IOException e) {
                        ThumbnailsException.error(e.getMessage());
                    }
                });
            }
        } finally {
            FileUtils.deleteFolderOrFile(zip);
        }
    }
    
    private List<String> getImageTypes() {
        ImageType[] imageTypes = ImageType.values();
        List<String> fileTypes = new ArrayList<>();
        for (ImageType imageType : imageTypes) {
            fileTypes.add(imageType.getTypeName());
        }
        return fileTypes;
    }
}
