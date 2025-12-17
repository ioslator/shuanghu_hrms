package com.shuanghu.hrms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shuanghu.hrms.utils.JdbcUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

// 删除了 "/api/notices"，解决冲突
@WebServlet(urlPatterns = {"/api/users", "/api/logs", "/api/change_pwd", "/api/notice/publish"})
public class SystemManageServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        handleRequest(req, resp);
    }

    private void handleRequest(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        String uri = req.getRequestURI();
        JSONObject result = new JSONObject();

        try (Connection conn = JdbcUtil.getConnection()) {
            // 获取用户列表
            if (uri.endsWith("users")) {
                String sql = "SELECT u.*, e.emp_name FROM sys_user u LEFT JOIN employee e ON u.emp_id = e.emp_id";
                resp.getWriter().write(fetchData(conn, sql).toString());
            }
            // 获取日志
            else if (uri.endsWith("logs")) {
                String sql = "SELECT * FROM sys_log ORDER BY create_time DESC LIMIT 50";
                resp.getWriter().write(fetchData(conn, sql).toString());
            }
            // ❌ 删除了获取公告的逻辑，防止干扰 GetNoticeListServlet

            // 修改密码 (Mock)
            else if (uri.endsWith("change_pwd")) {
                result.put("success", true);
                result.put("message", "密码修改成功");
                resp.getWriter().write(result.toJSONString());
            }
            // 发布公告
            else if (uri.endsWith("notice/publish")) {
                String title = req.getParameter("title");
                if(title != null) {
                    String sql = "INSERT INTO notice(notice_title, publish_dept, create_time, status) VALUES(?, '管理部', NOW(), 1)";
                    try(PreparedStatement ps = conn.prepareStatement(sql)){
                        ps.setString(1, title);
                        ps.executeUpdate();
                    }
                }
                result.put("success", true);
                result.put("message", "公告发布成功");
                resp.getWriter().write(result.toJSONString());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private JSONArray fetchData(Connection conn, String sql) throws SQLException {
        JSONArray arr = new JSONArray();
        try (PreparedStatement ps = conn.prepareStatement(sql); ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= md.getColumnCount(); i++) obj.put(md.getColumnLabel(i), rs.getObject(i));
                arr.add(obj);
            }
        }
        return arr;
    }
}