package com.example.thumbnailator.infrastructure.utils;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import net.coobird.thumbnailator.Thumbnails;


import org.apache.commons.io.IOUtils;

import com.example.thumbnailator.infrastructure.common.exception.ThumbnailsException;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ImageUtils {
    
    private static final List<Float> qualityList = new ArrayList<>();
    
    static {
        qualityList.add(0.1F);
        qualityList.add(0.2F);
        qualityList.add(0.3F);
        qualityList.add(0.4F);
        qualityList.add(0.5F);
        qualityList.add(0.6F);
        qualityList.add(0.7F);
        qualityList.add(0.8F);
        qualityList.add(0.9F);
    }
    
    
    /**
     * 【压缩图片】到要求的图片标准:  宽[300,500],高[300,500];100kb;jpg/jpeg
     * 先按宽高压缩，压缩后如果还不满足100kb,则按质量递归压缩
     * @Title:compressPhoto
     * @Description: TODO
     * @date 2020年6月30日 下午2:11:52
     * @author yqwang
     * @param imgFile 源图片文件
     * @param maxkb 目标kb
     * @param photoWidth 目标宽度
     * @param photoHeight 模板高度
     * @param quality 质量
     * @return 处理后的文件byte[]
     * @throws Exception
     */
    public static byte[] compressPhoto(File imgFile,Integer maxkb,Integer photoWidth,Integer photoHeight,Float quality,String fileType) throws IOException{
        // 1.压缩图片是否存在
        if(imgFile == null || !imgFile.exists()){
            throw new ThumbnailsException("图片文件不存在。");
        }
        byte[] bytes = readFileToByteArray(imgFile);
        if( bytes.length == 0){
            throw new ThumbnailsException("图片文件为空。");
        }
        
        // 2.是否超过100kb?没超过就不处理图片
        long fileSize = bytes.length;
        if (fileSize <= maxkb * 1024) {
            log.info("图片不超过{}kb,无需压缩。",maxkb);
            return bytes;
        }
        
        // 3.压缩到300-500宽高，100kb
        if (Objects.nonNull(photoHeight) && Objects.nonNull(photoWidth)) {
            BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
            int imgWidth = bim.getWidth();
            int imgHeight = bim.getHeight();
    
            // 3.1.先按宽高判断是否需要缩放  outputQuality=1确保画质清晰
            if (imgWidth >= photoWidth && imgHeight >= photoHeight) {
                log.info("先按宽{}高{}压缩。", photoWidth, photoHeight);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                //Closing a <tt>ByteArrayOutputStream</tt> has no effect.   因此无需close
                Thumbnails.of(new ByteArrayInputStream(bytes)).size(photoWidth, photoHeight).outputQuality(1)
                        .outputFormat(fileType).toOutputStream(out);
                bytes = out.toByteArray();
            }
        }
        // 3.2.判断按宽高缩放后是否超过100kb,超过递归就按质量压缩（图片会变得越来越模糊！）
        bytes = compressPhotoByQuality(bytes, quality, maxkb,fileType);
        return bytes;
    }
    
    /**
     * 【压缩图片】到要求的图片标准:  宽[300,500],高[300,500];100kb;jpg/jpeg
     * 先按宽高压缩，压缩后如果还不满足100kb,则按质量递归压缩
     * @Title:compressPhoto
     * @Description: TODO
     * @date 2020年6月30日 下午2:11:52
     * @author yqwang
     * @param imgFile 源图片文件
     * @param maxkb 目标kb
     * @param photoWidth 目标宽度
     * @param photoHeight 模板高度
     * @param quality 质量
     * @return 处理后的文件byte[]
     * @throws Exception
     */
    public static byte[] compressPhotoV2(File imgFile,Integer maxkb,Integer photoWidth,Integer photoHeight,Float quality,String fileType) throws IOException{
        // 1.压缩图片是否存在
        if(imgFile == null || !imgFile.exists()){
            throw new ThumbnailsException("图片文件不存在。");
        }
        byte[] bytes = readFileToByteArray(imgFile);
        if( bytes.length == 0){
            throw new ThumbnailsException("图片文件为空。");
        }
        
        // 2.是否超过100kb?没超过就不处理图片
        long fileSize = bytes.length;
        if (fileSize <= maxkb * 1024) {
            log.info("图片不超过{}kb,无需压缩。",maxkb);
            return bytes;
        }
        
        // 3.压缩到300-500宽高，100kb
        if (Objects.nonNull(photoHeight) && Objects.nonNull(photoWidth)) {
            BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
            int imgWidth = bim.getWidth();
            int imgHeight = bim.getHeight();
            
            // 3.1.先按宽高判断是否需要缩放  outputQuality=1确保画质清晰
            if (imgWidth >= photoWidth && imgHeight >= photoHeight) {
                log.info("先按宽{}高{}压缩。", photoWidth, photoHeight);
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                //Closing a <tt>ByteArrayOutputStream</tt> has no effect.   因此无需close
                Thumbnails.of(new ByteArrayInputStream(bytes)).size(photoWidth, photoHeight).outputQuality(1)
                        .outputFormat(fileType).toOutputStream(out);
                bytes = out.toByteArray();
            }
        }
        // 3.2.判断按宽高缩放后是否超过100kb,超过递归就按质量压缩（图片会变得越来越模糊！）
        byte[] result = null;
        for (Float aFloat : qualityList) {
            bytes = compressPicByQuality(bytes, aFloat);
            if (bytes != null) {
                if (result == null) {
                    result = bytes;
                }
                float byteLength = Math.abs(bytes.length - maxkb * 1024);
                float resultLength = Math.abs(result.length - maxkb * 1024);
                if (byteLength < resultLength) {
                    result = bytes;
                }
            }
        }
        return result;
    }
    
    /**
     * 递归按quality质量处理，压缩到maxkb后返回新的bytes值
     * @Title:compressPhotoByQuality
     * @Description: TODO
     * @date 2020年6月30日 下午2:24:36
     * @author yqwang
     * @param bytes 源文件字节
     * @param quality 压缩质量 （如果>=1则不处理）
     * @param maxkb 要求压缩到的最大kb
     * @param fileType 如png或者jpg
     * @return
     * @throws IOException
     */
    public static byte[] compressPhotoByQuality(byte[] bytes, Float quality, long maxkb, String fileType) throws IOException{
        if(bytes == null){
            return bytes;
        }
        log.info("开始按质量压缩图片({}kb)。",bytes.length/1024);
        // 如果配置的>=1，则不再处理,多说无益
        if(quality >= 1){
            log.info("quality>=1,不执行压缩。");
            return bytes;
        }
        // 满足目标kb值，则返回
        long fileSize = bytes.length;
        if (fileSize <= maxkb * 1024) {
            log.info("图片文件{}kb<={}kb,不再压缩质量。",fileSize/1024,maxkb);
            return bytes;
        }
        // Closing a <tt>ByteArrayOutputStream</tt> has no effect.   因此无需close
        ByteArrayOutputStream out = null;
        out = new ByteArrayOutputStream();
        BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
        int imgWidth = bim.getWidth();
        int imgHeight = bim.getHeight();
        // 如果不处理size,只用quality,可能导致一致压缩不到目标值，一致递归在当前方法中！！
        int desWidth = new BigDecimal(imgWidth).multiply(new BigDecimal(quality)).intValue();
        int desHeight = new BigDecimal(imgHeight).multiply(new BigDecimal(quality)).intValue();
        log.info("图片文将按照width={}*height={}进行压缩，画质quality={}。",desWidth,desHeight,quality);
        Thumbnails.of(new ByteArrayInputStream(bytes)).size(desWidth, desHeight).outputQuality(quality).outputFormat(fileType).toOutputStream(out);
        //递归
        return compressPhotoByQuality(out.toByteArray(), quality, maxkb, fileType);
    }
    
    /**
     * 递归按quality质量处理，压缩到maxkb后返回新的bytes值
     * @Title:compressPhotoByQuality
     * @Description: TODO
     * @date 2020年6月30日 下午2:24:36
     * @author yqwang
     * @param bytes 源文件字节
     * @param quality 压缩质量 （如果>=1则不处理）
     * @param maxkb 要求压缩到的最大kb
     * @param fileType 如png或者jpg
     * @return
     * @throws IOException
     */
    public static byte[] compressPhotoByQualityV2(byte[] bytes, Float quality, long maxkb, String fileType) throws IOException{
        if(bytes == null){
            return bytes;
        }
        log.info("开始按质量压缩图片({}kb)。",bytes.length/1024);
        // 如果配置的>=1，则不再处理,多说无益
        if(quality >= 1){
            log.info("quality>=1,不执行压缩。");
            return bytes;
        }
        // 满足目标kb值，则返回
        long fileSize = bytes.length;
        if (fileSize <= maxkb * 1024) {
            log.info("图片文件{}kb<={}kb,不再压缩质量。",fileSize/1024,maxkb);
            return bytes;
        }
        // Closing a <tt>ByteArrayOutputStream</tt> has no effect.   因此无需close
        ByteArrayOutputStream out = null;
        out = new ByteArrayOutputStream();
        BufferedImage bim = ImageIO.read(new ByteArrayInputStream(bytes));
        int imgWidth = bim.getWidth();
        int imgHeight = bim.getHeight();
        log.info("图片文将按照width={}*height={}进行压缩，画质quality={}。",imgWidth,imgHeight,quality);
        Thumbnails.of(new ByteArrayInputStream(bytes)).size(imgWidth, imgHeight).outputQuality(quality).outputFormat(fileType).toOutputStream(out);
        //递归
        return out.toByteArray();
    }
    
    /**File to bytes[]*/
    public static byte[] readFileToByteArray(File f) {
        byte[] fileb = null;
        InputStream is = null;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        
        try {
            is = new FileInputStream(f);
            byte[] b = new byte[1024];
            
            int n;
            while ((n = is.read(b)) != -1) {
                out.write(b, 0, n);
            }
            
            fileb = out.toByteArray();
            return fileb;
        } catch (Exception var16) {
            throw new ThumbnailsException(var16.getMessage());
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (Exception var15) {
                    // do nothing
                }
            }
            
            if (out != null) {
                try {
                    out.close();
                } catch (Exception var14) {
                    // do nothing
                }
            }
            
        }
    }
    
    public static void main(String[] args) throws Exception {
        File imageFile = new File("C:\\Users\\taozheng4\\Desktop\\new\\banner2.e1d4cd67.png");
        byte[] bytes = compressPhoto(imageFile, 100, 500, 500, 0.99F,"png");
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        BufferedImage image = ImageIO.read(byteArrayInputStream);
        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(bs);
        assert image != null;
        ImageIO.write(image, "png", imageOutputStream);
        InputStream inputStream = new ByteArrayInputStream(bs.toByteArray());
        OutputStream outputStream = new FileOutputStream("C:\\Users\\taozheng4\\Desktop\\new\\1.png");
        IOUtils.copy(inputStream, outputStream);
        inputStream.close();
        outputStream.close();
    }
    
    /**
     * 创建图片
     * @param bytes 图片bytes数据
     * @param tempFilePath 图片临时保存路径
     * @param formatName 图片保存格式
     */
    public static void createImage(@NonNull byte[] bytes,String tempFilePath,String formatName){
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
            BufferedImage image = ImageIO.read(byteArrayInputStream);
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            ImageOutputStream imageOutputStream = ImageIO.createImageOutputStream(bs);
            assert image != null;
            ImageIO.write(image, formatName, imageOutputStream);
            inputStream = new ByteArrayInputStream(bs.toByteArray());
            outputStream = new FileOutputStream(tempFilePath);
            IOUtils.copy(inputStream, outputStream);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null){
                    inputStream.close();
                }
                if (outputStream != null){
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    
    
    
    public static String png2jpeg(String pngPath) {
        //读取图片
        FileOutputStream fos =null;
        String jpgPath = pngPath.replace("png","jpg");
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(pngPath));
            //转成jpeg、
            BufferedImage bufferedImage1 = new BufferedImage(bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_RGB);
            bufferedImage1.createGraphics().drawImage(bufferedImage,bufferedImage.getWidth(),bufferedImage.getHeight(), Color.white,null);
            fos = new FileOutputStream(jpgPath);
            ImageIO.write(bufferedImage,"jpg",fos);
            fos.flush();
        } catch (IOException e) {
            e.printStackTrace();
            try {
                fos.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
        return jpgPath;
    }
    
    public static void jpeg2png(String jpgPath) {
        //读取图片
        String pngPath = jpgPath.replace("jpg","png");
        try {
            BufferedImage bufferedImage = ImageIO.read(new File(jpgPath));
            //转成png、
            BufferedImage bufferedImage1 = new BufferedImage(bufferedImage.getWidth(),
                    bufferedImage.getHeight(),
                    BufferedImage.TYPE_INT_ARGB);
            bufferedImage1.createGraphics().drawImage(bufferedImage,0,0, Color.white,null);
            FileOutputStream fos = new FileOutputStream(pngPath);
            ImageIO.write(bufferedImage1,"png",fos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 压缩图片（通过降低图片质量）
     * @explain 压缩图片,通过压缩图片质量，保持原图大小
     * @param quality
     *       图片质量（0-1）
     * @return byte[]
     *      压缩后的图片（jpg）
     * @throws
     */
    public static byte[] compressPicByQuality(byte[] imgByte, float quality) {
        byte[] imgBytes = null;
        try {
            ByteArrayInputStream byteInput = new ByteArrayInputStream(imgByte);
            BufferedImage image = ImageIO.read(byteInput);
            
            // 如果图片空，返回空
            if (image == null) {
                return null;
            }
            // 得到指定Format图片的writer（迭代器）
            Iterator<ImageWriter> iter = ImageIO.getImageWritersByFormatName("jpeg");
            // 得到writer
            ImageWriter writer = (ImageWriter) iter.next();
            // 得到指定writer的输出参数设置(ImageWriteParam )
            ImageWriteParam iwp = writer.getDefaultWriteParam();
            // 设置可否压缩
            iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            // 设置压缩质量参数
            iwp.setCompressionQuality(quality);
            
            iwp.setProgressiveMode(ImageWriteParam.MODE_DISABLED);
            
            ColorModel colorModel = ColorModel.getRGBdefault();
            // 指定压缩时使用的色彩模式
            iwp.setDestinationType(
                    new javax.imageio.ImageTypeSpecifier(colorModel, colorModel.createCompatibleSampleModel(16, 16)));
            
            // 开始打包图片，写入byte[]
            // 取得内存输出流
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            IIOImage iIamge = new IIOImage(image, null, null);
            
            // 此处因为ImageWriter中用来接收write信息的output要求必须是ImageOutput
            // 通过ImageIo中的静态方法，得到byteArrayOutputStream的ImageOutput
            writer.setOutput(ImageIO.createImageOutputStream(byteArrayOutputStream));
            writer.write(null, iIamge, iwp);
            imgBytes = byteArrayOutputStream.toByteArray();
        } catch (IOException e) {
            System.out.println("write errro");
            e.printStackTrace();
        }
        return imgBytes;
    }
}
