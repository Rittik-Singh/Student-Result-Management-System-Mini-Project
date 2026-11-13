package com.srms.model;

public class Subject {
    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private int deptId;
    private String deptCode;
    private String deptName;
    private int semesterNumber;
    private double credits;
    private String courseType; // 'THEORY', 'PRACTICAL', 'SESSIONAL', 'MANDATORY'
    private double maxCaMarks;
    private double maxFinalMarks;
    private double maxTotalMarks;
    private String status;

    public Subject() {}

    public Subject(int subjectId, String subjectCode, String subjectName, int deptId,
                   int semesterNumber, double credits, String courseType,
                   double maxCaMarks, double maxFinalMarks, double maxTotalMarks, String status) {
        this.subjectId = subjectId;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.deptId = deptId;
        this.semesterNumber = semesterNumber;
        this.credits = credits;
        this.courseType = courseType;
        this.maxCaMarks = maxCaMarks;
        this.maxFinalMarks = maxFinalMarks;
        this.maxTotalMarks = maxTotalMarks;
        this.status = status;
    }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getDeptId() { return deptId; }
    public void setDeptId(int deptId) { this.deptId = deptId; }

    public String getDeptCode() { return deptCode; }
    public void setDeptCode(String deptCode) { this.deptCode = deptCode; }

    public String getDeptName() { return deptName; }
    public void setDeptName(String deptName) { this.deptName = deptName; }

    public int getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(int semesterNumber) { this.semesterNumber = semesterNumber; }

    public double getCredits() { return credits; }
    public void setCredits(double credits) { this.credits = credits; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    public double getMaxCaMarks() { return maxCaMarks; }
    public void setMaxCaMarks(double maxCaMarks) { this.maxCaMarks = maxCaMarks; }

    public double getMaxFinalMarks() { return maxFinalMarks; }
    public void setMaxFinalMarks(double maxFinalMarks) { this.maxFinalMarks = maxFinalMarks; }

    public double getMaxTotalMarks() { return maxTotalMarks; }
    public void setMaxTotalMarks(double maxTotalMarks) { this.maxTotalMarks = maxTotalMarks; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return subjectCode + " - " + subjectName + " (" + credits + " Cr)";
    }
}
