package com.srms.model;

import java.sql.Timestamp;

public class Student {
    private int studentId;
    private Integer userId;
    private String rollNo;
    private String registrationNo;
    private String fullName;
    private String email;
    private String phone;
    private int deptId;
    private String deptCode;
    private String deptName;
    private int currentSemester;
    private String collegeName;
    private String programName;
    private int admissionYear;
    private Timestamp createdAt;

    public Student() {}

    public Student(int studentId, Integer userId, String rollNo, String registrationNo, String fullName,
                   String email, String phone, int deptId, int currentSemester,
                   String collegeName, String programName, int admissionYear, Timestamp createdAt) {
        this.studentId = studentId;
        this.userId = userId;
        this.rollNo = rollNo;
        this.registrationNo = registrationNo;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.deptId = deptId;
        this.currentSemester = currentSemester;
        this.collegeName = collegeName;
        this.programName = programName;
        this.admissionYear = admissionYear;
        this.createdAt = createdAt;
    }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

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

    public int getCurrentSemester() { return currentSemester; }
    public void setCurrentSemester(int currentSemester) { this.currentSemester = currentSemester; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public int getAdmissionYear() { return admissionYear; }
    public void setAdmissionYear(int admissionYear) { this.admissionYear = admissionYear; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return rollNo + " - " + fullName;
    }
}
