package com.srms.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class GradingUtil {

    public static class GradeInfo {
        private final String letterGrade;
        private final int points;
        private final String classification;

        public GradeInfo(String letterGrade, int points, String classification) {
            this.letterGrade = letterGrade;
            this.points = points;
            this.classification = classification;
        }

        public String getLetterGrade() { return letterGrade; }
        public int getPoints() { return points; }
        public String getClassification() { return classification; }
    }

    /**
     * Calculates MAKAUT Letter Grade and Grade Points based on 100 percentage score.
     */
    public static GradeInfo calculateGrade(double totalMarksOutOf100) {
        // Round to nearest integer standard or evaluate ranges:
        // 90 to 100 -> O (10)
        // 80 to 89  -> E (9)
        // 70 to 79  -> A (8)
        // 60 to 69  -> B (7)
        // 50 to 59  -> C (6)
        // 40 to 49  -> D (5)
        // Below 40  -> F (2)

        double score = Math.round(totalMarksOutOf100);

        if (score >= 90.0) {
            return new GradeInfo("O", 10, "Outstanding");
        } else if (score >= 80.0) {
            return new GradeInfo("E", 9, "Excellent");
        } else if (score >= 70.0) {
            return new GradeInfo("A", 8, "Very Good");
        } else if (score >= 60.0) {
            return new GradeInfo("B", 7, "Good");
        } else if (score >= 50.0) {
            return new GradeInfo("C", 6, "Fair");
        } else if (score >= 40.0) {
            return new GradeInfo("D", 5, "Below Average");
        } else {
            return new GradeInfo("F", 2, "Failed");
        }
    }

    /**
     * Calculates Credit Points = Grade Point * Subject Credits.
     */
    public static double calculateCreditPoints(int gradePoints, double credits) {
        BigDecimal gp = BigDecimal.valueOf(gradePoints);
        BigDecimal cr = BigDecimal.valueOf(credits);
        return gp.multiply(cr).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Calculates SGPA = Sum(Credit Points) / Sum(Credits).
     */
    public static double calculateSgpa(double totalCreditPoints, double totalCredits) {
        if (totalCredits <= 0.0) return 0.0;
        BigDecimal tcp = BigDecimal.valueOf(totalCreditPoints);
        BigDecimal tc = BigDecimal.valueOf(totalCredits);
        return tcp.divide(tc, 2, RoundingMode.HALF_UP).doubleValue();
    }

    /**
     * Determines Result Status according to MAKAUT rules:
     * P = Passed and Promoted (no backlogs, all subjects >= D)
     * XP = Eligible for Promotion with Backlogs (1 to 4 failed subjects)
     * X = Not eligible for Promotion (more than 4 failed subjects)
     */
    public static String determineResultStatus(int backlogCount) {
        if (backlogCount == 0) {
            return "P";
        } else if (backlogCount <= 4) {
            return "XP";
        } else {
            return "X";
        }
    }
}
