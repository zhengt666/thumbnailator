package com.example.thumbnailator.infrastructure.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.example.thumbnailator.infrastructure.common.exception.ThumbnailsException;
import com.example.thumbnailator.infrastructure.constant.FileConstant;
import com.example.thumbnailator.infrastructure.constant.properties.SystemProperties;

public class FileUtils extends org.apache.commons.io.FileUtils {
    
    private FileUtils() {
    }
    
    private static final SystemProperties SECURITY_PROPERTIES;
    
    static {
        SECURITY_PROPERTIES = SpringContextUtils.getContext().getBean(SystemProperties.class);
    }
    
    /**
     * 获取纯文档的编码
     * <p>
     * 接口返回值仅供参考，不能保证完全正确 注：实测没有BOM头的UTF文件会被误识别为GB2312
     *
     * @param filePath 文件路径
     */
    public static String getCharset(String filePath) throws IOException {
        try (InputStream inputStream = new FileInputStream(filePath)) {
            byte[] head = new byte[3];
            if (inputStream.read(head) == -1) {
                throw new IOException("文件乱码，无法识别字符集");
            }
            //或GBK
            String charset = "gb2312";
            if (head[0] == -1 && head[1] == -2) {
                charset = "UTF-16";
            } else if (head[0] == -2 && head[1] == -1) {
                charset = "Unicode";
            } else if (head[0] == -17 && head[1] == -69 && head[2] == -65) {
                charset = "UTF-8";
            }
            return charset;
        }
    }
    
    
    public static boolean exists(String fileName) {
        return exists(new File(fileName));
    }
    
    public static boolean exists(File file) {
        return file != null && file.exists();
    }
    
    public static void deleteFolderOrFile(File file) {
        if (file == null) {
            throw new IllegalArgumentException(" The File is Empty ");
        }
        deleteFolderOrFile(file, null, false);
    }
    
    
    /**
     * 删除目录、或文件。注意：如果失败，有可能已经删除了一部分文件，只有全部删除成功才返回true
     * <pre>
     * 1、为文件时，删除；
     * 2、为目录时，suffix为null,删除所有文件；
     * 3、suffix不为null时，filterSuffix为true,只删除匹配后缀的文件;filterSuffix为false,只删除不匹配后缀的文件
     * </pre>
     *
     * @param folderOrFile File 目录或文件
     * @param suffix       String 为目录时，uffis不为null时，只删除匹配后缀的文件；suffis为null,删除所有文件
     * @param filterSuffix suffix不为空时，有效。filterSuffix为true,只删除匹配后缀的文件;filterSuffix为false,只删除不匹配后缀的文件
     * @return boolean 全部删除成功，返回true,否则返回false
     */
    public static boolean deleteFolderOrFile(File folderOrFile, String suffix, boolean filterSuffix) {
        File[] allFiles;
        if (folderOrFile == null || !folderOrFile.exists()) {
            return true;
        }
        if (folderOrFile.isFile()) {
            // 为文件
            return deleteQuietly(folderOrFile);
        }
        // 为目录, 得到该文件夹下的所有文件夹和文件数组
        allFiles = listFiles(folderOrFile, suffix, filterSuffix);
        if (allFiles == null) {
            // 空目录
            return deleteQuietly(folderOrFile);
        }
        // 目录是否已删除
        boolean hasDeleted = false;
        for (File file : allFiles) {
            // 为 false 时操作
            if (file.isDirectory()) {
                //如果为文件夹,则递归调用删除文件夹的方法
                hasDeleted = deleteFolderOrFile(file, suffix, filterSuffix);
            } else if (file.isFile()) {
                //删除失败,返回false
                hasDeleted = deleteQuietly(file);
            }
            if (!hasDeleted) {
                break;
            }
        }
        if (hasDeleted) {
            //该文件夹已为空文件夹,删除它
            return deleteQuietly(folderOrFile);
            
        }
        return false;
    }
    
