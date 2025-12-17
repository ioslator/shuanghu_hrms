package com.shuanghu.hrms.controller;

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

@WebServlet("/api/notice/manage")
public class NoticeManageServlet extends HttpServlet {

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        String action = req.getParameter("action");
        JSONObject result = new JSONObject();

        try (Connection conn = JdbcUtil.getConnection()) {
            if ("list".equals(action)) {
                // 1. 获取公告列表
                String sql = "SELECT * FROM notice ORDER BY create_time DESC";
                try (PreparedStatement ps = conn.prepareStatement(sql);
                     ResultSet rs = ps.executeQuery()) {
                    JSONArray list = new JSONArray();
                    while (rs.next()) {
                        JSONObject item = new JSONObject();
                        item.put("id", rs.getInt("id"));
                        item.put("title", rs.getString("title"));
                        item.put("content", rs.getString("content")); // 列表页可能只需要标题，但在编辑时需要内容
                        item.put("publishDept", rs.getString("publish_dept"));
                        item.put("createTime", rs.getTimestamp("create_time"));
                        list.add(item);
                    }
                    result.put("success", true);
                    result.put("data", list);
                }
            } else if ("detail".equals(action)) {
                // 2. 获取单条详情
                String sql = "SELECT * FROM notice WHERE id = ?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, Integer.parseInt(req.getParameter("id")));
                    try (ResultSet rs = ps.executeQuery()) {
                        if (rs.next()) {
                            JSONObject item = new JSONObject();
                            item.put("id", rs.getInt("id"));
                            item.put("title", rs.getString("title"));
                            item.put("content", rs.getString("content"));
                            item.put("publishDept", rs.getString("publish_dept"));
                            item.put("createTime", rs.getTimestamp("create_time"));
                            result.put("success", true);
                            result.put("data", item);
                        } else {
                            result.put("success", false);
                            result.put("message", "公告不存在");
                        }
                    }
                }
            } else if ("add".equals(action)) {
                // 3. 新增公告
                String sql = "INSERT INTO notice (title, content, publish_dept, create_time) VALUES (?, ?, ?, NOW())";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, req.getParameter("title"));
                    ps.setString(2, req.getParameter("content"));
                    ps.setString(3, req.getParameter("publishDept"));
                    int rows = ps.executeUpdate();
                    result.put("success", rows > 0);
                    result.put("message", rows > 0 ? "发布成功" : "发布失败");
                }
            } else if ("update".equals(action)) {
                // 4. 修改公告
                String sql = "UPDATE notice SET title=?, content=?, publish_dept=? WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setString(1, req.getParameter("title"));
                    ps.setString(2, req.getParameter("content"));
                    ps.setString(3, req.getParameter("publishDept"));
                    ps.setInt(4, Integer.parseInt(req.getParameter("id")));
                    int rows = ps.executeUpdate();
                    result.put("success", rows > 0);
                    result.put("message", rows > 0 ? "修改成功" : "修改失败");
                }
            } else if ("delete".equals(action)) {
                // 5. 删除公告
                String sql = "DELETE FROM notice WHERE id=?";
                try (PreparedStatement ps = conn.prepareStatement(sql)) {
                    ps.setInt(1, Integer.parseInt(req.getParameter("id")));
                    int rows = ps.executeUpdate();
                    result.put("success", rows > 0);
                    result.put("message", rows > 0 ? "删除成功" : "删除失败");
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