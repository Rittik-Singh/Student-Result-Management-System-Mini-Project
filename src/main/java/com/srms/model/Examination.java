package com.srms.model;

import java.sql.Timestamp;

public class Examination {
    private int examId;
    private String examCode;
    private String examName;
    private String academicSession;
    private int semesterNumber;
    private String examType; // 'REGULAR', 'BACKLOG'
    private boolean isPublished;
    private Timestamp publishedAt;
    private String status; // 'SCHEDULED', 'MARKS_ENTRY', 'PROCESSED', 'PUBLISHED', 'CLOSED'
    private Timestamp createdAt;

    public Examination() {}

    public Examination(int examId, String examCode, String examName, String academicSession,
                       int semesterNumber, String examType, boolean isPublished,
                       Timestamp publishedAt, String status, Timestamp createdAt) {
        this.examId = examId;
        this.examCode = examCode;
        this.examName = examName;
        this.academicSession = academicSession;
        this.semesterNumber = semesterNumber;
        this.examType = examType;
        this.isPublished = isPublished;
        this.publishedAt = publishedAt;
        this.status = status;
        this.createdAt = createdAt;
    }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamCode() { return examCode; }
    public void setExamCode(String examCode) { this.examCode = examCode; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getAcademicSession() { return academicSession; }
    public void setAcademicSession(String academicSession) { this.academicSession = academicSession; }

    public int getSemesterNumber() { return semesterNumber; }
    public void setSemesterNumber(int semesterNumber) { this.semesterNumber = semesterNumber; }

    public String getExamType() { return examType; }
    public void setExamType(String examType) { this.examType = examType; }

    public boolean isPublished() { return isPublished; }
    public void setPublished(boolean published) { isPublished = published; }

    public Timestamp getPublishedAt() { return publishedAt; }
    public void setPublishedAt(Timestamp publishedAt) { this.publishedAt = publishedAt; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    @Override
    public String toString() {
        return examName + " (" + academicSession + ")";
    }
}
