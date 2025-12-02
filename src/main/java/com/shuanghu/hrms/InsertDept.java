package com.shuanghu.hrms; // 添加此行，声明包路径
//插入部门信息
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import model.Dept;
import utils.JdbcUtil;
import utils.Tool1;

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

        Tool1 tool = new Tool1();
        tool.setUtf8_Text(request, response); // 设置字符编码

        // 反射将请求参数映射到Dept模型
        Dept dept = new Dept();
        dept = (Dept) tool.requestToModel(request, dept);

        // 生成插入SQL语句（假设表名为dept，使用Tool1工具类生成）
        String sql = tool.modelToInsertString(dept, "dept");
        System.out.println("插入部门SQL: " + sql);

        new JdbcUtil();
        int result = Tool1.excuteNoQuery(sql);

        // 响应结果
        if (result == 1) {
            response.getWriter().write("部门新增成功");
        } else {
            response.getWriter().write("部门新增失败");
        }
    }

}