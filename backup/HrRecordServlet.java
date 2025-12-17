package com.shuanghu.hrms;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.shuanghu.hrms.utils.JdbcUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;
import java.io.IOException;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;

@WebServlet(urlPatterns = {"/api/leaves", "/api/punishments", "/api/resignations", "/api/emp/io"})
@MultipartConfig
public class HrRecordServlet extends HttpServlet {
    
    // 辅助方法：从 multipart 请求中提取参数
    private Map<String, String> parseMultipartParameters(HttpServletRequest request) throws IOException, ServletException {
        Map<String, String> parameters = new HashMap<>();
        
        // 遍历所有 part
        for (Part part : request.getParts()) {
            String name = part.getName();
            // 只处理非文件字段
            if (part.getContentType() == null) {
                // 读取参数值
                java.util.Scanner scanner = new java.util.Scanner(part.getInputStream()).useDelimiter("\\A");
                String value = scanner.hasNext() ? scanner.next() : "";
                parameters.put(name, value.trim());
                System.out.println("Parsed multipart parameter: " + name + " = " + value);
            }
        }
        
        return parameters;
    }
    
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
    
        // 1. 导出功能 (模拟 CSV 下载)
        if (uri.endsWith("/io")) {
            resp.setContentType("text/csv;charset=GBK");
            resp.setHeader("Content-Disposition", "attachment; filename=employees.csv");
            resp.getWriter().write("工号,姓名,部门,职位\n"); // 表头
            // 实际应查询数据库写入，这里演示
            resp.getWriter().write("1001,张三,技术部,开发\n");
            return;
        }
    
        // 2. 数据查询功能
        resp.setContentType("application/json;charset=utf-8");
        String sql = "";
    
        if (uri.endsWith("leaves")) {
            // 修复：使用正确的字段名application_id
            sql = "SELECT l.*, e.emp_name FROM leave_application l LEFT JOIN employee e ON l.emp_id = e.emp_id ORDER BY l.apply_time DESC";
        } else if (uri.endsWith("punishments")) {
            sql = "SELECT p.*, e.emp_name FROM emp_punishment p LEFT JOIN employee e ON p.emp_id = e.emp_id";
        } else if (uri.endsWith("resignations")) {
            sql = "SELECT r.*, e.emp_name FROM emp_resignation r LEFT JOIN employee e ON r.emp_id = e.emp_id";
        }
    
