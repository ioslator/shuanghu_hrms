package com.shuanghu.hrms;

import java.io.IOException;
import java.io.Serial;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import utils.JdbcUtil;

@WebServlet("/getCol")
public class getCol extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);   // 建议加上，方便前端 GET 也测试
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        System.out.println("========== getCol 被访问到了！时间：" + new java.util.Date() + " ==========");
        System.out.println("请求参数: tableName=" + request.getParameter("tableName")
                + ", keyName=" + request.getParameter("keyName"));


        response.setContentType("application/json;charset=utf-8");

        String tableName = request.getParameter("tableName");
        String keyName   = request.getParameter("keyName"); // 例如: dept_id,dept_name

        if (tableName == null || keyName == null) {
            response.getWriter().write("[]");
            return;
        }

        // 防止 SQL 注入（简单过滤）
        if (!keyName.matches("[a-zA-Z_, ]+")) {
            response.getWriter().write("[]");
            return;
        }

        String sql = "SELECT " + keyName + " FROM " + tableName;
        JSONArray jsonArray = new JSONArray();

        try (Connection conn = JdbcUtil.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {   // 关键：这里不会返回 null！

            while (rs.next()) {
                JSONObject obj = new JSONObject(true);  // true = 保持列顺序
                int count = rs.getMetaData().getColumnCount();
                for (int i = 1; i <= count; i++) {
                    String colName = rs.getMetaData().getColumnName(i);
                    Object value = rs.getObject(i);
                    obj.put(colName, value == null ? "" : value);
                }
                jsonArray.add(obj);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            // 出任何数据库错都返回空数组，防止前端卡死
            response.getWriter().write("[]");
            return;
        }

        response.getWriter().write(jsonArray.toJSONString());
    }
}