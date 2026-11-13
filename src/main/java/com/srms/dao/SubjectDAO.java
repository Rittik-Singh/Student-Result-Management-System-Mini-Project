package com.srms.dao;

import com.srms.config.DatabaseConnection;
import com.srms.model.Department;
import com.srms.model.Subject;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class SubjectDAO {

    public Subject findById(int subjectId) {
        String sql = "SELECT s.*, d.dept_code, d.dept_name FROM subjects s " +
                "JOIN departments d ON s.dept_id = d.dept_id WHERE s.subject_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Subject findByCode(String code) {
        String sql = "SELECT s.*, d.dept_code, d.dept_name FROM subjects s " +
                "JOIN departments d ON s.dept_id = d.dept_id WHERE LOWER(s.subject_code) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, code.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Subject> getAllSubjects() {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT s.*, d.dept_code, d.dept_name FROM subjects s " +
                "JOIN departments d ON s.dept_id = d.dept_id ORDER BY s.semester_number ASC, s.subject_code ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Subject> getSubjectsBySemester(int semesterNumber, Integer deptId) {
        List<Subject> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT s.*, d.dept_code, d.dept_name FROM subjects s " +
                "JOIN departments d ON s.dept_id = d.dept_id WHERE s.semester_number = ? ");
        if (deptId != null && deptId > 0) {
            sql.append("AND s.dept_id = ? ");
        }
        sql.append("ORDER BY s.course_type ASC, s.subject_code ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, semesterNumber);
            if (deptId != null && deptId > 0) {
                ps.setInt(2, deptId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRow(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean createSubject(Subject s) {
        String sql = "INSERT INTO subjects (subject_code, subject_name, dept_id, semester_number, credits, " +
                "course_type, max_ca_marks, max_final_marks, max_total_marks, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectCode().trim().toUpperCase());
            ps.setString(2, s.getSubjectName().trim());
            ps.setInt(3, s.getDeptId());
            ps.setInt(4, s.getSemesterNumber());
            ps.setDouble(5, s.getCredits());
            ps.setString(6, s.getCourseType() != null ? s.getCourseType() : "THEORY");
            ps.setDouble(7, s.getMaxCaMarks());
            ps.setDouble(8, s.getMaxFinalMarks());
            ps.setDouble(9, s.getMaxTotalMarks());
            ps.setString(10, s.getStatus() != null ? s.getStatus() : "ACTIVE");
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateSubject(Subject s) {
        String sql = "UPDATE subjects SET subject_name = ?, dept_id = ?, semester_number = ?, credits = ?, " +
                "course_type = ?, max_ca_marks = ?, max_final_marks = ?, max_total_marks = ?, status = ? WHERE subject_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, s.getSubjectName().trim());
            ps.setInt(2, s.getDeptId());
            ps.setInt(3, s.getSemesterNumber());
            ps.setDouble(4, s.getCredits());
            ps.setString(5, s.getCourseType());
            ps.setDouble(6, s.getMaxCaMarks());
            ps.setDouble(7, s.getMaxFinalMarks());
            ps.setDouble(8, s.getMaxTotalMarks());
            ps.setString(9, s.getStatus());
            ps.setInt(10, s.getSubjectId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteSubject(int subjectId) {
        String sql = "DELETE FROM subjects WHERE subject_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Department> getAllDepartments() {
        List<Department> list = new ArrayList<>();
        String sql = "SELECT * FROM departments ORDER BY dept_code ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Department(
                        rs.getInt("dept_id"),
                        rs.getString("dept_code"),
                        rs.getString("dept_name")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM subjects";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Subject mapRow(ResultSet rs) throws SQLException {
        Subject s = new Subject(
                rs.getInt("subject_id"),
                rs.getString("subject_code"),
                rs.getString("subject_name"),
                rs.getInt("dept_id"),
                rs.getInt("semester_number"),
                rs.getDouble("credits"),
                rs.getString("course_type"),
                rs.getDouble("max_ca_marks"),
                rs.getDouble("max_final_marks"),
                rs.getDouble("max_total_marks"),
                rs.getString("status")
        );
        s.setDeptCode(rs.getString("dept_code"));
        s.setDeptName(rs.getString("dept_name"));
        return s;
    }
}
