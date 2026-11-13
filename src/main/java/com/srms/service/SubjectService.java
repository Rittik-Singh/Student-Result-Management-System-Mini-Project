package com.srms.service;

import com.srms.dao.SubjectDAO;
import com.srms.model.Department;
import com.srms.model.Subject;

import java.util.List;

public class SubjectService {

    private final SubjectDAO subjectDAO = new SubjectDAO();

    public Subject getSubjectById(int subjectId) {
        return subjectDAO.findById(subjectId);
    }

    public Subject getSubjectByCode(String code) {
        return subjectDAO.findByCode(code);
    }

    public List<Subject> getAllSubjects() {
        return subjectDAO.getAllSubjects();
    }

    public List<Subject> getSubjectsBySemester(int semesterNumber, Integer deptId) {
        return subjectDAO.getSubjectsBySemester(semesterNumber, deptId);
    }

    public boolean createSubject(Subject subject) {
        return subjectDAO.createSubject(subject);
    }

    public boolean updateSubject(Subject subject) {
        return subjectDAO.updateSubject(subject);
    }

    public boolean deleteSubject(int subjectId) {
        return subjectDAO.deleteSubject(subjectId);
    }

    public List<Department> getAllDepartments() {
        return subjectDAO.getAllDepartments();
    }

    public int getTotalSubjectCount() {
        return subjectDAO.getTotalCount();
    }
}
