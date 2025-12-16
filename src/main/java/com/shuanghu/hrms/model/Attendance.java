package com.shuanghu.hrms.model;

import java.util.Date;

public class Attendance {
    private Integer attend_id;
    private Integer emp_id;
    private String emp_name; // 数据库连表查询时用
    private Date attend_date;
    private String start_time;
    private String end_time;
    private String status; // 正常/迟到/早退

    // Getters and Setters
    public Integer getAttend_id() { return attend_id; }
    public void setAttend_id(Integer attend_id) { this.attend_id = attend_id; }
    public Integer getEmp_id() { return emp_id; }
    public void setEmp_id(Integer emp_id) { this.emp_id = emp_id; }
    public String getEmp_name() { return emp_name; }
    public void setEmp_name(String emp_name) { this.emp_name = emp_name; }
    public Date getAttend_date() { return attend_date; }
    public void setAttend_date(Date attend_date) { this.attend_date = attend_date; }
    public String getStart_time() { return start_time; }
    public void setStart_time(String start_time) { this.start_time = start_time; }
    public String getEnd_time() { return end_time; }
    public void setEnd_time(String end_time) { this.end_time = end_time; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}