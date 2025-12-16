package com.shuanghu.hrms.model;

import java.util.Date;

public class Employee {
    private Integer emp_id;
    private String emp_no;
    private String emp_name;
    private String emp_gender;
    private Date emp_birth;
    private String emp_idcard;
    private String emp_phone;
    private String emp_email;
    private Integer dept_id;
    private Integer title_id;
    private Integer work_type_id;
    private Date hire_date;
    private Integer emp_status; // 1在职 2离职 0禁用
    private Date create_time;
    private Date update_time;

    public Integer getEmp_id() { return emp_id; }
    public void setEmp_id(Integer emp_id) { this.emp_id = emp_id; }

    public String getEmp_no() { return emp_no; }
    public void setEmp_no(String emp_no) { this.emp_no = emp_no; }

    public String getEmp_name() { return emp_name; }
    public void setEmp_name(String emp_name) { this.emp_name = emp_name; }

    public String getEmp_gender() { return emp_gender; }
    public void setEmp_gender(String emp_gender) { this.emp_gender = emp_gender; }

    public Date getEmp_birth() { return emp_birth; }
    public void setEmp_birth(Date emp_birth) { this.emp_birth = emp_birth; }

    public String getEmp_idcard() { return emp_idcard; }
    public void setEmp_idcard(String emp_idcard) { this.emp_idcard = emp_idcard; }

    public String getEmp_phone() { return emp_phone; }
    public void setEmp_phone(String emp_phone) { this.emp_phone = emp_phone; }

    public String getEmp_email() { return emp_email; }
    public void setEmp_email(String emp_email) { this.emp_email = emp_email; }

    public Integer getDept_id() { return dept_id; }
    public void setDept_id(Integer dept_id) { this.dept_id = dept_id; }

    public Integer getTitle_id() { return title_id; }
    public void setTitle_id(Integer title_id) { this.title_id = title_id; }

    public Integer getWork_type_id() { return work_type_id; }
    public void setWork_type_id(Integer work_type_id) { this.work_type_id = work_type_id; }

    public Date getHire_date() { return hire_date; }
    public void setHire_date(Date hire_date) { this.hire_date = hire_date; }

    public Integer getEmp_status() { return emp_status; }
    public void setEmp_status(Integer emp_status) { this.emp_status = emp_status; }

    public Date getCreate_time() { return create_time; }
    public void setCreate_time(Date create_time) { this.create_time = create_time; }

    public Date getUpdate_time() { return update_time; }
    public void setUpdate_time(Date update_time) { this.update_time = update_time; }
}