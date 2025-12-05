package com.shuanghu.hrms.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 错误处理控制器
 * 处理前端API请求的错误响应
 */
@RestController
@RequestMapping("/api/error")
public class ErrorController {
    
    /**
     * 处理404错误
     */
    @GetMapping("/not-found")
    public ResponseEntity<Map<String, Object>> handleNotFound(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.NOT_FOUND, "请求的资源不存在", request);
    }
    
    /**
     * 处理服务器内部错误
     */
    @GetMapping("/server-error")
    public ResponseEntity<Map<String, Object>> handleServerError(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR, "服务器内部错误", request);
    }
    
    /**
     * 处理参数错误
     */
    @GetMapping("/bad-request")
    public ResponseEntity<Map<String, Object>> handleBadRequest(HttpServletRequest request) {
        return buildErrorResponse(HttpStatus.BAD_REQUEST, "请求参数错误", request);
    }
    
    /**
     * 构建标准化的错误响应
     */
    private ResponseEntity<Map<String, Object>> buildErrorResponse(HttpStatus status, String message, HttpServletRequest request) {
        Map<String, Object> errorResponse = new HashMap<>();
        errorResponse.put("success", false);
        errorResponse.put("status", status.value());
        errorResponse.put("message", message);
        errorResponse.put("path", request.getRequestURI());
        errorResponse.put("timestamp", LocalDateTime.now().toString());
        errorResponse.put("error", status.getReasonPhrase());
        
        return new ResponseEntity<>(errorResponse, status);
    }
    
    /**
     * 通用的错误处理端点
     */
    @GetMapping("/handle")
    public ResponseEntity<Map<String, Object>> handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        String errorMessage = (String) request.getAttribute("javax.servlet.error.message");
        
        HttpStatus status = HttpStatus.INTERNAL_SERVER_ERROR;
        String message = "系统错误";
        
        if (statusCode != null) {
            try {
                status = HttpStatus.valueOf(statusCode);
                message = errorMessage != null ? errorMessage : status.getReasonPhrase();
            } catch (IllegalArgumentException e) {
                // 使用默认值
            }
        }
        
        return buildErrorResponse(status, message, request);
    }
}