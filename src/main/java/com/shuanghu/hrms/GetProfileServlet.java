package com.shuanghu.hrms;

import com.alibaba.fastjson.JSONObject;
import com.shuanghu.hrms.utils.JdbcUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/profile")
public class GetProfileServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        JSONObject result = new JSONObject();

        // 1. 从 Cookie 获取用户名
        String username = null;
        Cookie[] cookies = req.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    username = java.net.URLDecoder.decode(cookie.getValue(), "UTF-8");
                    break;
                }
            }
        }

        if (username == null) {
            result.put("error", "未登录");
            resp.getWriter().write(result.toJSONString());
            return;
        }

        // 2. 数据库查询：sys_user -> employee -> dept/title
        // 关联查询：用户表 -> 员工表 -> 部门表 -> 职称表
        String sql = "SELECT e.*, d.dept_name, t.title_name " +
                "FROM sys_user u " +
                "JOIN employee e ON u.emp_id = e.emp_id " +
                "LEFT JOIN dept d ON e.dept_id = d.dept_id " +
                "LEFT JOIN title t ON e.title_id = t.title_id " +
                "WHERE u.username = ?";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    result.put("success", true);
                    result.put("username", username); // 登录账号
                    result.put("empId", rs.getInt("emp_id"));
                    result.put("empNo", rs.getString("emp_no"));
                    result.put("name", rs.getString("emp_name"));
                    result.put("deptName", rs.getString("dept_name"));
                    result.put("titleName", rs.getString("title_name"));
                    result.put("phone", rs.getString("emp_phone"));
                    result.put("email", rs.getString("emp_email"));
                    result.put("address", "成都市双流区xx路"); // 数据库若无此字段，给个默认值或留空
                } else {
                    result.put("success", false);
                    result.put("message", "未找到关联的员工档案");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }

        resp.getWriter().write(result.toJSONString());
    }
}