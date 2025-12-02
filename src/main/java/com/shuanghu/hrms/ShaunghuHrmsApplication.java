package com.shuanghu.hrms;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication                 // 这行必须有！表示这是 Spring Boot 项目
@ServletComponentScan                  // 这行必须有！才能扫描你所有 @WebServlet
public class ShaunghuHrmsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShaunghuHrmsApplication.class, args);
    }
}