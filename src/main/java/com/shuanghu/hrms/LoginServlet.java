package com.shuanghu.hrms;

import com.alibaba.fastjson.JSONObject;
import com.shuanghu.hrms.utils.JdbcUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 设置编码
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        String uName = req.getParameter("username");
        String uPass = req.getParameter("password");
        String uRole = req.getParameter("role"); // 前端传来的: admin, manager, employee, visitor

        JSONObject result = new JSONObject();

        if (uName == null || uPass == null || uRole == null) {
            result.put("success", false);
            result.put("message", "登录信息不完整");
            resp.getWriter().write(result.toJSONString());
            return;
        }

        try (Connection conn = JdbcUtil.getConnection()) {
            // 1. 查询用户 (根据用户名)
            String sql = "SELECT * FROM sys_user WHERE username = ?";
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, uName);

                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                        // 2. 验证密码 (这里演示明文对比，实际建议加密)
                        String dbPass = rs.getString("password");
                        if (!uPass.equals(dbPass)) {
                            result.put("success", false);
                            result.put("message", "密码错误");
                        } else {
                            // 3. 验证状态
                            int status = rs.getInt("user_status");
                            if (status != 1) {
                                result.put("success", false);
                                result.put("message", "该账号已被禁用，请联系管理员");
                            } else {
                                // 4. 验证角色匹配
                                // 数据库定义: 1超级管理员 2管理人员 3普通员工
                                int dbRoleInt = rs.getInt("user_role");
                                String expectedRole = getRoleString(dbRoleInt);

                                if ("visitor".equals(uRole)) {
                                    // 特殊处理访客：如果数据库里真的有访客账号，可以在这里放行
                                    // 或者只允许特定账号作为访客。这里简单处理：只要账号密码对，且选了访客，提示角色不符(除非DB里也有访客角色)
                                    // 假设访客不需要数据库验证，或者数据库没有访客角色，这里严格校验：
                                    result.put("success", false);
                                    result.put("message", "该账号不是访客身份，请选择正确的身份登录");
                                } else if (!expectedRole.equals(uRole)) {
                                    result.put("success", false);
                                    result.put("message", "身份不匹配！您是 " + getRoleNameCN(expectedRole) + "，请重新选择");
                                } else {
                                    // 5. 登录成功
                                    result.put("success", true);
                                    result.put("message", "登录成功");
                                    result.put("data", new JSONObject()
                                            .fluentPut("username", rs.getString("username"))
                                            .fluentPut("role", uRole)
                                            .fluentPut("emp_id", rs.getInt("emp_id"))
                                    );

                                    // 这里可以使用 Session 记录登录态
                                    req.getSession().setAttribute("user", rs.getString("username"));
                                    req.getSession().setAttribute("role", uRole);
                                }
                            }
                        }
                    } else {
                        result.put("success", false);
                        result.put("message", "用户名不存在");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }

        resp.getWriter().write(result.toJSONString());
    }

    // 辅助：将数据库的 int 角色转为前端的 string 标识
    private String getRoleString(int roleId) {
        switch (roleId) {
            case 1: return "admin";
            case 2: return "manager";
            case 3: return "employee";
            default: return "unknown";
        }
    }

    // 辅助：中文名称
    private String getRoleNameCN(String role) {
        switch (role) {
            case "admin": return "超级管理员";
            case "manager": return "管理人员";
            case "employee": return "普通员工";
            default: return "未知";
        }
    }
}