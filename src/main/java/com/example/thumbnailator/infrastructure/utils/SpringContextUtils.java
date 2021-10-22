package com.example.thumbnailator.infrastructure.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
/**
 * Spring上下文工具类（整合了Quartz上下文）
 *
 * @author 机考（企业版）项目组
 * @date 2020/6/9
 */
@Component
public class SpringContextUtils  implements ApplicationContextAware {
    
    private static ApplicationContext context;


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getContext() {
        return context;
    }
}