    /**
     * 返回表示此抽象路径名所表示目录中的文件和目录的抽象路径名数组. 1、suffis为null,返回所有文件； 2、suffis不为null时，filterSuffix为true,只返回与后缀匹配后缀的文件;filterSuffix为false,只返回与后缀不匹配后缀的文件
     *
     * @param file         查找的路径
     * @param suffix       String 后缀,不区分大小写，如 "jsp",可以为null
     * @param filterSuffix boolean suffix不为null时，有效。filterSuffix为true,只返回与后缀匹配后缀的文件;filterSuffix为false,只返回与后缀不匹配后缀的文件
     * @return File[] 此抽象路径名所表示目录中的文件和目录的抽象路径名数组
     */
    public static File[] listFiles(File file, String suffix,
            boolean filterSuffix) {
        if (file == null) {
            return new File[]{};
        }
        if (suffix == null) {
            //返回一个抽象路径名数组，这些路径名表示此抽象路径名所表示目录中的文件。
            return file.listFiles();
        } else {
            return file.listFiles(new SuffixFilter(suffix, filterSuffix));
        }
    }
    
    /**
     * FilenameFilter
     */
    private static class SuffixFilter implements FilenameFilter {
        
        private final String suffix;
        private final boolean filterSuffix;
        
        SuffixFilter(String suffix, boolean filterSuffix) {
            this.suffix = suffix;
            this.filterSuffix = filterSuffix;
        }
        
        @Override
        public boolean accept(File dir, String name) {
            File file = new File(dir, name);
            int index = name.lastIndexOf('.');
            String suf = name.substring(index + 1);
            //跳过目录
            if (file.isDirectory()) {
                return true;
                //包含过滤的文件(index!=-1因为有些文件没有后缀)
            } else if (index != -1 && suf.equalsIgnoreCase(suffix)) {
                return filterSuffix;
            }
            return !filterSuffix;
        }
    }
    
    public static void createNotExistsDirs(String dirs) throws IOException {
        File dir = new File(dirs);
        if (!dir.exists() && !dir.mkdirs()) {
            throw new IOException("Make Directory: " + dir + " Failed! ");
        }
    }
    
    /**
     * 获取文件类型
     */
    public static String getFileSuffix(String filename) {
        final String dot = ".";
        if (StringUtils.isNotBlank(filename) && filename.lastIndexOf(dot) >= 0) {
            return filename.substring(filename.lastIndexOf(dot));
        }
        return StringUtils.EMPTY;
    }
    
    /**
     * 获取文件后缀(不带.)
     */
    public static String getFileSuffixExcludeDot(String filename) {
        final String dot = ".";
        if (StringUtils.isNotBlank(filename) && filename.lastIndexOf(dot) >= 0) {
            return filename.substring(filename.lastIndexOf(dot) + 1);
        }
        return StringUtils.EMPTY;
    }
    
    public static boolean createNewFileWithPath(File f, boolean mkdirs) throws IOException {
        File parent = f.getParentFile();
        if (mkdirs && !parent.exists() && !parent.mkdirs()) {
            throw new IOException("Make Directory: " + parent + " Failed! ");
        }
        return f.createNewFile();
    }
    
    /**
     * 获取临时目录（不存在时先创建），
     *
     * @param childPaths 子路径（可为空）
     * @return 创建后的新路径
     * @throws IOException 创建路径异常
     */
    public static String getTempPath(String... childPaths) throws IOException {
        StringBuilder tempPath = new StringBuilder(SECURITY_PROPERTIES.getLocalFileRootPath());
        if (!SECURITY_PROPERTIES.getLocalFileRootPath().startsWith(File.separator)) {
            tempPath.append(File.separator);
        }
        if (childPaths.length == 0) {
            tempPath.append("common").append(File.separator);
        } else {
            for (String childPath : childPaths) {
                if (StringUtils.isNotBlank(childPath)) {
                    tempPath.append(childPath).append(File.separator);
                } else {
                    tempPath.append("common").append(File.separator);
                }
            }
        }
        File file = new File(tempPath.toString());
        forceMkdir(file);
        return file.getCanonicalPath();
    }
    
    /**
     * 递归读取文件(包括子目录下)
     */
    public static List<File> getFileList(String filePath, String suffix, boolean filterSuffix) {
        try {
            String strPath = FileUtils.getTempPath(filePath);
            ArrayList<File> returnFileList = new ArrayList<>();
            File[] files;
            File dir = new File(strPath);
            if (suffix == null) {
                files = dir.listFiles();
            } else {
                files = dir.listFiles(new SuffixFilter(suffix, filterSuffix));
            }
            if (files == null) {
                return new ArrayList<>();
            }
            for (File file : files) {
                if (file.isDirectory()) {
                    List<File> returnFileList1 = getFileList(file.getAbsolutePath(), suffix, filterSuffix);
                    returnFileList.addAll(returnFileList1);
                } else {
                    returnFileList.add(file);
                }
            }
            return returnFileList;
        } catch (IOException ex) {
            throw new ThumbnailsException("文件操作异常：" + ex.getMessage());
        }
    }
    
