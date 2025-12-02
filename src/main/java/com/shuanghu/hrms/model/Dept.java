package com.shuanghu.hrms.model;

public class Dept {
    private int dept_id;
    private String dept_name;
    private Integer dept_parent_id; // 允许为null
    private Integer dept_manage_id;
    private int dept_status;
    private String create_time;

    public int getDept_id() { return dept_id; }
    public void setDept_id(int dept_id) { this.dept_id = dept_id; }
    public String getDept_name() {
        return dept_name;
    }
    public void setDept_name(String dept_name) {
        this.dept_name = dept_name;
    }
    public Integer getDept_parent_id() {
        return dept_parent_id;
    }
    public void setDept_parent_id(Integer dept_parent_id) {
        this.dept_parent_id = dept_parent_id;
    }
    public Integer getDept_manage_id() {
        return dept_manage_id;
    }
    public void setDept_manage_id(Integer dept_manage_id) {
        this.dept_manage_id = dept_manage_id;
    }
    public int getDept_status() {
        return dept_status;
    }
    public void setDept_status(int dept_status) {
        this.dept_status = dept_status;
    }
    public String getCreate_time() {
        return create_time;
    }
    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }
}