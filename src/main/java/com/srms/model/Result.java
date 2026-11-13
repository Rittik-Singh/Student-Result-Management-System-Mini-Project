package com.srms.model;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class Result {
    private int resultId;
    private int studentId;
    private String studentName;
    private String rollNo;
    private String registrationNo;
    private String collegeName;
    private String programName;
    private int examId;
    private String examName;
    private String academicSession;
    private int semesterNumber;
    private double totalMarks;
    private double maxMarks;
    private double percentage;
    private double totalCredits;
    private double totalCreditPoints;
    private double sgpa;
    private Double ygpa;
    private Double cgpa;
    private Double dgpa;
    private String resultStatus; // 'P', 'XP', 'X'
    private int backlogCount;
    private boolean isPublished;
    private Timestamp calculatedAt;
    private List<ResultItem> items = new ArrayList<>();

    public Result() {}

    public Result(int resultId, int studentId, int examId, int semesterNumber,
                  double totalMarks, double maxMarks, double percentage,
                  double totalCredits, double totalCreditPoints, double sgpa,
                  Double ygpa, Double cgpa, Double dgpa,
                  String resultStatus, int backlogCount, boolean isPublished, Timestamp calculatedAt) {
        this.resultId = resultId;
        this.studentId = studentId;
        this.examId = examId;
        this.semesterNumber = semesterNumber;
        this.totalMarks = totalMarks;
        this.maxMarks = maxMarks;
        this.percentage = percentage;
        this.totalCredits = totalCredits;
        this.totalCreditPoints = totalCreditPoints;
        this.sgpa = sgpa;
        this.ygpa = ygpa;
        this.cgpa = cgpa;
        this.dgpa = dgpa;
        this.resultStatus = resultStatus;
        this.backlogCount = backlogCount;
        this.isPublished = isPublished;
        this.calculatedAt = calculatedAt;
    }

    public int getResultId() { return resultId; }
    public void setResultId(int resultId) { this.resultId = resultId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getRegistrationNo() { return registrationNo; }
    public void setRegistrationNo(String registrationNo) { this.registrationNo = registrationNo; }

    public String getCollegeName() { return collegeName; }
    public void setCollegeName(String collegeName) { this.collegeName = collegeName; }

    public String getProgramName() { return programName; }
    public void setProgramName(String programName) { this.programName = programName; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getAcademicSession() { return academicSession; }
    public void setAcademicSession(String academicSession) { this.academicSession = academicSession; }

    public int getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(int semesterNumber) { this.semesterNumber = semesterNumber; }

    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; }

    public double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(double maxMarks) { this.maxMarks = maxMarks; }

    public double getPercentage() { return percentage; }
    public void setPercentage(double percentage) { this.percentage = percentage; }

    public double getTotalCredits() { return totalCredits; }
    public void setTotalCredits(double totalCredits) { this.totalCredits = totalCredits; }

    public double getTotalCreditPoints() { return totalCreditPoints; }
    public void setTotalCreditPoints(double totalCreditPoints) { this.totalCreditPoints = totalCreditPoints; }

    public double getSgpa() { return sgpa; }
    public void setSgpa(double sgpa) { this.sgpa = sgpa; }

    public Double getYgpa() { return ygpa; }
    public void setYgpa(Double ygpa) { this.ygpa = ygpa; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public Double getDgpa() { return dgpa; }
    public void setDgpa(Double dgpa) { this.dgpa = dgpa; }

    public String getResultStatus() { return resultStatus; }
    public void setResultStatus(String resultStatus) { this.resultStatus = resultStatus; }

    public int getBacklogCount() { return backlogCount; }
    public void setBacklogCount(int backlogCount) { this.backlogCount = backlogCount; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public Timestamp getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(Timestamp calculatedAt) { this.calculatedAt = calculatedAt; }

    public List<ResultItem> getItems() { return items; }
    public void setItems(List<ResultItem> items) { this.items = items; }

    public String getStatusDescription() {
        if ("P".equalsIgnoreCase(resultStatus)) return "Passed and Promoted";
        if ("XP".equalsIgnoreCase(resultStatus)) return "Eligible for Promotion with Backlogs (" + backlogCount + ")";
        if ("X".equalsIgnoreCase(resultStatus)) return "Not eligible for Semester Promotion";
        return resultStatus;
    }
}
