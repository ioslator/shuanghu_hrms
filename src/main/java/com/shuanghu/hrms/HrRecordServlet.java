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

// 确保 urlPatterns 包含 leaves 和 resignations
@WebServlet(urlPatterns = {"/api/leaves", "/api/resignations", "/api/punishments"})
public class HrRecordServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        String uri = req.getRequestURI();
        String sql = "";

        // 修复：确保 leaves 接口查询 emp_leave 表
        if (uri.endsWith("/leaves")) {
            sql = "SELECT l.*, e.emp_name FROM emp_leave l LEFT JOIN employee e ON l.emp_id = e.emp_id ORDER BY l.leave_date DESC";
        }
        // 修复：确保 resignations 接口查询 emp_resignation 表
        else if (uri.endsWith("/resignations")) {
            sql = "SELECT r.*, e.emp_name FROM emp_resignation r LEFT JOIN employee e ON r.emp_id = e.emp_id ORDER BY r.resign_date DESC";
        }
        // 奖惩记录保持不变
        else if (uri.endsWith("/punishments")) {
            sql = "SELECT p.*, e.emp_name FROM emp_punishment p LEFT JOIN employee e ON p.emp_id = e.emp_id";
        }

        JSONArray list = new JSONArray();
        // 如果 sql 为空（未匹配到接口），直接返回空数组
        if (sql.isEmpty()) {
            resp.getWriter().write(list.toString());
            return;
        }

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            int colCount = md.getColumnCount();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= colCount; i++) {
                    // 获取列别名或列名，并放入 JSON 对象
                    obj.put(md.getColumnLabel(i), rs.getObject(i));
                }
                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 可以选择返回错误信息，或者在控制台打印日志
        }
        resp.getWriter().write(list.toString());
    }
}