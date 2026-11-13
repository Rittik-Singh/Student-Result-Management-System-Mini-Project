package com.srms.dao;

import com.srms.config.DatabaseConnection;
import com.srms.model.Student;
import com.srms.model.User;
import com.srms.util.PasswordUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StudentDAO {

    public Student findById(int studentId) {
        String sql = "SELECT s.*, d.dept_code, d.dept_name FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id WHERE s.student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student findByRollNo(String rollNo) {
        String sql = "SELECT s.*, d.dept_code, d.dept_name FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id WHERE LOWER(s.roll_no) = LOWER(?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, rollNo.trim());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Student findByUserId(int userId) {
        String sql = "SELECT s.*, d.dept_code, d.dept_name FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id WHERE s.user_id = ?";
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

    public List<Student> getAllStudents() {
        List<Student> list = new ArrayList<>();
        String sql = "SELECT s.*, d.dept_code, d.dept_name FROM students s " +
                     "JOIN departments d ON s.dept_id = d.dept_id ORDER BY s.roll_no ASC";
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

    public List<Student> searchStudents(String keyword, Integer deptId, Integer semester) {
        List<Student> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT s.*, d.dept_code, d.dept_name FROM students s " +
                "JOIN departments d ON s.dept_id = d.dept_id WHERE 1=1 ");

        if (keyword != null && !keyword.trim().isEmpty()) {
            sql.append("AND (LOWER(s.roll_no) LIKE LOWER(?) OR LOWER(s.full_name) LIKE LOWER(?) OR LOWER(s.registration_no) LIKE LOWER(?)) ");
        }
        if (deptId != null && deptId > 0) {
            sql.append("AND s.dept_id = ? ");
        }
        if (semester != null && semester > 0) {
            sql.append("AND s.current_semester = ? ");
        }
        sql.append("ORDER BY s.roll_no ASC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            int paramIndex = 1;
            if (keyword != null && !keyword.trim().isEmpty()) {
                String searchStr = "%" + keyword.trim() + "%";
                ps.setString(paramIndex++, searchStr);
                ps.setString(paramIndex++, searchStr);
                ps.setString(paramIndex++, searchStr);
            }
            if (deptId != null && deptId > 0) {
                ps.setInt(paramIndex++, deptId);
            }
            if (semester != null && semester > 0) {
                ps.setInt(paramIndex++, semester);
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

    public boolean createStudentWithUser(Student student, String plainPassword) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Create User
            String userSql = "INSERT INTO users (username, password_hash, role, email, status) VALUES (?, ?, 'STUDENT', ?, 'ACTIVE')";
            int userId;
            try (PreparedStatement psUser = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, student.getRollNo().trim());
                psUser.setString(2, PasswordUtil.hashPassword(plainPassword));
                psUser.setString(3, student.getEmail().trim());
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

            // 2. Create Student Record
            String stuSql = "INSERT INTO students (user_id, roll_no, registration_no, full_name, email, phone, dept_id, current_semester, college_name, program_name, admission_year) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement psStu = conn.prepareStatement(stuSql)) {
                psStu.setInt(1, userId);
                psStu.setString(2, student.getRollNo().trim());
                psStu.setString(3, student.getRegistrationNo().trim());
                psStu.setString(4, student.getFullName().trim());
                psStu.setString(5, student.getEmail().trim());
                psStu.setString(6, student.getPhone());
                psStu.setInt(7, student.getDeptId());
                psStu.setInt(8, student.getCurrentSemester());
                psStu.setString(9, student.getCollegeName() != null ? student.getCollegeName() : "276-BUDGE BUDGE INSTITUTE OF TECHNOLOGY");
                psStu.setString(10, student.getProgramName() != null ? student.getProgramName() : "BACHELOR OF TECHNOLOGY IN COMPUTER SCIENCE & ENGINEERING");
                psStu.setInt(11, student.getAdmissionYear() > 0 ? student.getAdmissionYear() : 2024);
                psStu.executeUpdate();
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

    public boolean updateStudent(Student student) {
        String sql = "UPDATE students SET registration_no = ?, full_name = ?, email = ?, phone = ?, " +
                "dept_id = ?, current_semester = ?, college_name = ?, program_name = ? WHERE student_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, student.getRegistrationNo());
            ps.setString(2, student.getFullName());
            ps.setString(3, student.getEmail());
            ps.setString(4, student.getPhone());
            ps.setInt(5, student.getDeptId());
            ps.setInt(6, student.getCurrentSemester());
            ps.setString(7, student.getCollegeName());
            ps.setString(8, student.getProgramName());
            ps.setInt(9, student.getStudentId());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteStudent(int studentId) {
        Student stu = findById(studentId);
        if (stu == null) return false;

        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            try (PreparedStatement ps = conn.prepareStatement("DELETE FROM students WHERE student_id = ?")) {
                ps.setInt(1, studentId);
                ps.executeUpdate();
            }

            if (stu.getUserId() != null) {
                try (PreparedStatement ps2 = conn.prepareStatement("DELETE FROM users WHERE user_id = ?")) {
                    ps2.setInt(1, stu.getUserId());
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

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM students";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Student mapRow(ResultSet rs) throws SQLException {
        int uid = rs.getInt("user_id");
        Integer userId = rs.wasNull() ? null : uid;
        Student s = new Student(
                rs.getInt("student_id"),
                userId,
                rs.getString("roll_no"),
                rs.getString("registration_no"),
                rs.getString("full_name"),
                rs.getString("email"),
                rs.getString("phone"),
                rs.getInt("dept_id"),
                rs.getInt("current_semester"),
                rs.getString("college_name"),
                rs.getString("program_name"),
                rs.getInt("admission_year"),
                rs.getTimestamp("created_at")
        );
        s.setDeptCode(rs.getString("dept_code"));
        s.setDeptName(rs.getString("dept_name"));
        return s;
    }
}
