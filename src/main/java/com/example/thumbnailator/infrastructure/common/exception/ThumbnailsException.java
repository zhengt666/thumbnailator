package com.example.thumbnailator.infrastructure.common.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * BaseException.java
 *
 * @author taozheng
 * @date 2021/10/21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ThumbnailsException extends RuntimeException {
    
    private Integer code;
    
    private String message;
    
    private ThumbnailsException() {
    }
    
    public ThumbnailsException(Integer code,String message){
        this.code = code;
        this.message = message;
    }
    
    public ThumbnailsException(String message){
        this.code = 3000;
        this.message = message;
    }
    
    public static void error(String message){
        throw new ThumbnailsException(message);
    }
    
    
}
