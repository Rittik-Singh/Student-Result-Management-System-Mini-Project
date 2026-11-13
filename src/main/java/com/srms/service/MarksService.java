package com.srms.service;

import com.srms.dao.MarksDAO;
import com.srms.dao.SubjectDAO;
import com.srms.model.Marks;
import com.srms.model.Subject;
import com.srms.util.GradingUtil;
import com.srms.util.ValidationUtil;

import java.util.List;

public class MarksService {

    private final MarksDAO marksDAO = new MarksDAO();
    private final SubjectDAO subjectDAO = new SubjectDAO();

    public List<Marks> getMarksByStudentAndExam(int studentId, int examId) {
        return marksDAO.getMarksByStudentAndExam(studentId, examId);
    }

    public List<Marks> getMarksBySubjectAndExam(int subjectId, int examId) {
        return marksDAO.getMarksBySubjectAndExam(subjectId, examId);
    }

    public boolean saveMarks(Marks marks, int modifiedByUserId, String reason) throws Exception {
        Subject sub = subjectDAO.findById(marks.getSubjectId());
        if (sub == null) {
            throw new IllegalArgumentException("Subject not found: ID " + marks.getSubjectId());
        }

        if (!ValidationUtil.isValidMarks(marks.getCaMarks(), sub.getMaxCaMarks())) {
            throw new IllegalArgumentException("Continuous Assessment marks must be between 0 and " + sub.getMaxCaMarks());
        }
        if (!ValidationUtil.isValidMarks(marks.getFinalMarks(), sub.getMaxFinalMarks())) {
            throw new IllegalArgumentException("Final Exam marks must be between 0 and " + sub.getMaxFinalMarks());
        }

        double total = marks.getCaMarks() + marks.getFinalMarks();
        // Scale to 100 if subject max total marks differs from 100
        double scaleOutOf100 = sub.getMaxTotalMarks() > 0 ? (total / sub.getMaxTotalMarks()) * 100.0 : total;

        GradingUtil.GradeInfo gradeInfo = GradingUtil.calculateGrade(scaleOutOf100);
        double creditPoints = GradingUtil.calculateCreditPoints(gradeInfo.getPoints(), sub.getCredits());

        marks.setTotalMarks(total);
        marks.setLetterGrade(gradeInfo.getLetterGrade());
        marks.setGradePoint(gradeInfo.getPoints());
        marks.setCreditPoints(creditPoints);

        return marksDAO.saveOrUpdateMarks(marks, modifiedByUserId, reason);
    }

    public int getPendingMarksCount() {
        return marksDAO.getPendingMarksCount();
    }
}
