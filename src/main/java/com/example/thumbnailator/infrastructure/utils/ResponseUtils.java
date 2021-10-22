package com.example.thumbnailator.infrastructure.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;


/**
 * ResponseUtils.java
 *
 * @author taozheng
 * @date 2021/10/21
 */
public class ResponseUtils {
    
    private ResponseUtils() {
    }
    
    private static final String DOWNLOAD_CONTENT_TYPE = "application/x-download";
    private static final String FILE_NAME = "File-Name";
    

    
  
    
    public static void outZip(HttpServletResponse response, String fileName, List<String> fileList) throws IOException {
        //不通过浏览器打开文件
        response.setContentType(DOWNLOAD_CONTENT_TYPE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + "\"");
        response.setHeader(FILE_NAME, URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, FILE_NAME);
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (int i = 0; fileList != null && i < fileList.size(); i++) {
                File f = new File(fileList.get(i));
                zipOut.putNextEntry(new ZipEntry(f.getName()));
                try (FileInputStream fis = new FileInputStream(f)) {
                    byte[] buffer = new byte[1024];
                    int r = 0;
                    while ((r = fis.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, r);
                    }
                }
            }
        }
    }
    
    public static void outZipV2(HttpServletResponse response, String fileName, List<String> fileList)
            throws IOException {
        // 不通过浏览器打开文件
        response.setContentType(DOWNLOAD_CONTENT_TYPE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + "\"");
        response.setHeader(FILE_NAME, URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, FILE_NAME);
        
        // 创建list用于删除临时文件
        List<File> files = new ArrayList<>();
        try (ZipOutputStream zipOut = new ZipOutputStream(response.getOutputStream())) {
            for (int i = 0; fileList != null && i < fileList.size(); i++) {
                File f = new File(fileList.get(i));
                files.add(f);
                zipOut.putNextEntry(new ZipEntry(f.getName()));
                try (FileInputStream fis = new FileInputStream(f)) {
                    byte[] buffer = new byte[1024];
                    int r = 0;
                    while ((r = fis.read(buffer)) != -1) {
                        zipOut.write(buffer, 0, r);
                    }
                }
            }
        } finally {
            // 删除临时文件
            for (File file : files) {
                if (file != null) {
                    FileUtils.deleteFolderOrFile(file);
                }
            }
        }
    }
    
    public static void outFile(HttpServletResponse response, File file) throws IOException {
        response.setContentType(DOWNLOAD_CONTENT_TYPE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()) + "\"");
        response.setHeader(FILE_NAME, URLEncoder.encode(file.getName(), StandardCharsets.UTF_8.name()));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, FILE_NAME);
        
        try (OutputStream fileOut = response.getOutputStream()) {
            fileOut.write(FileUtils.readFileToByteArray(file));
            fileOut.flush();
        }
    }
    
    public static void outFile(HttpServletResponse response, String fileName, InputStream inputStream)
            throws IOException {
        response.setContentType(DOWNLOAD_CONTENT_TYPE);
        response.setHeader(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" +
                URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()) + "\"");
        response.setHeader(FILE_NAME, URLEncoder.encode(fileName, StandardCharsets.UTF_8.name()));
        response.setHeader(HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS, FILE_NAME);
        
        try (OutputStream fileOut = response.getOutputStream()) {
            int len = 0;
            byte[] buffer = new byte[8192];
            while ((len = inputStream.read(buffer)) != -1) {
                fileOut.write(buffer, 0, len);
            }
            fileOut.flush();
        }
    }
}

