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

@WebServlet("/api/dashboard")
public class GetDashboardDataServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        JSONObject data = new JSONObject();

        try (Connection conn = JdbcUtil.getConnection()) {
            if (conn == null) {
                data.put("error", "数据库连接失败");
                resp.getWriter().write(data.toJSONString());
                return;
            }

            // 1. 总员工数
            String sql1 = "SELECT COUNT(*) FROM employee WHERE emp_status = 1";
            try (PreparedStatement ps = conn.prepareStatement(sql1);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("totalEmployees", rs.getInt(1));
                } else {
                    data.put("totalEmployees", 0);
                }
            }

            // 2. 部门数
            String sql2 = "SELECT COUNT(*) FROM dept";
            try (PreparedStatement ps = conn.prepareStatement(sql2);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("totalDepartments", rs.getInt(1));
                } else {
                    data.put("totalDepartments", 0);
                }
            }

            // 3. 今日出勤 (修正了这里的报错逻辑)
            // 注意：CURDATE() 是 MySQL 函数，确保数据库是 MySQL
            String sql3 = "SELECT COUNT(*) FROM attendance WHERE attend_date = CURDATE()";
            try (PreparedStatement ps = conn.prepareStatement(sql3);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    data.put("todayAttendance", rs.getInt(1));
                } else {
                    data.put("todayAttendance", 0);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            data.put("error", "查询异常: " + e.getMessage());
        }

        resp.getWriter().write(data.toJSONString());
    }
}