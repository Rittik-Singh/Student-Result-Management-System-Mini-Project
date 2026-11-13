package com.srms.service;

import com.srms.dao.ExaminationDAO;
import com.srms.model.Examination;

import java.util.List;

public class ExaminationService {

    private final ExaminationDAO examinationDAO = new ExaminationDAO();

    public Examination getExamById(int examId) {
        return examinationDAO.findById(examId);
    }

    public Examination getExamByCode(String code) {
        return examinationDAO.findByCode(code);
    }

    public List<Examination> getAllExaminations() {
        return examinationDAO.getAllExaminations();
    }

    public List<Examination> getExaminationsBySemester(int semester) {
        return examinationDAO.getExaminationsBySemester(semester);
    }

    public boolean createExamination(Examination exam) {
        return examinationDAO.createExamination(exam);
    }

    public boolean updateExamination(Examination exam) {
        return examinationDAO.updateExamination(exam);
    }

    public boolean updatePublishStatus(int examId, boolean isPublished) {
        return examinationDAO.updatePublishStatus(examId, isPublished);
    }

    public int getTotalExamCount() {
        return examinationDAO.getTotalCount();
    }

    public int getPublishedExamCount() {
        return examinationDAO.getPublishedCount();
    }
}
