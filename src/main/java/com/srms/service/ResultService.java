package com.srms.service;

import com.srms.dao.ExaminationDAO;
import com.srms.dao.MarksDAO;
import com.srms.dao.ResultDAO;
import com.srms.dao.StudentDAO;
import com.srms.model.Examination;
import com.srms.model.Marks;
import com.srms.model.Result;
import com.srms.model.ResultHistory;
import com.srms.model.Student;
import com.srms.util.GradingUtil;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ResultService {

    private final ResultDAO resultDAO = new ResultDAO();
    private final MarksDAO marksDAO = new MarksDAO();
    private final StudentDAO studentDAO = new StudentDAO();
    private final ExaminationDAO examinationDAO = new ExaminationDAO();

    public Result getResultByStudentAndExam(int studentId, int examId) {
        return resultDAO.getResultByStudentAndExam(studentId, examId);
    }

    public List<Result> getAllResultsByExam(int examId) {
        return resultDAO.getAllResultsByExam(examId);
    }

    public List<Result> getAllResultsByStudent(int studentId) {
        return resultDAO.getAllResultsByStudent(studentId);
    }

    public Result calculateAndSaveStudentResult(int studentId, int examId, Integer modifiedByUserId, String reason) throws Exception {
        Student student = studentDAO.findById(studentId);
        if (student == null) throw new IllegalArgumentException("Student not found: ID " + studentId);

        Examination exam = examinationDAO.findById(examId);
        if (exam == null) throw new IllegalArgumentException("Examination not found: ID " + examId);

        List<Marks> marksList = marksDAO.getMarksByStudentAndExam(studentId, examId);
        if (marksList.isEmpty()) {
            throw new IllegalStateException("No marks recorded for student " + student.getRollNo() + " in " + exam.getExamName());
        }

        double totalMarks = 0.0;
        double maxMarks = 0.0;
        double totalCredits = 0.0;
        double totalCreditPoints = 0.0;
        int backlogCount = 0;

        for (Marks m : marksList) {
            totalMarks += m.getTotalMarks();
            maxMarks += 100.0; // Standard MAKAUT 100 max points per subject
            totalCredits += m.getCredits();
            totalCreditPoints += m.getCreditPoints();

            if (m.getGradePoint() < 5) { // 'F' (2) or 'I' (2)
                backlogCount++;
            }
        }

        double percentage = maxMarks > 0 ? (totalMarks / maxMarks) * 100.0 : 0.0;
        percentage = BigDecimal.valueOf(percentage).setScale(2, RoundingMode.HALF_UP).doubleValue();

        double sgpa = GradingUtil.calculateSgpa(totalCreditPoints, totalCredits);
        String status = GradingUtil.determineResultStatus(backlogCount);

        // Fetch historical results to calculate YGPA and CGPA
        List<Result> existingResults = resultDAO.getAllResultsByStudent(studentId);
        double cumulativeCp = totalCreditPoints;
        double cumulativeCr = totalCredits;
        Double ygpa = null;

        for (Result r : existingResults) {
            if (r.getExamId() != examId) {
                cumulativeCp += r.getTotalCreditPoints();
                cumulativeCr += r.getTotalCredits();

                // If currently processing an even semester, check for matching odd semester of same year
                if (exam.getSemesterNumber() % 2 == 0 && r.getSemesterNumber() == exam.getSemesterNumber() - 1) {
                    double oddEvenCp = r.getTotalCreditPoints() + totalCreditPoints;
                    double oddEvenCr = r.getTotalCredits() + totalCredits;
                    if (oddEvenCr > 0) {
                        ygpa = BigDecimal.valueOf(oddEvenCp / oddEvenCr).setScale(2, RoundingMode.HALF_UP).doubleValue();
                    }
                }
            }
        }

        Double cgpa = cumulativeCr > 0
                ? BigDecimal.valueOf(cumulativeCp / cumulativeCr).setScale(2, RoundingMode.HALF_UP).doubleValue()
                : null;

        Result result = new Result();
        result.setStudentId(studentId);
        result.setExamId(examId);
        result.setSemesterNumber(exam.getSemesterNumber());
        result.setTotalMarks(totalMarks);
        result.setMaxMarks(maxMarks);
        result.setPercentage(percentage);
        result.setTotalCredits(totalCredits);
        result.setTotalCreditPoints(totalCreditPoints);
        result.setSgpa(sgpa);
        result.setYgpa(ygpa);
        result.setCgpa(cgpa);
        result.setResultStatus(status);
        result.setBacklogCount(backlogCount);
        result.setPublished(exam.isPublished());

        boolean saved = resultDAO.saveOrUpdateResult(result, modifiedByUserId, reason);
        if (!saved) throw new RuntimeException("Failed to save calculated result to database.");

        return resultDAO.getResultByStudentAndExam(studentId, examId);
    }

    public int processAllResultsForExam(int examId, Integer adminUserId) throws Exception {
        Examination exam = examinationDAO.findById(examId);
        if (exam == null) throw new IllegalArgumentException("Examination not found: ID " + examId);

        // Find all students who have marks in this exam
        String sql = "SELECT DISTINCT student_id FROM marks WHERE exam_id = ?";
        Set<Integer> studentIds = new HashSet<>();
        try (var conn = com.srms.config.DatabaseConnection.getConnection();
             var ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (var rs = ps.executeQuery()) {
                while (rs.next()) {
                    studentIds.add(rs.getInt("student_id"));
                }
            }
        }

        int processedCount = 0;
        for (int stuId : studentIds) {
            calculateAndSaveStudentResult(stuId, examId, adminUserId, "Automated semester result batch processing");
            processedCount++;
        }

        // Update exam status to PROCESSED
        if (!exam.isPublished()) {
            exam.setStatus("PROCESSED");
            examinationDAO.updateExamination(exam);
        }

        return processedCount;
    }

    public boolean publishResults(int examId, int adminUserId, String notes) {
        return resultDAO.publishExamResults(examId, adminUserId, notes);
    }

    public List<Result> getToppers(int examId, int limit) {
        return resultDAO.getToppersByExam(examId, limit);
    }

    public List<Result> getBacklogStudents(int examId) {
        return resultDAO.getBacklogStudentsByExam(examId);
    }

    public List<ResultHistory> getResultHistory(int examId, Integer studentId) {
        return resultDAO.getResultHistory(examId, studentId);
    }
}
