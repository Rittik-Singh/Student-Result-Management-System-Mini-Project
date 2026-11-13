package com.srms.model;

import java.sql.Timestamp;

public class Marks {
    private int marksId;
    private int studentId;
    private String studentName;
    private String rollNo;
    private int subjectId;
    private String subjectCode;
    private String subjectName;
    private double credits;
    private String courseType;
    private int examId;
    private String examName;
    private double caMarks;
    private double finalMarks;
    private double totalMarks;
    private String letterGrade;
    private int gradePoint;
    private double creditPoints;
    private boolean isSubmitted;
    private Integer enteredByFacultyId;
    private Timestamp updatedAt;

    public Marks() {}

    public Marks(int marksId, int studentId, int subjectId, int examId,
                 double caMarks, double finalMarks, double totalMarks,
                 String letterGrade, int gradePoint, double creditPoints,
                 boolean isSubmitted, Integer enteredByFacultyId, Timestamp updatedAt) {
        this.marksId = marksId;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.examId = examId;
        this.caMarks = caMarks;
        this.finalMarks = finalMarks;
        this.totalMarks = totalMarks;
        this.letterGrade = letterGrade;
        this.gradePoint = gradePoint;
        this.creditPoints = creditPoints;
        this.isSubmitted = isSubmitted;
        this.enteredByFacultyId = enteredByFacultyId;
        this.updatedAt = updatedAt;
    }

    public int getMarksId() { return marksId; }
    public void setMarksId(int marksId) { this.marksId = marksId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public double getCredits() { return credits; }
    public void setCredits(double credits) { this.credits = credits; }

    public String getCourseType() { return courseType; }
    public void setCourseType(String courseType) { this.courseType = courseType; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public double getCaMarks() { return caMarks; }
    public void setCaMarks(double caMarks) { this.caMarks = caMarks; }

    public double getFinalMarks() { return finalMarks; }
    public void setFinalMarks(double finalMarks) { this.finalMarks = finalMarks; }

    public double getTotalMarks() { return totalMarks; }
    public void setTotalMarks(double totalMarks) { this.totalMarks = totalMarks; }

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

    public int getGradePoint() { return gradePoint; }
    public void setGradePoint(int gradePoint) { this.gradePoint = gradePoint; }

    public double getCreditPoints() { return creditPoints; }
    public void setCreditPoints(double creditPoints) { this.creditPoints = creditPoints; }

    public boolean isSubmitted() { return isSubmitted; }
    public void setSubmitted(boolean submitted) { isSubmitted = submitted; }

    public Integer getEnteredByFacultyId() { return enteredByFacultyId; }
    public void setEnteredByFacultyId(Integer enteredByFacultyId) { this.enteredByFacultyId = enteredByFacultyId; }

    public Timestamp getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt) { this.updatedAt = updatedAt; }
}
