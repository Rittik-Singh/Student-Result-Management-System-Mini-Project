package com.srms.dao;

import com.srms.config.DatabaseConnection;
import com.srms.model.Faculty;
import com.srms.model.Subject;
import com.srms.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDAO {

    public Faculty findById(int facultyId) {
        String sql = "SELECT f.*, d.dept_code, d.dept_name FROM faculty f " +
                     "JOIN departments d ON f.dept_id = d.dept_id WHERE f.faculty_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Faculty findByUserId(int userId) {
        String sql = "SELECT f.*, d.dept_code, d.dept_name FROM faculty f " +
                     "JOIN departments d ON f.dept_id = d.dept_id WHERE f.user_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Faculty> getAllFaculty() {
        List<Faculty> list = new ArrayList<>();
        String sql = "SELECT f.*, d.dept_code, d.dept_name FROM faculty f " +
                     "JOIN departments d ON f.dept_id = d.dept_id ORDER BY f.full_name ASC";
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

    public boolean createFacultyWithUser(Faculty faculty, String plainPassword) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String userSql = "INSERT INTO users (username, password_hash, role, email, status) VALUES (?, ?, 'FACULTY', ?, 'ACTIVE')";
            int userId;
            try (PreparedStatement psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, faculty.getFacultyCode().trim().toLowerCase());
                psUser.setString(2, PasswordUtil.hashPassword(plainPassword));
                psUser.setString(3, faculty.getEmail().trim());
                psUser.executeUpdate();
                try (ResultSet rs = psUser.getGeneratedKeys()) {
                    if (rs.next()) {
                        userId = rs.getInt(1);
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            }

            String facSql = "INSERT INTO faculty (user_id, faculty_code, full_name, email, phone, dept_id, designation, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psFac = conn.prepareStatement(facSql)) {
                psFac.setInt(1, userId);
                psFac.setString(2, faculty.getFacultyCode().trim());
                psFac.setString(3, faculty.getFullName().trim());
                psFac.setString(4, faculty.getEmail().trim());
                psFac.setString(5, faculty.getPhone());
                psFac.setInt(6, faculty.getDeptId());
                psFac.setString(7, faculty.getDesignation());
                psFac.setString(8, faculty.getStatus() != null ? faculty.getStatus() : "ACTIVE");
                psFac.executeUpdate();
            }

            conn.commit();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean updateFaculty(Faculty faculty) {
        String sql = "UPDATE faculty SET full_name = ?, email = ?, phone = ?, dept_id = ?, " +
                "designation = ?, status = ? WHERE faculty_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, faculty.getFullName());
            ps.setString(2, faculty.getEmail());
            ps.setString(3, faculty.getPhone());
            ps.setInt(4, faculty.getDeptId());
            ps.setString(5, faculty.getDesignation());
            ps.setString(6, faculty.getStatus());
            ps.setInt(7, faculty.getFacultyId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteFaculty(int facultyId) {
        Faculty fac = findById(facultyId);
        if (fac == null) return false;

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM faculty WHERE faculty_id = ?")) {
                ps.setInt(1, facultyId);
                ps.executeUpdate();
            }

            if (fac.getUserId() != null) {
                try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                    ps2.setInt(1, fac.getUserId());
                    ps2.executeUpdate();
                }
            }

            conn.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ignored) {}
            }
            return false;
        } finally {
            if (conn != null) {
                try { conn.setAutoCommit(true); conn.close(); } catch (SQLException ignored) {}
            }
        }
    }

    public boolean assignSubject(int facultyId, int subjectId, String session, int semester) {
        String sql = "INSERT INTO faculty_subjects (faculty_id, subject_id, academic_session, semester_number) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            ps.setInt(2, subjectId);
            ps.setString(3, session);
            ps.setInt(4, semester);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Subject> getAssignedSubjects(int facultyId) {
        List<Subject> list = new ArrayList<>();
        String sql = "SELECT s.*, d.dept_code, d.dept_name FROM faculty_subjects fs " +
                "JOIN subjects s ON fs.subject_id = s.subject_id " +
                "JOIN departments d ON s.dept_id = d.dept_id " +
                "WHERE fs.faculty_id = ? ORDER BY s.semester_number ASC, s.subject_code ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facultyId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Subject sub = new Subject(
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
                    sub.setDeptCode(rs.getString("dept_code"));
                    sub.setDeptName(rs.getString("dept_name"));
                    list.add(sub);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM faculty";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Faculty mapRow(ResultSet rs) throws SQLException {
        int uid = rs.getInt("user_id");
        Integer userId = rs.wasNull() ? null : uid;
        Faculty f = new Faculty(
                rs.getInt("faculty_id"),
                userId,
                rs.getString("faculty_code"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getInt("dept_id"),
                rs.getString("designation"),
                rs.getString("status")
        );
        f.setDeptCode(rs.getString("dept_code"));
        f.setDeptName(rs.getString("dept_name"));
        return f;
    }
}
