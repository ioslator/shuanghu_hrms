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
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");

        // 【修改】SQL语句增加 d.dept_desc
        String sql = "SELECT d.dept_id, d.dept_name, d.dept_parent_id, d.dept_status, d.dept_desc, " +
                "(SELECT COUNT(*) FROM employee e WHERE e.dept_id = d.dept_id AND e.emp_status=1) as emp_count " +
                "FROM dept d WHERE d.dept_status = 1";

        JSONArray list = new JSONArray();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("dept_id"));
                obj.put("name", rs.getString("dept_name"));

                int parentId = rs.getInt("dept_parent_id");
                obj.put("parentId", rs.wasNull() ? 0 : parentId);

                // 【修改】获取数据库中的简介，如果是 null 则返回空字符串
                String desc = rs.getString("dept_desc");
                obj.put("description", desc == null ? "暂无部门职能描述" : desc);

                obj.put("employeeCount", rs.getInt("emp_count"));
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write(list.toString());
    }
}