package com.shuanghu.hrms;

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
import java.text.SimpleDateFormat;

@WebServlet("/api/notices")
public class GetNoticeListServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json;charset=utf-8");
        JSONArray list = new JSONArray();

        String sql = "SELECT * FROM notice WHERE status = 1 ORDER BY create_time DESC";

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            SimpleDateFormat sdfDay = new SimpleDateFormat("MM-dd");
            SimpleDateFormat sdfYear = new SimpleDateFormat("yyyy");

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("id", rs.getInt("notice_id"));
                obj.put("title", rs.getString("notice_title"));
                obj.put("dept", rs.getString("publish_dept"));

                // 拆分日期格式，方便前端展示
                java.sql.Timestamp time = rs.getTimestamp("create_time");
                obj.put("dateDay", sdfDay.format(time));
                obj.put("dateYear", sdfYear.format(time));

                // 判断是否是最近3天发布的，打上 "NEW" 标记
                long diff = System.currentTimeMillis() - time.getTime();
                obj.put("isNew", diff < 3 * 24 * 60 * 60 * 1000L); // 3天内

                list.add(obj);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        resp.getWriter().write(list.toString());
    }
}