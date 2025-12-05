package com.shuanghu.hrms.config;

import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.ErrorPageRegistrar;
import org.springframework.boot.web.server.ErrorPageRegistry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

/**
 * 错误页面配置类
 * 配置Spring Boot错误页面重定向到自定义错误页面
 */
@Component
public class ErrorPageConfig implements ErrorPageRegistrar {
    
    @Override
    public void registerErrorPages(ErrorPageRegistry registry) {
        // 404错误页面
        ErrorPage error404Page = new ErrorPage(HttpStatus.NOT_FOUND, "/error.html?status=404&message=页面未找到");
        
        // 500错误页面
        ErrorPage error500Page = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error.html?status=500&message=服务器内部错误");
        
        // 400错误页面
        ErrorPage error400Page = new ErrorPage(HttpStatus.BAD_REQUEST, "/error.html?status=400&message=请求参数错误");
        
        // 其他错误页面
        ErrorPage error403Page = new ErrorPage(HttpStatus.FORBIDDEN, "/error.html?status=403&message=访问被拒绝");
        ErrorPage error503Page = new ErrorPage(HttpStatus.SERVICE_UNAVAILABLE, "/error.html?status=503&message=服务不可用");
        
        registry.addErrorPages(error404Page, error500Page, error400Page, error403Page, error503Page);
    }
}