        JSONArray list = new JSONArray();
        try (Connection conn = JdbcUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            ResultSetMetaData md = rs.getMetaData();
            while (rs.next()) {
                JSONObject obj = new JSONObject();
                for (int i = 1; i <= md.getColumnCount(); i++) obj.put(md.getColumnLabel(i), rs.getObject(i));
                list.add(obj);
            }
        } catch (Exception e) { e.printStackTrace(); }
        resp.getWriter().write(list.toString());
    }
    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        resp.setContentType("application/json;charset=utf-8");
        
        if (uri.endsWith("leaves")) {
            // 处理请假申请提交
            try {
                // 正确处理 multipart/form-data 格式的数据
                String empId = null;
                String leaveType = null;
                String startDate = null;
                String endDate = null;
                String leaveReason = null;
                String contactInfo = null;
                
                // 首先尝试直接获取参数（适用于普通表单提交）
                empId = req.getParameter("empId");
                leaveType = req.getParameter("leaveType");
                startDate = req.getParameter("startDate");
                endDate = req.getParameter("endDate");
                leaveReason = req.getParameter("leaveReason");
                contactInfo = req.getParameter("contactInfo");
                
                // 如果getParameter获取不到数据，说明是multipart/form-data请求，需要特殊处理
                if (empId == null) {
                    System.out.println("Detected multipart/form-data request, parsing parts...");
                    Map<String, String> params = parseMultipartParameters(req);
                    
                    empId = params.get("empId");
                    leaveType = params.get("leaveType");
                    startDate = params.get("startDate");
                    endDate = params.get("endDate");
                    leaveReason = params.get("leaveReason");
                    contactInfo = params.get("contactInfo");
                }
                
                // 输出调试信息到控制台
                System.out.println("Received parameters:");
                System.out.println("empId: " + empId);
                System.out.println("leaveType: " + leaveType);
                System.out.println("startDate: " + startDate);
                System.out.println("endDate: " + endDate);
                System.out.println("leaveReason: " + leaveReason);
                System.out.println("contactInfo: " + contactInfo);
                
                // 完整的参数校验
                if (empId == null || empId.trim().isEmpty() || 
                    leaveType == null || leaveType.trim().isEmpty() || 
                    startDate == null || startDate.trim().isEmpty() || 
                    endDate == null || endDate.trim().isEmpty() || 
                    leaveReason == null || leaveReason.trim().isEmpty()) {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "请填写完整的请假信息！");
                    resp.getWriter().write(response.toJSONString());
                    return;
                }
                
                // 注意：contactInfo 可以为 null 或空，因为它是可选字段
                if (contactInfo == null) {
                    contactInfo = ""; // 设置为空字符串而不是 null
                }
                
                // 使用正确的字段名
                String sql = "INSERT INTO leave_application (emp_id, leave_type, start_date, end_date, leave_reason, contact_info, status) VALUES (?, ?, ?, ?, ?, ?, 'pending')";
                
                try (Connection conn = JdbcUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                    
                    // 设置SQL参数
                    ps.setInt(1, Integer.parseInt(empId));
                    ps.setInt(2, Integer.parseInt(leaveType));
                    ps.setDate(3, Date.valueOf(startDate));
                    ps.setDate(4, Date.valueOf(endDate));
                    ps.setString(5, leaveReason);
                    ps.setString(6, contactInfo);
                    
                    int result = ps.executeUpdate();
                    
                    JSONObject response = new JSONObject();
                    if (result > 0) {
                        // 获取插入的记录ID
                        try (ResultSet generatedKeys = ps.getGeneratedKeys()) {
                            if (generatedKeys.next()) {
                                response.put("applicationId", generatedKeys.getInt(1));
                            }
                        }
                        response.put("success", true);
                        response.put("message", "请假申请提交成功！");
                    } else {
                        response.put("success", false);
                        response.put("message", "请假申请提交失败！");
                    }
                    resp.getWriter().write(response.toJSONString());
                }
            } catch (NumberFormatException e) {
                System.err.println("Invalid number format in leave approval: " + e.getMessage());
                e.printStackTrace();
                JSONObject response = new JSONObject();
                response.put("success", false);
                response.put("message", "参数格式错误，请检查输入数据");
                resp.getWriter().write(response.toJSONString());
            } catch (SQLException e) {
                System.err.println("Database error in leave approval: " + e.getMessage());
                e.printStackTrace();
                JSONObject response = new JSONObject();
                response.put("success", false);
                response.put("message", "数据库操作失败：" + e.getMessage());
                resp.getWriter().write(response.toJSONString());
            } catch (Exception e) {
                System.err.println("Unexpected error in leave approval: " + e.getMessage());
                e.printStackTrace();
                JSONObject response = new JSONObject();
                response.put("success", false);
                response.put("message", "系统错误：" + e.getMessage());
                resp.getWriter().write(response.toJSONString());
            }
        }
    }
    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String uri = req.getRequestURI();
        resp.setContentType("application/json;charset=utf-8");
        
        if (uri.endsWith("leaves")) {
            // 处理请假审批
            try {
                String appId = null;
                String status = null;
                String approveBy = null;
                
                // 首先尝试直接获取参数（适用于普通表单提交）
                appId = req.getParameter("appId");
                status = req.getParameter("status");
                approveBy = req.getParameter("approveBy");
                
                // 如果getParameter获取不到数据，说明是multipart/form-data请求，需要特殊处理
                if (appId == null) {
                    System.out.println("Detected multipart/form-data request in PUT, parsing parts...");
                    Map<String, String> params = parseMultipartParameters(req);
                    
                    appId = params.get("appId");
                    status = params.get("status");
                    approveBy = params.get("approveBy");
                }
                
                // 参数验证
                if (appId == null || appId.trim().isEmpty()) {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "申请ID不能为空！");
                    resp.getWriter().write(response.toJSONString());
                    return;
                }
                
                if (status == null || status.trim().isEmpty()) {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "审批状态不能为空！");
                    resp.getWriter().write(response.toJSONString());
                    return;
                }
                
                if (!"approved".equals(status) && !"rejected".equals(status)) {
                    JSONObject response = new JSONObject();
                    response.put("success", false);
                    response.put("message", "审批状态必须为approved或rejected！");
                    resp.getWriter().write(response.toJSONString());
                    return;
                }
                
                System.out.println("Processing leave approval - AppId: " + appId + ", Status: " + status + ", ApproveBy: " + approveBy);
                
                // 修复：使用正确的字段名application_id
                String sql = "UPDATE leave_application SET status = ?, approve_by = ?, approve_time = NOW() WHERE application_id = ?";
                
                try (Connection conn = JdbcUtil.getConnection();
                     PreparedStatement ps = conn.prepareStatement(sql)) {
                    
                    ps.setString(1, status);
                    ps.setObject(2, approveBy != null && !approveBy.isEmpty() ? Integer.parseInt(approveBy) : null);
                    ps.setInt(3, Integer.parseInt(appId));
                    
                    System.out.println("Executing SQL: " + sql);
                    System.out.println("Parameters - Status: " + status + ", ApproveBy: " + approveBy + ", AppId: " + appId);
                    
                    int result = ps.executeUpdate();
                    
                    System.out.println("Update result - Rows affected: " + result);
                    
                    JSONObject response = new JSONObject();
                    if (result > 0) {
                        response.put("success", true);
                        response.put("message", "审批操作成功！");
                    } else {
                        response.put("success", false);
                        response.put("message", "审批操作失败！");
                    }
                    resp.getWriter().write(response.toJSONString());
                }
            } catch (Exception e) {
                e.printStackTrace();
                JSONObject response = new JSONObject();
                response.put("success", false);
                response.put("message", "系统错误：" + e.getMessage());
                resp.getWriter().write(response.toJSONString());
            }
        }
    }
}