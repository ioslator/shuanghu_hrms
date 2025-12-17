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

@WebServlet("/api/work_type/manage")
public class WorkTypeManageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        String action = req.getParameter("action");
        JSONObject result = new JSONObject();

        try (Connection conn = JdbcUtil.getConnection()) {
            if ("add".equals(action)) {
                // === 1. 新增工种 ===
                String name = req.getParameter("name");
                String desc = req.getParameter("desc");

                if (name == null || name.trim().isEmpty()) {
                    result.put("success", false);
                    result.put("message", "工种名称不能为空");
                } else {
                    String sql = "INSERT INTO work_type (work_type_name, work_type_desc, work_type_status) VALUES (?, ?, 1)";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setString(1, name);
                        ps.setString(2, desc);
                        int rows = ps.executeUpdate();
                        result.put("success", rows > 0);
                        result.put("message", rows > 0 ? "工种添加成功" : "添加失败");
                    }
                }

            } else if ("toggle".equals(action)) {
                // === 2. 修改状态 (启用/禁用) ===
                String idStr = req.getParameter("id");
                String statusStr = req.getParameter("status"); // 目标状态

                if (idStr != null && statusStr != null) {
                    String sql = "UPDATE work_type SET work_type_status = ? WHERE work_type_id = ?";
                    try (PreparedStatement ps = conn.prepareStatement(sql)) {
                        ps.setInt(1, Integer.parseInt(statusStr));
                        ps.setInt(2, Integer.parseInt(idStr));
                        int rows = ps.executeUpdate();
                        result.put("success", rows > 0);
                        result.put("message", "状态更新成功");
                    }
                } else {
                    result.put("success", false);
                    result.put("message", "参数错误");
                }
            } else {
                result.put("success", false);
                result.put("message", "未知操作");
            }
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }

        resp.getWriter().write(result.toJSONString());
    }
}