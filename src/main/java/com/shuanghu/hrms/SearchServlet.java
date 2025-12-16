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

// 假设您使用的是 Servlet 3.0+ 的注解
@WebServlet("/api/search")
public class SearchServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");
        String keyword = req.getParameter("keyword");
        if (keyword == null) keyword = ""; // 处理空关键字

        // SQL：查询所有员工字段 (e.*)，并关联部门和职位，只查在职员工 (emp_status = 1)
        String sql = "SELECT e.*, d.dept_name, t.title_name " +
                "FROM employee e " +
                "LEFT JOIN dept d ON e.dept_id = d.dept_id " +
                "LEFT JOIN title t ON e.title_id = t.title_id " +
                // 确保能查到所有员工，如果关键字为空，则 LIKE '%%'
                "WHERE e.emp_status = 1 AND (e.emp_name LIKE ? OR e.emp_no LIKE ?)";

        JSONArray list = new JSONArray();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // 绑定参数：用于模糊匹配
            ps.setString(1, "%" + keyword + "%");
            ps.setString(2, "%" + keyword + "%");

            try(ResultSet rs = ps.executeQuery()){
                ResultSetMetaData md = rs.getMetaData();
                while (rs.next()) {
                    JSONObject obj = new JSONObject();
                    // 自动将所有查询到的列放入 JSON (包括 emp_email, emp_phone, hire_date 等)
                    for (int i = 1; i <= md.getColumnCount(); i++) {
                        obj.put(md.getColumnLabel(i), rs.getObject(i));
                    }
                    list.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        resp.getWriter().write(list.toString());
    }
}