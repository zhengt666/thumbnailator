package com.example.thumbnailator.infrastructure.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import com.example.thumbnailator.infrastructure.common.exception.ThumbnailsException;

/**
 * ZipUtils.java
 *
 * @author taozheng
 * @date 2021/10/21
 */
public class ZipUtils {
    
    public static String zipUncompress(String inputFile) throws IOException {
        File srcFile = new File(inputFile);
        // 判断源文件是否存在
        if (!srcFile.exists()) {
            throw new ThumbnailsException(srcFile.getPath() + "所指文件不存在");
        }
        String destDirPath = inputFile.replace(".zip", "");
        //创建压缩文件对象
        ZipFile zipFile = new ZipFile(srcFile, Charset.forName("gbk"));
        //开始解压
        Enumeration<?> entries = zipFile.entries();
        while (entries.hasMoreElements()) {
            ZipEntry entry = (ZipEntry) entries.nextElement();
            // 如果是文件夹，就创建个文件夹
            if (entry.isDirectory()) {
                srcFile.mkdirs();
            } else {
                // 如果是文件，就先创建一个文件，然后用io流把内容copy过去
                File targetFile = new File(destDirPath + "/" + entry.getName());
                // 保证这个文件的父文件夹必须要存在
                if (!targetFile.getParentFile().exists()) {
                    targetFile.getParentFile().mkdirs();
                }
                targetFile.createNewFile();
                // 将压缩文件内容写入到这个文件中
                InputStream is = zipFile.getInputStream(entry);
                FileOutputStream fos = new FileOutputStream(targetFile);
                int len;
                byte[] buf = new byte[1024];
                while ((len = is.read(buf)) != -1) {
                    fos.write(buf, 0, len);
                }
                // 关流顺序，先打开的后关闭
                fos.close();
                is.close();
            }
        }
        return destDirPath;
    }
    
    public static void readFiles(String inputFile) throws Exception {
        File srcFile = new File(inputFile);
        if (srcFile.isDirectory()) {
            File next[] = srcFile.listFiles();
            for (int i = 0; i < next.length; i++) {
                System.out.println(next[i].getName());
                if(!next[i].isDirectory()){
                    BufferedReader br = new BufferedReader(new FileReader(next[i]));
                    List<String> arr1 = new ArrayList<>();
                    String contentLine ;
                    while ((contentLine = br.readLine()) != null) {
                        arr1.add(contentLine);
                    }
                    System.out.println(arr1);
                }
                
            }
        }
    }

}
