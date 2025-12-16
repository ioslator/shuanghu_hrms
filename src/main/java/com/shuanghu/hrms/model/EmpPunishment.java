package com.shuanghu.hrms.model;

import java.math.BigDecimal;
import java.util.Date;

public class EmpPunishment {
    private Integer punish_id;
    private Integer emp_id;
    private Integer punish_type; // 1口头警告 2书面警告 3罚款 4记过
    private String punish_reason;
    private String punish_detail;
    private BigDecimal punish_amount;
    private Date punish_date;
    private Integer operator_id;

    public Integer getPunish_id() { return punish_id; }
    public void setPunish_id(Integer punish_id) { this.punish_id = punish_id; }

    public Integer getEmp_id() { return emp_id; }
    public void setEmp_id(Integer emp_id) { this.emp_id = emp_id; }

    public Integer getPunish_type() { return punish_type; }
    public void setPunish_type(Integer punish_type) { this.punish_type = punish_type; }

    public String getPunish_reason() { return punish_reason; }
    public void setPunish_reason(String punish_reason) { this.punish_reason = punish_reason; }

    public String getPunish_detail() { return punish_detail; }
    public void setPunish_detail(String punish_detail) { this.punish_detail = punish_detail; }

    public BigDecimal getPunish_amount() { return punish_amount; }
    public void setPunish_amount(BigDecimal punish_amount) { this.punish_amount = punish_amount; }

    public Date getPunish_date() { return punish_date; }
    public void setPunish_date(Date punish_date) { this.punish_date = punish_date; }

    public Integer getOperator_id() { return operator_id; }
    public void setOperator_id(Integer operator_id) { this.operator_id = operator_id; }
}