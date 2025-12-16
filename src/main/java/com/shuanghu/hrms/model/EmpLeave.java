package com.shuanghu.hrms.model;

import java.util.Date;

public class EmpLeave {
    private Integer leave_id;
    private Integer emp_id;
    private Integer leave_type; // 1退休 2开除 3主动离职 4其他
    private String leave_reason;
    private Date leave_date;
    private Integer operator_id;

    public Integer getLeave_id() { return leave_id; }
    public void setLeave_id(Integer leave_id) { this.leave_id = leave_id; }

    public Integer getEmp_id() { return emp_id; }
    public void setEmp_id(Integer emp_id) { this.emp_id = emp_id; }

    public Integer getLeave_type() { return leave_type; }
    public void setLeave_type(Integer leave_type) { this.leave_type = leave_type; }

    public String getLeave_reason() { return leave_reason; }
    public void setLeave_reason(String leave_reason) { this.leave_reason = leave_reason; }

    public Date getLeave_date() { return leave_date; }
    public void setLeave_date(Date leave_date) { this.leave_date = leave_date; }

    public Integer getOperator_id() { return operator_id; }
    public void setOperator_id(Integer operator_id) { this.operator_id = operator_id; }
}