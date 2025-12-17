package com.shuanghu.hrms.model;

import java.util.Date;

public class LeaveApplication {
    private Integer application_id;
    private Integer emp_id;
    private String emp_name; // 用于显示，不存储到数据库
    private Integer leave_type; // 1:事假 2:病假 3:年假 4:调休假 5:婚假 6:产假
    private Date start_date;
    private Date end_date;
    private String leave_reason;
    private String contact_info;
    private String status; // pending:待审批 approved:已批准 rejected:已拒绝
    private Date apply_time;
    private Date approve_time;
    private Integer approve_by;

    public Integer getApplication_id() { return application_id; }
    public void setApplication_id(Integer application_id) { this.application_id = application_id; }

    public Integer getEmp_id() { return emp_id; }
    public void setEmp_id(Integer emp_id) { this.emp_id = emp_id; }

    public String getEmp_name() { return emp_name; }
    public void setEmp_name(String emp_name) { this.emp_name = emp_name; }

    public Integer getLeave_type() { return leave_type; }
    public void setLeave_type(Integer leave_type) { this.leave_type = leave_type; }

    public Date getStart_date() { return start_date; }
    public void setStart_date(Date start_date) { this.start_date = start_date; }

    public Date getEnd_date() { return end_date; }
    public void setEnd_date(Date end_date) { this.end_date = end_date; }

    public String getLeave_reason() { return leave_reason; }
    public void setLeave_reason(String leave_reason) { this.leave_reason = leave_reason; }

    public String getContact_info() { return contact_info; }
    public void setContact_info(String contact_info) { this.contact_info = contact_info; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Date getApply_time() { return apply_time; }
    public void setApply_time(Date apply_time) { this.apply_time = apply_time; }

    public Date getApprove_time() { return approve_time; }
    public void setApprove_time(Date approve_time) { this.approve_time = approve_time; }

    public Integer getApprove_by() { return approve_by; }
    public void setApprove_by(Integer approve_by) { this.approve_by = approve_by; }
}
