package com.shuanghu.hrms.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shuanghu.hrms.utils.JdbcUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.*;

@WebServlet(urlPatterns = {"/api/leaves", "/api/punishments", "/api/resignations", "/api/emp/io"})
public class HrRecordServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();

        // 1. 导出功能 (模拟 CSV 下载)
        if (uri.endsWith("/io")) {
            resp.setContentType("text/csv;charset=GBK");
            resp.setHeader("Content-Disposition", "attachment; filename=employees.csv");
            resp.getWriter().write("工号,姓名,部门,职位\n"); // 表头
            // 实际应查询数据库写入，这里演示
            resp.getWriter().write("1001,张三,技术部,开发\n");
            return;
        }

        // 2. 数据查询功能
        resp.setContentType("application/json;charset=utf-8");
        String sql = "";

        if (uri.endsWith("leaves")) {
            sql = "SELECT l.*, e.emp_name FROM emp_leave l LEFT JOIN employee e ON l.emp_id = e.emp_id";
        } else if (uri.endsWith("punishments")) {
            sql = "SELECT p.*, e.emp_name FROM emp_punishment p LEFT JOIN employee e ON p.emp_id = e.emp_id";
        } else if (uri.endsWith("resignations")) {
            sql = "SELECT r.*, e.emp_name FROM emp_resignation r LEFT JOIN employee e ON r.emp_id = e.emp_id";
        }

        JSONArray list = new JSONArray();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= md.getColumnCount(); i++) obj.put(md.getColumnLabel(i), rs.getObject(i));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        resp.getWriter().write(list.toString());
    }
}