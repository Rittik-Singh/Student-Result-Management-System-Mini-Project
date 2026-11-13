package com.srms.model;

public class ResultItem {
    private String subjectCode;
    private String subjectName;
    private String letterGrade;
    private int points; // Grade Point (e.g. 10, 9, 8, 7, 6, 5, 2)
    private double credit; // e.g. 4.0, 3.0, 2.0, 1.5, 1.0
    private double creditPoints; // points * credit
    private double marksObtained;
    private double maxMarks;

    public ResultItem() {}

    public ResultItem(String subjectCode, String subjectName, String letterGrade, int points, double credit, double creditPoints) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.letterGrade = letterGrade;
        this.points = points;
        this.credit = credit;
        this.creditPoints = creditPoints;
    }

    public ResultItem(String subjectCode, String subjectName, String letterGrade, int points, double credit, double creditPoints, double marksObtained, double maxMarks) {
        this(subjectCode, subjectName, letterGrade, points, credit, creditPoints);
        this.marksObtained = marksObtained;
        this.maxMarks = maxMarks;
    }

    public String getSubjectCode() { return subjectCode; }
    public void setSubjectCode(String subjectCode) { this.subjectCode = subjectCode; }

    public String getSubjectName() { return subjectName; }
    public void setSubjectName(String subjectName) { this.subjectName = subjectName; }

    public String getLetterGrade() { return letterGrade; }
    public void setLetterGrade(String letterGrade) { this.letterGrade = letterGrade; }

    public int getPoints() { return points; }
    public void setPoints(int points) { this.points = points; }

    public double getCredit() { return credit; }
    public void setCredit(double credit) { this.credit = credit; }

    public double getCreditPoints() { return creditPoints; }
    public void setCreditPoints(double creditPoints) { this.creditPoints = creditPoints; }

    public double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(double marksObtained) { this.marksObtained = marksObtained; }

    public double getMaxMarks() { return maxMarks; }
    public void setMaxMarks(double maxMarks) { this.maxMarks = maxMarks; }
}
