package com.example.thumbnailator.infrastructure.constant;


/**
 * 文件相关常量类
 *
 * @author 机考（企业版）项目组
 * @date 2019/9/12
 */
public class FileConstant {
    
    private FileConstant() {
    }
    
    public static final int FILE_TYPE_UNKNOWN = 0;
    public static final int FILE_TYPE_DBF = 1;
    public static final int FILE_TYPE_TXT = 2;
    public static final int FILE_TYPE_EXCEL2003 = 3;
    public static final int FILE_TYPE_EXCEL2007 = 4;
    public static final int FILE_TYPE_CSV = 5;
    public static final int FILE_TYPE_ZIP = 6;
    
    public static final String DBF_SUFFIX = ".dbf";
    public static final String TXT_SUFFIX = ".txt";
    public static final String EXCEL2003_SUFFIX = ".xls";
    public static final String EXCEL2007_SUFFIX = ".xlsx";
    public static final String CSV_SUFFIX = ".csv";
    public static final String ZIP_SUFFIX = ".zip";
    public static final String RAR_SUFFIX = ".rar";
    public static final String BMP_SUFFIX = ".bmp";
    public static final String GIF_SUFFIX = ".gif";
    public static final String JPEG_SUFFIX = ".jpeg";
    public static final String JPG_SUFFIX = ".jpg";
    public static final String PNG_SUFFIX = ".png";
    public static final String PPT2007_SUFFIX = ".pptx";
    public static final String PPT2003_SUFFIX = ".ppt";
    public static final String DOCX2007_SUFFIX = ".docx";
    public static final String DOC2003_SUFFIX = ".doc";
    public static final String PDF_SUFFIX = ".pdf";
    
    
    public static final String VIDEO_3GP_SUFFIX = ".3gp";
    public static final String VIDEO_MP4_SUFFIX = ".mp4";
    public static final String VIDEO_AVI_SUFFIX = ".avi";
    public static final String VIDEO_WMV_SUFFIX = ".wmv";
    public static final String VIDEO_MKV_SUFFIX = ".mkv";
    public static final String VIDEO_FLV_SUFFIX = ".flv";
    public static final String VIDEO_RMVB_SUFFIX = ".rmvb";
    public static final String VIDEO_MOV_SUFFIX = ".mov";
    
    public static final String MP3_SUFFIX = ".mp3";
    public static final String AUDIO_WAV_SUFFIX = ".wav";
    
    public static final int EXCEL2003_MAX_ROW = 65536;
    public static final int EXCEL2007_MAX_ROW = 1048576;
    public static final int EXCEL2003_MAX_COLUMN = 256;
    public static final int EXCEL2007_MAX_COLUMN = 16384;
    
    
    /**
     * 文件类型
     */
    public static class FileType {
        
        /**
         * 文档
         */
        public static final String WORD = "word";
        
        /**
         * PPT文档
         */
        public static final String PPT = "ppt";
        
        /**
         * 制表文档
         */
        public static final String EXCEL = "excel";
        
        /**
         * PDF文档
         */
        public static final String PDF = "pdf";
        
        /**
         * 图片
         */
        public static final String IMAGE = "image";
        
        /**
         * 视频
         */
        public static final String VIDEO = "video";
        
        /**
         * 音频
         */
        public static final String AUDIO = "audio";
        
        public static final String FLASH = "flash";
        
        public static final String OTHER = "other";
        
        
        public static final String SVG = "svg";
        
        public static final String JPG = "jpg";
        
        public static final String ZIP = "zip";
    }
    
    
    public enum ImageType {
        JPG(1,"jpg"),PNG(2,"png");
    
        private Integer code;
        
        private String typeName;
    
        ImageType(Integer code, String typeName) {
            this.code = code;
            this.typeName = typeName;
        }
    
        public Integer getCode() {
            return code;
        }
    
        public String getTypeName() {
            return typeName;
        }
    }
    
}
