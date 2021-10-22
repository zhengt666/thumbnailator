package com.example.thumbnailator.interfaces.facade;

import java.io.IOException;

import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.thumbnailator.application.service.ThumbnailsService;
import com.example.thumbnailator.infrastructure.common.result.ThumbnailsResult;

import lombok.RequiredArgsConstructor;

/**
 * ThumbnailsController.java
 *
 * @author taozheng
 * @date 2021/10/21
 */
@RestController
@RequestMapping("/thumbnails")
@RequiredArgsConstructor
public class ThumbnailsController {
    
    private final ThumbnailsService thumbnailsService;
    
    @PostMapping("/compressPhoto")
    public ThumbnailsResult<Object> compressPhoto(@ModelAttribute @RequestParam("file") MultipartFile file,
            @RequestParam Integer maxkb, @RequestParam(required = false) Integer photoWidth, @RequestParam(required = false) Integer photoHeight,
            @RequestParam Float quality, @RequestParam String fileType) throws IOException {
        thumbnailsService.compressPhoto(file,maxkb,photoHeight,photoWidth,quality,fileType);
        return ThumbnailsResult.getInstance();
    }
}