    /**
     * byte数组转文件
     *
     * @param bfile    文件数组
     * @param filePath 文件存放路径
     * @param fileName 文件名称
     */
    public static void byte2File(byte[] bfile, String filePath, String fileName) {
        File file = new File(filePath + fileName);
        try (FileOutputStream fos = new FileOutputStream(file);
                BufferedOutputStream bos = new BufferedOutputStream(fos)) {
            bos.write(bfile);
        } catch (Exception ex) {
            throw new ThumbnailsException(ex.getMessage());
        }
    }
    
    /**
     * 获取文件contentType
     *
     * @param filenameExtension 文件后缀
     * @return 文件contentType
     */
    public static String getContentType(String filenameExtension) {
        if (filenameExtension.equalsIgnoreCase(FileConstant.BMP_SUFFIX)) {
            return "image/bmp";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.GIF_SUFFIX)) {
            return "image/gif";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.JPEG_SUFFIX) ||
                filenameExtension.equalsIgnoreCase(FileConstant.JPG_SUFFIX) ||
                filenameExtension.equalsIgnoreCase(FileConstant.PNG_SUFFIX)) {
            return "image/jpg";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.TXT_SUFFIX)) {
            return "text/plain";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.PPT2007_SUFFIX) ||
                filenameExtension.equalsIgnoreCase(FileConstant.PPT2003_SUFFIX)) {
            return "application/vnd.ms-powerpoint";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.DOCX2007_SUFFIX) ||
                filenameExtension.equalsIgnoreCase(FileConstant.DOC2003_SUFFIX)) {
            return "application/msword";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.EXCEL2007_SUFFIX) ||
                filenameExtension.equalsIgnoreCase(FileConstant.EXCEL2003_SUFFIX)) {
            return "application/vnd.ms-excel";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.PDF_SUFFIX)) {
            return "application/pdf";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.VIDEO_3GP_SUFFIX)) {
            return "video/3gpp";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.VIDEO_MP4_SUFFIX)) {
            return "video/mp4";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.VIDEO_AVI_SUFFIX)) {
            return "video/x-msvideo";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.VIDEO_WMV_SUFFIX)) {
            return "video/x-ms-wmv";
        }
        if (filenameExtension.equalsIgnoreCase(FileConstant.MP3_SUFFIX) || filenameExtension.equalsIgnoreCase(
                FileConstant.AUDIO_WAV_SUFFIX)) {
            return "audio/mpeg";
        }
        return null;
    }
    
    
    /**
     * 将字节数转为合适的文件大小
     *
     * @param bytes 字节数
     * @return 文件大小（TB、GB、MB、KB）
     */
    public static String parseBytesToFileSize(Long bytes) {
        if (null == bytes) {
            return null;
        }
        final long KB = 1024;
        final long MB = KB * KB;
        final long GB = MB * KB;
        final long TB = GB * KB;
        BigDecimal total = new BigDecimal(bytes);
        double unit;
        String suffixValue = "";
        if (bytes < MB) {
            unit = KB;
            suffixValue = "k";
        } else if (bytes < GB) {
            unit = MB;
            suffixValue = "m";
        } else if (bytes < TB) {
            unit = GB;
            suffixValue = "G";
        } else {
            throw new ThumbnailsException("不支持文件大小为TB以上的转换");
        }
        return total.divide(BigDecimal.valueOf(unit), 1, RoundingMode.HALF_UP) + suffixValue;
    }
    
    /**
     *递归目录下所有文件
     * @param path 目录路径
     * @return 所有文件
     */
    public static List<File> getFiles(String path) {
        List<File> files = new ArrayList<>();
        File file = new File(path);
        File[] tempList = file.listFiles();
        
        for (int i = 0; i < tempList.length; i++) {
            if (tempList[i].isFile()) {
                files.add(tempList[i]);
                //文件名，不包含路径
                //String fileName = tempList[i].getName();
            }
            if (tempList[i].isDirectory()) {
                //这里就不递归了，
                List<File> fileList = getFiles(tempList[i].getPath());
                files.addAll(fileList);
            }
        }
        return files;
    }
    
    public static void main(String[] args) {
        File file = new File("C:\\Users\\taozheng4\\Desktop\\new\\1.png");
        System.out.println(file.toPath());
    }
}
