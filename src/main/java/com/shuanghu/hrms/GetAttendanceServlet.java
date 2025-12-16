package com.shuanghu.hrms;

import com.alibaba.fastjson.JSONArray;
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

@WebServlet("/api/attendance")
public class GetAttendanceServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        // 假设表名为 attendance
        String sql = "SELECT a.*, e.emp_name FROM attendance a " +
                "LEFT JOIN employee e ON a.emp_id = e.emp_id ORDER BY a.attend_date DESC";

        JSONArray list = new JSONArray();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("attend_id"));
                obj.put("employeeId", rs.getInt("emp_id"));
                obj.put("employeeName", rs.getString("emp_name"));
                obj.put("date", rs.getDate("attend_date"));
                obj.put("checkIn", rs.getTime("start_time"));
                obj.put("checkOut", rs.getTime("end_time"));
                obj.put("status", rs.getString("status"));
                list.add(obj);
            }
        } catch (Exception e) {
            // 如果表不存在，返回空数组，不报错
            e.printStackTrace();
        }
        resp.getWriter().write(list.toString());
    }
}