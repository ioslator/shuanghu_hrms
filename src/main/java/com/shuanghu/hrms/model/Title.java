package com.shuanghu.hrms.model;

public class Title {
    private Integer title_id;
    private Integer title_level; // 职称等级，越高越高级
    private String title_name;
    private String title_salary_range;

    public Integer getTitle_id() { return title_id; }
    public void setTitle_id(Integer title_id) { this.title_id = title_id; }

    public Integer getTitle_level() { return title_level; }
    public void setTitle_level(Integer title_level) { this.title_level = title_level; }

    public String getTitle_name() { return title_name; }
    public void setTitle_name(String title_name) { this.title_name = title_name; }

    public String getTitle_salary_range() { return title_salary_range; }
    public void setTitle_salary_range(String title_salary_range) { this.title_salary_range = title_salary_range; }
}