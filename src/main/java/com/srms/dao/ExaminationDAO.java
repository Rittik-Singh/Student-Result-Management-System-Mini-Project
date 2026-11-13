package com.srms.dao;

import com.srms.config.DatabaseConnection;
import com.srms.model.Examination;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExaminationDAO {

    public Examination findById(int examId) {
        String sql = "SELECT * FROM examinations WHERE exam_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapRow(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public Examination findByCode(String code) {
        String sql = "SELECT * FROM examinations WHERE LOWER(exam_code) = LOWER(?)";
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

    public List<Examination> getAllExaminations() {
        List<Examination> list = new ArrayList<>();
        String sql = "SELECT * FROM examinations ORDER BY semester_number ASC, exam_name ASC";
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

    public List<Examination> getExaminationsBySemester(int sem) {
        List<Examination> list = new ArrayList<>();
        String sql = "SELECT * FROM examinations WHERE semester_number = ? ORDER BY exam_name ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, sem);
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

    public boolean createExamination(Examination e) {
        String sql = "INSERT INTO examinations (exam_code, exam_name, academic_session, semester_number, exam_type, is_published, status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getExamCode().trim().toUpperCase());
            ps.setString(2, e.getExamName().trim());
            ps.setString(3, e.getAcademicSession().trim());
            ps.setInt(4, e.getSemesterNumber());
            ps.setString(5, e.getExamType() != null ? e.getExamType() : "REGULAR");
            ps.setBoolean(6, e.isPublished());
            ps.setString(7, e.getStatus() != null ? e.getStatus() : "SCHEDULED");
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updateExamination(Examination e) {
        String sql = "UPDATE examinations SET exam_name = ?, academic_session = ?, semester_number = ?, " +
                "exam_type = ?, is_published = ?, status = ? WHERE exam_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, e.getExamName());
            ps.setString(2, e.getAcademicSession());
            ps.setInt(3, e.getSemesterNumber());
            ps.setString(4, e.getExamType());
            ps.setBoolean(5, e.isPublished());
            ps.setString(6, e.getStatus());
            ps.setInt(7, e.getExamId());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public boolean updatePublishStatus(int examId, boolean isPublished) {
        String sql = "UPDATE examinations SET is_published = ?, published_at = ?, status = ? WHERE exam_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setBoolean(1, isPublished);
            ps.setTimestamp(2, isPublished ? new Timestamp(System.currentTimeMillis()) : null);
            ps.setString(3, isPublished ? "PUBLISHED" : "PROCESSED");
            ps.setInt(4, examId);
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            ex.printStackTrace();
            return false;
        }
    }

    public int getTotalCount() {
        String sql = "SELECT COUNT(*) FROM examinations";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int getPublishedCount() {
        String sql = "SELECT COUNT(*) FROM examinations WHERE is_published = true";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Examination mapRow(ResultSet rs) throws SQLException {
        return new Examination(
                rs.getInt("exam_id"),
                rs.getString("exam_code"),
                rs.getString("exam_name"),
                rs.getString("academic_session"),
                rs.getInt("semester_number"),
                rs.getString("exam_type"),
                rs.getBoolean("is_published"),
                rs.getTimestamp("published_at"),
                rs.getString("status"),
                rs.getTimestamp("created_at")
        );
    }
}
