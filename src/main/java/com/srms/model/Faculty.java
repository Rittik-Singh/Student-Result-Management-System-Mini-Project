package com.srms.model;

public class Faculty {
    private int facultyId;
    private Integer userId;
    private String facultyCode;
    private String fullName;
    private String email;
    private String phone;
    private int deptId;
    private String deptCode;
    private String deptName;
    private String designation;
    private String status;

    public Faculty() {}

    public Faculty(int facultyId, Integer userId, String facultyCode, String fullName,
                   String email, String phone, int deptId, String designation, String status) {
        this.facultyId = facultyId;
        this.userId = userId;
        this.facultyCode = facultyCode;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.deptId = deptId;
        this.designation = designation;
        this.status = status;
    }

    public int getFacultyId() { return facultyId; }
    public void setFacultyId(int facultyId) { this.facultyId = facultyId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getFacultyCode() { return facultyCode; }
    public void setFacultyCode(String facultyCode) { this.facultyCode = facultyCode; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return facultyCode + " - " + fullName;
    }
}
