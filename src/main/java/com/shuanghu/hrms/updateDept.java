package com.shuanghu.hrms;
//更新部门信息
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.shuanghu.hrms.model.Dept;
import com.shuanghu.hrms.utils.Tool1;

@WebServlet("/updateDept")
@MultipartConfig
public class updateDept extends HttpServlet {
    /**
     *
     */
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("========== updateDept 被访问到了！时间：" + new java.util.Date() + " ==========");

        Tool1 tool = new Tool1();
        tool.setUtf8_Text(request, response);
        Dept dept = new Dept();
        dept = (Dept) tool.requestToModel(request, dept);
        String sql = tool.modelToUpdateString(dept, "dept", "dept_id"); // 生成更新SQL
        int re =Tool1.excuteNoQuery(sql);
        response.getWriter().write(re > 0 ? "1" : "0");
    }
}