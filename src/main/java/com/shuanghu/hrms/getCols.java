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
import com.shuanghu.hrms.utils.JdbcUtil;

@WebServlet("/getCols")
public class getCols extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        System.out.println("========== getCols 被访问到了！时间：" + new java.util.Date() + " ==========");

        response.setContentType("application/json;charset=utf-8");

        try {
            String tableName = request.getParameter("tableName");
            String keyName   = request.getParameter("keyName");
            String keyValue  = request.getParameter("keyValue");

            // 参数验证
            if (tableName == null || keyName == null || keyValue == null) {
                sendErrorResponse(response, 400, "缺少必要参数: tableName, keyName, keyValue");
                return;
            }

            // 简单防注入（实际项目建议用白名单）
            if (!tableName.matches("[a-zA-Z_]+") || !keyName.matches("[a-zA-Z_]+")) {
                sendErrorResponse(response, 400, "参数格式错误");
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
                sendErrorResponse(response, 500, "数据库查询异常: " + e.getMessage());
                return;
            }

            // 返回标准化的JSON响应
            JSONObject result = new JSONObject();
            result.put("success", true);
            result.put("data", array);
            result.put("total", array.size());
            response.getWriter().write(result.toJSONString());
            
        } catch (Exception e) {
            e.printStackTrace();
            sendErrorResponse(response, 500, "服务器内部错误: " + e.getMessage());
        }
    }
    
    /**
     * 发送错误响应
     */
    private void sendErrorResponse(HttpServletResponse response, int statusCode, String message) throws IOException {
        response.setStatus(statusCode);
        JSONObject errorResponse = new JSONObject();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("status", statusCode);
        response.getWriter().write(errorResponse.toJSONString());
    }
}