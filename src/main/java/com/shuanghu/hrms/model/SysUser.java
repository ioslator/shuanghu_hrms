package com.shuanghu.hrms.model;

public class SysUser {
    private Integer user_id;
    private String username;
    private String password;
    private Integer emp_id; // 关联员工, 访客为0
    private Integer user_role; // 1超级管理员 2管理人员 3普通员工
    private Integer user_status; // 1启用 0禁用

    public Integer getUser_id() { return user_id; }
    public void setUser_id(Integer user_id) { this.user_id = user_id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Integer getEmp_id() { return emp_id; }
    public void setEmp_id(Integer emp_id) { this.emp_id = emp_id; }

    public Integer getUser_role() { return user_role; }
    public void setUser_role(Integer user_role) { this.user_role = user_role; }

    public Integer getUser_status() { return user_status; }
    public void setUser_status(Integer user_status) { this.user_status = user_status; }
}