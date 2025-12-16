package com.shuanghu.hrms.model;

import java.util.Date;

public class Management {
    private Integer mgmt_id;
    private Integer emp_id;
    private Integer manage_dept_id;
    private String subordinate_emp_ids;
    private Date mgmt_start_time;
    private Date mgmt_end_time;

    public Integer getMgmt_id() { return mgmt_id; }
    public void setMgmt_id(Integer mgmt_id) { this.mgmt_id = mgmt_id; }

    public Integer getEmp_id() { return emp_id; }
    public void setEmp_id(Integer emp_id) { this.emp_id = emp_id; }

    public Integer getManage_dept_id() { return manage_dept_id; }
    public void setManage_dept_id(Integer manage_dept_id) { this.manage_dept_id = manage_dept_id; }

    public String getSubordinate_emp_ids() { return subordinate_emp_ids; }
    public void setSubordinate_emp_ids(String subordinate_emp_ids) { this.subordinate_emp_ids = subordinate_emp_ids; }

    public Date getMgmt_start_time() { return mgmt_start_time; }
    public void setMgmt_start_time(Date mgmt_start_time) { this.mgmt_start_time = mgmt_start_time; }

    public Date getMgmt_end_time() { return mgmt_end_time; }
    public void setMgmt_end_time(Date mgmt_end_time) { this.mgmt_end_time = mgmt_end_time; }
}