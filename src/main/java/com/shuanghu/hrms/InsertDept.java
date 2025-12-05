package com.shuanghu.hrms; // 添加此行，声明包路径
//插入部门信息
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import com.shuanghu.hrms.model.Dept;
import com.shuanghu.hrms.utils.JdbcUtil;
import com.shuanghu.hrms.utils.Tool1;

/**
 * Servlet implementation class InsertDept
 */
@WebServlet("/InsertDept")
public class InsertDept extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public InsertDept() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        response.getWriter().append("Served at: ").append(request.getContextPath());
    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        System.out.println("========== InsertDept 被访问到了！时间：" + new java.util.Date() + " ==========");

        response.setContentType("application/json;charset=utf-8");
        
        try {
            Tool1 tool = new Tool1();
            tool.setUtf8_Text(request, response); // 设置字符编码

            // 参数验证
            String deptName = request.getParameter("dept_name");
            if (deptName == null || deptName.trim().isEmpty()) {
                sendErrorResponse(response, 400, "部门名称不能为空");
                return;
            }

            // 反射将请求参数映射到Dept模型
            Dept dept = new Dept();
            dept = (Dept) tool.requestToModel(request, dept);

            // 生成插入SQL语句（假设表名为dept，使用Tool1工具类生成）
            String sql = tool.modelToInsertString(dept, "dept");
            System.out.println("插入部门SQL: " + sql);

            int result = new JdbcUtil().excuteNoQuery(sql);

            // 响应结果
            com.alibaba.fastjson.JSONObject jsonResponse = new com.alibaba.fastjson.JSONObject();
            if (result == 1) {
                jsonResponse.put("success", true);
                jsonResponse.put("message", "部门新增成功");
            } else {
                jsonResponse.put("success", false);
                jsonResponse.put("message", "部门新增失败");
            }
            response.getWriter().write(jsonResponse.toJSONString());
            
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
        com.alibaba.fastjson.JSONObject errorResponse = new com.alibaba.fastjson.JSONObject();
        errorResponse.put("success", false);
        errorResponse.put("message", message);
        errorResponse.put("status", statusCode);
        response.getWriter().write(errorResponse.toJSONString());
    }

}