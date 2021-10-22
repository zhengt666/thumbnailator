package com.example.thumbnailator.infrastructure.common.result;

import lombok.Data;

/**
 * ThumbnailsResult.java
 *
 * @author taozheng
 * @date 2021/10/21
 */
@Data
public  class ThumbnailsResult<T> {
    
    private Integer code;
    
    private T data;
    
    private ThumbnailsResult(Integer code, T data) {
        this.code = code;
        this.data = data;
    }
    
    private ThumbnailsResult(T data) {
        this.code = 200;
        this.data = data;
    }
    
    public static <T> ThumbnailsResult<T> getInstance(){
        return new ThumbnailsResult<>(null);
    }
    
    public static <T> ThumbnailsResult<T> getInstance(T data){
        return new ThumbnailsResult<>(data);
    }
    
    public static <T> ThumbnailsResult<T> getInstance(Integer code,T data){
        return new ThumbnailsResult<>(code,data);
    }
}
