package com.srms.dao;

import com.srms.config.DatabaseConnection;
import com.srms.model.Marks;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MarksDAO {

    public List<Marks> getMarksByStudentAndExam(int studentId, int examId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, s.full_name, s.roll_no, sub.subject_code, sub.subject_name, sub.credits, sub.course_type, e.exam_name " +
                "FROM marks m " +
                "JOIN students s ON m.student_id = s.student_id " +
                "JOIN subjects sub ON m.subject_id = sub.subject_id " +
                "JOIN examinations e ON m.exam_id = e.exam_id " +
                "WHERE m.student_id = ? AND m.exam_id = ? " +
                "ORDER BY sub.course_type ASC, sub.subject_code ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
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

    public List<Marks> getMarksBySubjectAndExam(int subjectId, int examId) {
        List<Marks> list = new ArrayList<>();
        String sql = "SELECT m.*, s.full_name, s.roll_no, sub.subject_code, sub.subject_name, sub.credits, sub.course_type, e.exam_name " +
                "FROM marks m " +
                "JOIN students s ON m.student_id = s.student_id " +
                "JOIN subjects sub ON m.subject_id = sub.subject_id " +
                "JOIN examinations e ON m.exam_id = e.exam_id " +
                "WHERE m.subject_id = ? AND m.exam_id = ? " +
                "ORDER BY s.roll_no ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, subjectId);
            ps.setInt(2, examId);
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

    public boolean saveOrUpdateMarks(Marks marks, int modifiedByUserId, String reason) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // Check if existing marks exist
            String checkSql = "SELECT marks_id, total_marks, letter_grade, grade_point FROM marks WHERE student_id = ? AND subject_id = ? AND exam_id = ?";
            Integer existingMarksId = null;
            Double oldTotalMarks = null;
            String oldGrade = null;

            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, marks.getStudentId());
                checkPs.setInt(2, marks.getSubjectId());
                checkPs.setInt(3, marks.getExamId());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        existingMarksId = rs.getInt("marks_id");
                        oldTotalMarks = rs.getDouble("total_marks");
                        oldGrade = rs.getString("letter_grade");
                    }
                }
            }

            if (existingMarksId == null) {
                // Insert new marks
                String insSql = "INSERT INTO marks (student_id, subject_id, exam_id, ca_marks, final_marks, total_marks, " +
                        "letter_grade, grade_point, credit_points, is_submitted, entered_by_faculty_id) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insSql)) {
                    ps.setInt(1, marks.getStudentId());
                    ps.setInt(2, marks.getSubjectId());
                    ps.setInt(3, marks.getExamId());
                    ps.setDouble(4, marks.getCaMarks());
                    ps.setDouble(5, marks.getFinalMarks());
                    ps.setDouble(6, marks.getTotalMarks());
                    ps.setString(7, marks.getLetterGrade());
                    ps.setInt(8, marks.getGradePoint());
                    ps.setDouble(9, marks.getCreditPoints());
                    ps.setBoolean(10, marks.isSubmitted());
                    if (marks.getEnteredByFacultyId() != null) ps.setInt(11, marks.getEnteredByFacultyId());
                    else ps.setNull(11, Types.INTEGER);
                    ps.executeUpdate();
                }
            } else {
                // Update existing marks
                String updSql = "UPDATE marks SET ca_marks = ?, final_marks = ?, total_marks = ?, letter_grade = ?, " +
                        "grade_point = ?, credit_points = ?, is_submitted = ?, updated_at = CURRENT_TIMESTAMP WHERE marks_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updSql)) {
                    ps.setDouble(1, marks.getCaMarks());
                    ps.setDouble(2, marks.getFinalMarks());
                    ps.setDouble(3, marks.getTotalMarks());
                    ps.setString(4, marks.getLetterGrade());
                    ps.setInt(5, marks.getGradePoint());
                    ps.setDouble(6, marks.getCreditPoints());
                    ps.setBoolean(7, marks.isSubmitted());
                    ps.setInt(8, existingMarksId);
                    ps.executeUpdate();
                }

                // If modified and reason provided, record audit history
                if (reason != null && !reason.trim().isEmpty() && oldTotalMarks != null && Math.abs(oldTotalMarks - marks.getTotalMarks()) > 0.001) {
                    String histSql = "INSERT INTO result_history (student_id, subject_id, exam_id, field_changed, old_value, new_value, modified_by_user_id, reason) " +
                            "VALUES (?, ?, ?, 'MARKS', ?, ?, ?, ?)";
                    try (PreparedStatement psHist = conn.prepareStatement(histSql)) {
                        psHist.setInt(1, marks.getStudentId());
                        psHist.setInt(2, marks.getSubjectId());
                        psHist.setInt(3, marks.getExamId());
                        psHist.setString(4, "Total: " + oldTotalMarks + " (" + oldGrade + ")");
                        psHist.setString(5, "Total: " + marks.getTotalMarks() + " (" + marks.getLetterGrade() + ")");
                        psHist.setInt(6, modifiedByUserId);
                        psHist.setString(7, reason.trim());
                        psHist.executeUpdate();
                    }
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

    public int getPendingMarksCount() {
        String sql = "SELECT COUNT(*) FROM marks WHERE is_submitted = false";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) return rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    private Marks mapRow(ResultSet rs) throws SQLException {
        int fid = rs.getInt("entered_by_faculty_id");
        Integer facultyId = rs.wasNull() ? null : fid;
        Marks m = new Marks(
                rs.getInt("marks_id"),
                rs.getInt("student_id"),
                rs.getInt("subject_id"),
                rs.getInt("exam_id"),
                rs.getDouble("ca_marks"),
                rs.getDouble("final_marks"),
                rs.getDouble("total_marks"),
                rs.getString("letter_grade"),
                rs.getInt("grade_point"),
                rs.getDouble("credit_points"),
                rs.getBoolean("is_submitted"),
                facultyId,
                rs.getTimestamp("updated_at")
        );
        m.setStudentName(rs.getString("full_name"));
        m.setRollNo(rs.getString("roll_no"));
        m.setSubjectCode(rs.getString("subject_code"));
        m.setSubjectName(rs.getString("subject_name"));
        m.setCredits(rs.getDouble("credits"));
        m.setCourseType(rs.getString("course_type"));
        m.setExamName(rs.getString("exam_name"));
        return m;
    }
}
