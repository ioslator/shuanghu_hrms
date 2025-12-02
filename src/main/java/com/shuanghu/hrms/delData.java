package com.shuanghu.hrms;
//删除数据
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import utils.Tool1;

/**
 * Servlet implementation class delSt
 */
@WebServlet("/delData")
//@ResonseBody
public class delData extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public delData() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // TODO Auto-generated method stub
        System.out.println("删除来了");
        response.setContentType("text/json;charset=utf-8");
        String tableName=request.getParameter("tableName");
        String keyName=request.getParameter("keyName");
        String keyValue=request.getParameter("keyValue");
        System.out.println(tableName+" "+keyName+keyValue);
        //删除命令格式：delete from 表名  where 列名=列值，如 delete from student where Sno='0001'
        String sqlString="delete from "+tableName+" where "+keyName+"='"+keyValue+"'";
        int re = 0;
        try {
            re = Tool1.excuteNoQuery(sqlString);
        } catch (Exception e) {
            // TODO 自动生成的 catch 块
            e.printStackTrace();
        }
        if(re==1)
            response.getWriter().write("ok");
        else
            response.getWriter().write("删除没成功");
    }

}