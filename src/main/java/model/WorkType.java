package model;

public class WorkType {
    private int work_type_id;
    private String work_type_name;
    private String work_type_desc;
    private int work_type_status;

    // 无参构造方法
    public WorkType() {
    }

    // getter和setter方法
    public int getWork_type_id() {
        return work_type_id;
    }

    public void setWork_type_id(int work_type_id) {
        this.work_type_id = work_type_id;
    }

    public String getWork_type_name() {
        return work_type_name;
    }

    public void setWork_type_name(String work_type_name) {
        this.work_type_name = work_type_name;
    }

    public String getWork_type_desc() {
        return work_type_desc;
    }

    public void setWork_type_desc(String work_type_desc) {
        this.work_type_desc = work_type_desc;
    }

    public int getWork_type_status() {
        return work_type_status;
    }

    public void setWork_type_status(int work_type_status) {
        this.work_type_status = work_type_status;
    }
}