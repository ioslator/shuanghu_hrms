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

@WebServlet(urlPatterns = {"/api/job_types", "/api/titles", "/api/management"})
public class BasicInfoServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        String uri = req.getRequestURI();
        String sql = "";

        if (uri.endsWith("job_types")) {
            sql = "SELECT * FROM work_type";
        } else if (uri.endsWith("titles")) {
            sql = "SELECT * FROM title";
        } else if (uri.endsWith("management")) {
            // 连表查询：获取管理者姓名和部门名
            sql = "SELECT m.*, e.emp_name, d.dept_name FROM management m " +
                    "LEFT JOIN employee e ON m.emp_id = e.emp_id " +
                    "LEFT JOIN dept d ON m.manage_dept_id = d.dept_id";
        }

        JSONArray list = new JSONArray();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= colCount; i++) {
                    obj.put(md.getColumnLabel(i), rs.getObject(i));
                }
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        resp.getWriter().write(list.toString());
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");
        String uri = req.getRequestURI();
        JSONObject result = new JSONObject();

        if (uri.endsWith("titles")) {
            // 修改职称薪资范围
            String id = req.getParameter("id");
            String range = req.getParameter("range");

            if (id != null && range != null) {
                String sql = "UPDATE title SET title_salary_range = ? WHERE title_id = ?";
                try (Connection conn = JdbcUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, range);
                    ps.setInt(2, Integer.parseInt(id));
                    int rows = ps.executeUpdate();
                    if (rows > 0) {
                        result.put("success", true);
                        result.put("message", "修改成功");
                    } else {
                        result.put("success", false);
                        result.put("message", "修改失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    result.put("success", false);
                    result.put("message", "数据库错误: " + e.getMessage());
                }
            } else {
                result.put("success", false);
                result.put("message", "参数不完整");
            }
        } else {
            result.put("success", false);
            result.put("message", "不支持的接口");
        }
        resp.getWriter().write(result.toJSONString());
    }
}