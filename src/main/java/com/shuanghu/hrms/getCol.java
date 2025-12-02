package com.shuanghu.hrms;

import java.io.IOException;
import java.io.Serial;
import java.sql.*;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Date;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import com.shuanghu.hrms.utils.JdbcUtil;

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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json;charset=UTF-8");
        PrintWriter out = resp.getWriter();

        String tableName = req.getParameter("tableName");
        String keyName = req.getParameter("keyName"); // 比如 dept_id,dept_name

        System.out.println("========== getCol 被访问到了！时间：" + new Date() + " ==========");
        System.out.println("请求参数: tableName=" + tableName + ", keyName=" + keyName);

        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        JSONArray jsonArray = new JSONArray();

        try {
            conn = JdbcUtil.getConnection();
            if (conn == null) {
                out.print("[]");
                return;
            }

            String[] keys = keyName.split(",");
            String sql = "SELECT " + keys[0] + " as value, " + keys[1] + " as label FROM " + tableName;
            ps = conn.prepareStatement(sql);
            rs = ps.executeQuery();

            // ======== 紧急调试代码开始（查到数据就一定打印出来）========
            System.out.println("SQL 执行成功！正在遍历结果...");
            int count = 0;
            while (rs.next()) {
                count++;
                System.out.println("第 " + count + " 条数据：value=" + rs.getObject(1) + ", label=" + rs.getString(2));

                JSONObject obj = new JSONObject();
                obj.put("value", rs.getObject(1));
                obj.put("label", rs.getString(2));
                jsonArray.add(obj);
            }
            System.out.println("一共查到 " + count + " 条数据！");
// ======== 紧急调试代码结束 ========

            while (rs.next()) {
                JSONObject obj = new JSONObject();
                obj.put("value", rs.getString("value"));
                obj.put("label", rs.getString("label"));
                jsonArray.add(obj);
            }

            out.print(jsonArray.toString());
            System.out.println("成功返回数据：" + jsonArray.toString());

        } catch (Exception e) {
            e.printStackTrace();
            out.print("[]");
        } finally {
            JdbcUtil.close(rs, ps, conn);
        }
    }
}