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

@WebServlet("/api/employees")
public class GetEmpListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        // 连表查询：员工表 + 部门表 + 职称表
        String sql = "SELECT e.emp_id, e.emp_name, e.emp_no, e.emp_email, e.emp_phone, t.title_name as position, d.dept_name, e.hire_date " +
                "FROM employee e " +
                "LEFT JOIN dept d ON e.dept_id = d.dept_id " +
                "LEFT JOIN title t ON e.title_id = t.title_id " +
                "WHERE e.emp_status = 1"; // 1 表示在职

        JSONArray list = new JSONArray();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("emp_id"));
                obj.put("name", rs.getString("emp_name"));
                obj.put("no", rs.getString("emp_no")); // 确保工号也有
                obj.put("email", rs.getString("emp_email")); // 新增：邮箱
                obj.put("phone", rs.getString("emp_phone")); // 新增：手机
                // ... 其他字段保持不变
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write(list.toString());
    }
}