package com.srms.service;

import com.srms.dao.FacultyDAO;
import com.srms.model.Faculty;
import com.srms.model.Subject;

import java.util.List;

public class FacultyService {

    private final FacultyDAO facultyDAO = new FacultyDAO();

    public Faculty getFacultyById(int facultyId) {
        return facultyDAO.findById(facultyId);
    }

    public Faculty getFacultyByUserId(int userId) {
        return facultyDAO.findByUserId(userId);
    }

    public List<Faculty> getAllFaculty() {
        return facultyDAO.getAllFaculty();
    }

    public boolean createFaculty(Faculty faculty, String plainPassword) {
        return facultyDAO.createFacultyWithUser(faculty, plainPassword);
    }

    public boolean updateFaculty(Faculty faculty) {
        return facultyDAO.updateFaculty(faculty);
    }

    public boolean deleteFaculty(int facultyId) {
        return facultyDAO.deleteFaculty(facultyId);
    }

    public boolean assignSubject(int facultyId, int subjectId, String session, int semester) {
        return facultyDAO.assignSubject(facultyId, subjectId, session, semester);
    }

    public List<Subject> getAssignedSubjects(int facultyId) {
        return facultyDAO.getAssignedSubjects(facultyId);
    }

    public int getTotalFacultyCount() {
        return facultyDAO.getTotalCount();
    }
}
