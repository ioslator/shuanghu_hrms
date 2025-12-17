package com.shuanghu.hrms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shuanghu.hrms.utils.JdbcUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * 处理前端 fetch('api/job_types') 的 GET 请求，返回工种列表数据
 */
@WebServlet("/api/job_types")
public class GetJobTypesServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=utf-8");

        JSONArray list = new JSONArray();

        try (Connection conn = JdbcUtil.getConnection()) {
            // 查询所有工种
            String sql = "SELECT * FROM work_type";
            try (PreparedStatement ps = conn.prepareStatement(sql);
                 ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    JSONObject item = new JSONObject();
                    item.put("work_type_id", rs.getInt("work_type_id"));
                    item.put("work_type_name", rs.getString("work_type_name"));
                    item.put("work_type_desc", rs.getString("work_type_desc"));
                    item.put("work_type_status", rs.getInt("work_type_status"));
                    list.add(item);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 出错时返回空数组或错误信息，这里简单返回空数组
        }

        resp.getWriter().write(list.toJSONString());
    }
}