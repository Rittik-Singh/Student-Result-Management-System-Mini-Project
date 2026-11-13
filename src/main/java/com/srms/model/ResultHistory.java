package com.srms.model;

import java.sql.Timestamp;

public class ResultHistory {
    private int historyId;
    private Integer resultId;
    private int studentId;
    private String rollNo;
    private String studentName;
    private Integer subjectId;
    private String subjectCode;
    private String subjectName;
    private int examId;
    private String examName;
    private String fieldChanged;
    private String oldValue;
    private String newValue;
    private Integer modifiedByUserId;
    private String modifiedByUsername;
    private Timestamp modifiedAt;
    private String reason;

    public ResultHistory() {}

    public ResultHistory(int historyId, Integer resultId, int studentId, Integer subjectId,
                         int examId, String fieldChanged, String oldValue, String newValue,
                         Integer modifiedByUserId, Timestamp modifiedAt, String reason) {
        this.historyId = historyId;
        this.resultId = resultId;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.examId = examId;
        this.fieldChanged = fieldChanged;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.modifiedByUserId = modifiedByUserId;
        this.modifiedAt = modifiedAt;
        this.reason = reason;
    }

    public int getHistoryId() { return historyId; }
    public void setHistoryId(int historyId) { this.historyId = historyId; }

    public Integer getResultId() { return resultId; }
    public void setResultId(Integer resultId) { this.resultId = resultId; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public String getRollNo() { return rollNo; }
    public void setRollNo(String rollNo) { this.rollNo = rollNo; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public Integer getSubjectId() { return subjectId; }
    public void setSubjectId(Integer subjectId) { this.subjectId = subjectId; }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public int getExamId() { return examId; }
    public void setExamId(int examId) { this.examId = examId; }

    public String getExamName() { return examName; }
    public void setExamName(String examName) { this.examName = examName; }

    public String getFieldChanged() { return fieldChanged; }
    public void setFieldChanged(String fieldChanged) { this.fieldChanged = fieldChanged; }

    public String getOldValue() { return oldValue; }
    public void setOldValue(String oldValue) { this.oldValue = oldValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public Integer getModifiedByUserId() { return modifiedByUserId; }
    public void setModifiedByUserId(Integer modifiedByUserId) { this.modifiedByUserId = modifiedByUserId; }

    public String getModifiedByUsername() { return modifiedByUsername; }
    public void setModifiedByUsername(String modifiedByUsername) { this.modifiedByUsername = modifiedByUsername; }

    public Timestamp getModifiedAt() { return modifiedAt; }
    public void setModifiedAt(Timestamp modifiedAt) { this.modifiedAt = modifiedAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
