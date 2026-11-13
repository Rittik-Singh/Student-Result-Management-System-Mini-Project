package com.srms.service;

import com.srms.dao.StudentDAO;
import com.srms.model.Student;

import java.util.List;

public class StudentService {

    private final StudentDAO studentDAO = new StudentDAO();

    public Student getStudentById(int studentId) {
        return studentDAO.findById(studentId);
    }

    public Student getStudentByRollNo(String rollNo) {
        return studentDAO.findByRollNo(rollNo);
    }

    public Student getStudentByUserId(int userId) {
        return studentDAO.findByUserId(userId);
    }

    public List<Student> getAllStudents() {
        return studentDAO.getAllStudents();
    }

    public List<Student> searchStudents(String keyword, Integer deptId, Integer semester) {
        return studentDAO.searchStudents(keyword, deptId, semester);
    }

    public boolean createStudent(Student student, String password) {
        return studentDAO.createStudentWithUser(student, password);
    }

    public boolean updateStudent(Student student) {
        return studentDAO.updateStudent(student);
    }

    public boolean deleteStudent(int studentId) {
        return studentDAO.deleteStudent(studentId);
    }

    public int getTotalStudentCount() {
        return studentDAO.getTotalCount();
    }
}
