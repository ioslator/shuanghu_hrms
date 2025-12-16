package com.shuanghu.hrms.model;

import java.util.Date;

public class Notice {
    private Integer id;
    private String title;
    private String content;
    private String publishDept; // 发布部门
    private Date createTime;

    // 无参构造
    public Notice() {}

    // 全参构造
    public Notice(Integer id, String title, String content, String publishDept, Date createTime) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.publishDept = publishDept;
        this.createTime = createTime;
    }

    // Getters and Setters
    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getPublishDept() { return publishDept; }
    public void setPublishDept(String publishDept) { this.publishDept = publishDept; }
    public Date getCreateTime() { return createTime; }
    public void setCreateTime(Date createTime) { this.createTime = createTime; }
}