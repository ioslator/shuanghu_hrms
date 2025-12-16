package com.shuanghu.hrms.controller;

import com.alibaba.fastjson.JSONObject;
import com.shuanghu.hrms.utils.JdbcUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;

@WebServlet("/api/emp/manage")
public class EmployeeManageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doHandle(req, resp);
    }

    // 统一处理增删改，通过 action 参数区分
    private void doHandle(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        String action = req.getParameter("action");
        JSONObject result = new JSONObject();

        try (Connection conn = JdbcUtil.getConnection()) {
            if ("add".equals(action)) {
                // 新增员工
                String sql = "INSERT INTO employee (emp_name, emp_no, dept_id, title_id, hire_date, emp_status, create_time) VALUES (?, ?, ?, ?, ?, 1, NOW())";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, req.getParameter("name"));
                    ps.setString(2, "EMP" + System.currentTimeMillis() % 10000); // 简单生成工号
                    ps.setInt(3, Integer.parseInt(req.getParameter("deptId")));
                    ps.setInt(4, Integer.parseInt(req.getParameter("titleId") == null ? "0" : req.getParameter("titleId"))); // 默认为0
                    ps.setString(5, req.getParameter("hireDate"));

                    int rows = ps.executeUpdate();
                    result.put("success", rows > 0);
                    result.put("message", rows > 0 ? "新增成功" : "新增失败");
                }
            } else if ("update".equals(action)) {
                // 更新员工信息
                String sql = "UPDATE employee SET emp_name=?, dept_id=?, title_id=?, hire_date=? WHERE emp_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, req.getParameter("name"));
                    ps.setInt(2, Integer.parseInt(req.getParameter("deptId")));
                    ps.setInt(3, Integer.parseInt(req.getParameter("titleId")));
                    ps.setString(4, req.getParameter("hireDate"));
                    ps.setInt(5, Integer.parseInt(req.getParameter("id")));

                    int rows = ps.executeUpdate();
                    result.put("success", rows > 0);
                    result.put("message", rows > 0 ? "更新成功" : "更新失败");
                }
            } else if ("delete".equals(action)) {
                // 删除员工 (软删除，修改状态为0)
                String sql = "UPDATE employee SET emp_status=0 WHERE emp_id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, Integer.parseInt(req.getParameter("id")));
                    int rows = ps.executeUpdate();
                    result.put("success", rows > 0);
                    result.put("message", rows > 0 ? "删除成功" : "删除失败");
                }
            } else {
                result.put("success", false);
                result.put("message", "未知操作类型");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "操作异常: " + e.getMessage());
        }
        resp.getWriter().write(result.toJSONString());
    }
}