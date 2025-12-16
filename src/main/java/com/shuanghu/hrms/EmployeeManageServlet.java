package com.shuanghu.hrms;

import com.alibaba.fastjson.JSONObject;
import com.shuanghu.hrms.utils.JdbcUtil;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Types; // 导入 Types，用于设置 NULL

@WebServlet(urlPatterns = {"/api/employee/add", "/api/employee/update", "/api/employee/delete"})
public class EmployeeManageServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // 设置编码，防止中文乱码
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        String uri = req.getRequestURI();
        JSONObject result = new JSONObject();

        // 路由判断
        if (uri.endsWith("/add")) {
            addEmployee(req, result);
        } else if (uri.endsWith("/update")) {
            // 这里可以放更新逻辑
            result.put("success", false);
            result.put("message", "更新接口待实现");
        } else if (uri.endsWith("/delete")) {
            // 这里可以放删除逻辑
            result.put("success", false);
            result.put("message", "删除接口待实现");
        }

        resp.getWriter().write(result.toJSONString());
    }

    // 核心：添加员工逻辑
    private void addEmployee(HttpServletRequest req, JSONObject result) {
        // 1. 获取参数 (前端 form 表单的 name 属性)
        String name = req.getParameter("emp_name");
        String no = req.getParameter("emp_no");
        String gender = req.getParameter("gender");
        String deptIdStr = req.getParameter("dept_id");
        String titleIdStr = req.getParameter("title_id");
        String hireDate = req.getParameter("hire_date");

        // 选填项
        String birthDate = req.getParameter("birth_date");
        String idCard = req.getParameter("id_card");
        String education = req.getParameter("education");
        String phone = req.getParameter("emp_phone");
        String email = req.getParameter("emp_email");
        String address = req.getParameter("emp_address");

        // 2. 必填项校验 (后端二次校验，防止非法请求)
        if (name == null || name.isEmpty() || no == null || no.isEmpty() ||
                deptIdStr == null || titleIdStr == null || hireDate == null) {
            result.put("success", false);
            result.put("message", "必填字段不能为空！");
            return;
        }

        // 3. 构造 SQL
        // 注意：数据库字段名请根据您实际数据库调整，这里参考了 Employee.java
        String sql = "INSERT INTO employee " +
                "(emp_name, emp_no, gender, dept_id, title_id, hire_date, " + // 必填列
                "birth_date, id_card, education, emp_phone, emp_email, emp_address, " + // 选填列
                "emp_status, create_time) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, NOW())"; // status 默认 1 (在职), create_time 默认当前时间

        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            // --- 设置必填参数 ---
            ps.setString(1, name);
            ps.setString(2, no);
            ps.setString(3, gender);
            ps.setInt(4, Integer.parseInt(deptIdStr));
            ps.setInt(5, Integer.parseInt(titleIdStr));
            ps.setString(6, hireDate); // 格式通常为 yyyy-MM-dd

            // --- 设置选填参数 (核心逻辑：如果是空字符串，则设为 NULL) ---

            // 7. birth_date (日期类型比较特殊，如果为空设为 NULL)
            if (birthDate == null || birthDate.trim().isEmpty()) {
                ps.setNull(7, Types.DATE);
            } else {
                ps.setString(7, birthDate);
            }

            // 8. id_card
            setOptionalString(ps, 8, idCard);

            // 9. education
            setOptionalString(ps, 9, education);

            // 10. emp_phone
            setOptionalString(ps, 10, phone);

            // 11. emp_email
            setOptionalString(ps, 11, email);

            // 12. emp_address
            setOptionalString(ps, 12, address);

            // 4. 执行插入
            int rows = ps.executeUpdate();
            if (rows > 0) {
                result.put("success", true);
                result.put("message", "员工添加成功");
            } else {
                result.put("success", false);
                result.put("message", "数据库插入失败");
            }

        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "部门ID或职位ID无效");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统错误: " + e.getMessage());
        }
    }

    // 辅助工具方法：处理可选的字符串参数
    private void setOptionalString(PreparedStatement ps, int index, String value) throws java.sql.SQLException {
        if (value == null || value.trim().isEmpty()) {
            ps.setNull(index, Types.VARCHAR); // 如果为空，插入 DB NULL
        } else {
            ps.setString(index, value.trim());
        }
    }
}