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
import java.sql.ResultSet;
import java.sql.Types;
import java.util.regex.Pattern;

@WebServlet(urlPatterns = {"/api/employee/add", "/api/employee/update", "/api/employee/delete"})
public class EmployeeManageServlet extends HttpServlet {

    // 预定义正则表达式
    private static final String PHONE_REGEX = "^1[3-9]\\d{9}$"; // 中国大陆手机号
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"; // 简单邮箱
    private static final String ID_CARD_REGEX = "(^\\d{15}$)|(^\\d{18}$)|(^\\d{17}(\\d|X|x)$)"; // 身份证

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json;charset=utf-8");

        String uri = req.getRequestURI();
        JSONObject result = new JSONObject();

        if (uri.endsWith("/add")) {
            addEmployee(req, result);
        } else {
            result.put("success", false);
            result.put("message", "接口功能未实现");
        }

        resp.getWriter().write(result.toJSONString());
    }

    private void addEmployee(HttpServletRequest req, JSONObject result) {
        // 1. 获取所有参数
        String name = req.getParameter("emp_name");
        String no = req.getParameter("emp_no");
        String gender = req.getParameter("gender");
        String deptIdStr = req.getParameter("dept_id");
        String titleIdStr = req.getParameter("title_id");
        String hireDate = req.getParameter("hire_date");
        String phone = req.getParameter("emp_phone");
        String email = req.getParameter("emp_email");
        String idCard = req.getParameter("id_card");
        String birthDate = req.getParameter("birth_date");
        String education = req.getParameter("education");
        String address = req.getParameter("emp_address");

        // 2.【后端核心校验】开始

        // (1) 必填项非空校验
        if (isEmpty(name) || isEmpty(no) || isEmpty(gender) || isEmpty(hireDate) ||
                isEmpty(deptIdStr) || isEmpty(titleIdStr)) {
            result.put("success", false);
            result.put("message", "提交失败：所有带 * 的必填项都不能为空！");
            return;
        }

        // (2) 格式校验 (即便前端没填，如果有值就必须符合格式)
        if (!isEmpty(phone) && !Pattern.matches(PHONE_REGEX, phone)) {
            result.put("success", false);
            result.put("message", "提交失败：手机号码格式不正确！");
            return;
        }
        if (!isEmpty(email) && !Pattern.matches(EMAIL_REGEX, email)) {
            result.put("success", false);
            result.put("message", "提交失败：电子邮箱格式不正确！");
            return;
        }
        if (!isEmpty(idCard) && !Pattern.matches(ID_CARD_REGEX, idCard)) {
            result.put("success", false);
            result.put("message", "提交失败：身份证号格式不正确！");
            return;
        }

        // 3. 数据库操作
        try (Connection conn = JdbcUtil.getConnection()) {

            // (3) 业务逻辑校验：检查工号是否已存在
            String checkSql = "SELECT count(*) FROM employee WHERE emp_no = ? AND emp_status = 1";
            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setString(1, no);
                ResultSet rs = checkPs.executeQuery();
                if (rs.next() && rs.getInt(1) > 0) {
                    result.put("success", false);
                    result.put("message", "提交失败：工号 " + no + " 已存在，请更换！");
                    return;
                }
            }

            // (4) 执行插入
            String sql = "INSERT INTO employee (emp_name, emp_no, gender, dept_id, title_id, hire_date, " +
                    "emp_phone, emp_email, id_card, birth_date, education, emp_address, emp_status, create_time) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 1, NOW())";

            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setString(1, name);
                ps.setString(2, no);
                ps.setString(3, gender);
                ps.setInt(4, Integer.parseInt(deptIdStr));
                ps.setInt(5, Integer.parseInt(titleIdStr));
                ps.setString(6, hireDate);

                // 处理选填项 (空字符串转NULL)
                setOptionalString(ps, 7, phone);
                setOptionalString(ps, 8, email);
                setOptionalString(ps, 9, idCard);
                // 日期特殊处理
                if (isEmpty(birthDate)) ps.setNull(10, Types.DATE);
                else ps.setString(10, birthDate);

                setOptionalString(ps, 11, education);
                setOptionalString(ps, 12, address);

                int rows = ps.executeUpdate();
                result.put("success", rows > 0);
                result.put("message", rows > 0 ? "员工录入成功" : "数据库插入失败");
            }

        } catch (NumberFormatException e) {
            result.put("success", false);
            result.put("message", "数据异常：部门或职位ID必须为数字");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("success", false);
            result.put("message", "系统内部错误: " + e.getMessage());
        }
    }

    // 工具方法：判断字符串是否为空
    private boolean isEmpty(String str) {
        return str == null || str.trim().isEmpty();
    }

    // 工具方法：处理可选字段
    private void setOptionalString(PreparedStatement ps, int index, String value) throws java.sql.SQLException {
        if (isEmpty(value)) {
            ps.setNull(index, Types.VARCHAR);
        } else {
            ps.setString(index, value.trim());
        }
    }
}