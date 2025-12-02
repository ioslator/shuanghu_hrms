package com.shuanghu.hrms;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import utils.JdbcUtil;

@WebServlet("/getCols")
public class getCols extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("========== getCols 被访问到了！时间：" + new java.util.Date() + " ==========");

        response.setContentType("application/json;charset=utf-8");

        String tableName = request.getParameter("tableName");
        String keyName   = request.getParameter("keyName");
        String keyValue  = request.getParameter("keyValue");

        if (tableName == null || keyName == null || keyValue == null) {
            response.getWriter().write("[]");
            return;
        }

        // 简单防注入（实际项目建议用白名单）
        if (!tableName.matches("[a-zA-Z_]+") || !keyName.matches("[a-zA-Z_]+")) {
            response.getWriter().write("[]");
            return;
        }

        String sql = "SELECT * FROM " + tableName + " WHERE " + keyName + " = ?";
        JSONArray array = new JSONArray();

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, keyValue);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    JSONObject obj = new JSONObject(true);
                    int count = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= count; i++) {
                        String col = rs.getMetaData().getColumnName(i).toLowerCase();
                        Object val = rs.getObject(i);
                        obj.put(col, val == null ? "" : val);
                    }
                    array.add(obj);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        response.getWriter().write(array.toJSONString());
    }
}