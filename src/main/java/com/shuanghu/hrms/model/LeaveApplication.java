package com.shuanghu.hrms.model;

import java.util.Date;

/**
 * 请假申请实体类
 * 对应数据库表: leave_application
 */
public class LeaveApplication {

    private Integer id;
    private Integer empId;
    private String leaveType; // 请假类型
    private Date startTime;   // 开始时间
    private Date endTime;     // 结束时间
    private String reason;    // 请假原因
    private Integer status;   // 状态: 0=待审批, 1=已通过, 2=已驳回
    private Date createTime;  // 申请时间
    private String auditRemark; // 审批备注

    // --- 辅助字段 (数据库中没有，但在列表显示时很有用) ---
    private String empName;

    // 无参构造方法
    public LeaveApplication() {
    }

    // 全参构造方法
    public LeaveApplication(Integer id, Integer empId, String leaveType, Date startTime, Date endTime, String reason, Integer status, Date createTime, String auditRemark) {
        this.id = id;
        this.empId = empId;
        this.leaveType = leaveType;
        this.startTime = startTime;
        this.endTime = endTime;
        this.reason = reason;
        this.status = status;
        this.createTime = createTime;
        this.auditRemark = auditRemark;
    }

    // --- Getter 和 Setter 方法 ---

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getEmpId() {
        return empId;
    }

    public void setEmpId(Integer empId) {
        this.empId = empId;
    }

    public String getLeaveType() {
        return leaveType;
    }

    public void setLeaveType(String leaveType) {
        this.leaveType = leaveType;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public String getAuditRemark() {
        return auditRemark;
    }

    public void setAuditRemark(String auditRemark) {
        this.auditRemark = auditRemark;
    }

    public String getEmpName() {
        return empName;
    }

    public void setEmpName(String empName) {
        this.empName = empName;
    }

    @Override
    public String toString() {
        return "LeaveApplication{" +
                "id=" + id +
                ", empId=" + empId +
                ", leaveType='" + leaveType + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", reason='" + reason + '\'' +
                ", status=" + status +
                ", createTime=" + createTime +
                ", auditRemark='" + auditRemark + '\'' +
                '}';
    }
}