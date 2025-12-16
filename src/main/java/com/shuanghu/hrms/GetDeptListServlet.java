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

@WebServlet("/api/departments")
public class GetDeptListServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        // 查询部门及其人数
        String sql = "SELECT d.dept_id, d.dept_name, " +
                "(SELECT COUNT(*) FROM employee e WHERE e.dept_id = d.dept_id AND e.emp_status=1) as emp_count " +
                "FROM dept d";

        JSONArray list = new JSONArray();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("dept_id"));
                obj.put("name", rs.getString("dept_name"));
                obj.put("description", "部门常规职能"); // 数据库若无描述字段，给默认值
                obj.put("employeeCount", rs.getInt("emp_count"));
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write(list.toString());
    }
}