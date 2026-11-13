package com.srms.dao;

import com.srms.config.DatabaseConnection;
import com.srms.model.Result;
import com.srms.model.ResultHistory;
import com.srms.model.ResultItem;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ResultDAO {

    public Result getResultByStudentAndExam(int studentId, int examId) {
        String sql = "SELECT r.*, s.full_name, s.roll_no, s.registration_no, s.college_name, s.program_name, " +
                "e.exam_name, e.academic_session " +
                "FROM results r " +
                "JOIN students s ON r.student_id = s.student_id " +
                "JOIN examinations e ON r.exam_id = e.exam_id " +
                "WHERE r.student_id = ? AND r.exam_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Result res = mapRow(rs);
                    res.setItems(loadResultItems(conn, studentId, examId));
                    return res;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Result> getAllResultsByExam(int examId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, s.full_name, s.roll_no, s.registration_no, s.college_name, s.program_name, " +
                "e.exam_name, e.academic_session " +
                "FROM results r " +
                "JOIN students s ON r.student_id = s.student_id " +
                "JOIN examinations e ON r.exam_id = e.exam_id " +
                "WHERE r.exam_id = ? ORDER BY s.roll_no ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
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

    public List<Result> getAllResultsByStudent(int studentId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, s.full_name, s.roll_no, s.registration_no, s.college_name, s.program_name, " +
                "e.exam_name, e.academic_session " +
                "FROM results r " +
                "JOIN students s ON r.student_id = s.student_id " +
                "JOIN examinations e ON r.exam_id = e.exam_id " +
                "WHERE r.student_id = ? ORDER BY r.semester_number ASC";
        try (Connection conn = DatabaseConnection.getConnection()) {
            try (PreparedStatement ps = conn.prepareStatement(sql)) {
                ps.setInt(1, studentId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Result res = mapRow(rs);
                        res.setItems(loadResultItems(conn, studentId, res.getExamId()));
                        list.add(res);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public List<Result> getToppersByExam(int examId, int limit) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, s.full_name, s.roll_no, s.registration_no, s.college_name, s.program_name, " +
                "e.exam_name, e.academic_session " +
                "FROM results r " +
                "JOIN students s ON r.student_id = s.student_id " +
                "JOIN examinations e ON r.exam_id = e.exam_id " +
                "WHERE r.exam_id = ? AND r.result_status = 'P' " +
                "ORDER BY r.sgpa DESC, r.total_marks DESC LIMIT ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
            ps.setInt(2, limit > 0 ? limit : 10);
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

    public List<Result> getBacklogStudentsByExam(int examId) {
        List<Result> list = new ArrayList<>();
        String sql = "SELECT r.*, s.full_name, s.roll_no, s.registration_no, s.college_name, s.program_name, " +
                "e.exam_name, e.academic_session " +
                "FROM results r " +
                "JOIN students s ON r.student_id = s.student_id " +
                "JOIN examinations e ON r.exam_id = e.exam_id " +
                "WHERE r.exam_id = ? AND r.backlog_count > 0 " +
                "ORDER BY r.backlog_count DESC, s.roll_no ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, examId);
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

    public boolean saveOrUpdateResult(Result res, Integer modifiedByUserId, String reason) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            String checkSql = "SELECT result_id, sgpa, result_status FROM results WHERE student_id = ? AND exam_id = ?";
            Integer existingResultId = null;
            Double oldSgpa = null;
            String oldStatus = null;

            try (PreparedStatement checkPs = conn.prepareStatement(checkSql)) {
                checkPs.setInt(1, res.getStudentId());
                checkPs.setInt(2, res.getExamId());
                try (ResultSet rs = checkPs.executeQuery()) {
                    if (rs.next()) {
                        existingResultId = rs.getInt("result_id");
                        oldSgpa = rs.getDouble("sgpa");
                        oldStatus = rs.getString("result_status");
                    }
                }
            }

            if (existingResultId == null) {
                String insSql = "INSERT INTO results (student_id, exam_id, semester_number, total_marks, max_marks, " +
                        "percentage, total_credits, total_credit_points, sgpa, ygpa, cgpa, dgpa, result_status, backlog_count, is_published) " +
                        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
                try (PreparedStatement ps = conn.prepareStatement(insSql)) {
                    ps.setInt(1, res.getStudentId());
                    ps.setInt(2, res.getExamId());
                    ps.setInt(3, res.getSemesterNumber());
                    ps.setDouble(4, res.getTotalMarks());
                    ps.setDouble(5, res.getMaxMarks());
                    ps.setDouble(6, res.getPercentage());
                    ps.setDouble(7, res.getTotalCredits());
                    ps.setDouble(8, res.getTotalCreditPoints());
                    ps.setDouble(9, res.getSgpa());
                    if (res.getYgpa() != null) ps.setDouble(10, res.getYgpa()); else ps.setNull(10, Types.DECIMAL);
                    if (res.getCgpa() != null) ps.setDouble(11, res.getCgpa()); else ps.setNull(11, Types.DECIMAL);
                    if (res.getDgpa() != null) ps.setDouble(12, res.getDgpa()); else ps.setNull(12, Types.DECIMAL);
                    ps.setString(13, res.getResultStatus());
                    ps.setInt(14, res.getBacklogCount());
                    ps.setBoolean(15, res.isPublished());
                    ps.executeUpdate();
                }
            } else {
                String updSql = "UPDATE results SET semester_number = ?, total_marks = ?, max_marks = ?, percentage = ?, " +
                        "total_credits = ?, total_credit_points = ?, sgpa = ?, ygpa = ?, cgpa = ?, dgpa = ?, result_status = ?, " +
                        "backlog_count = ?, is_published = ?, calculated_at = CURRENT_TIMESTAMP WHERE result_id = ?";
                try (PreparedStatement ps = conn.prepareStatement(updSql)) {
                    ps.setInt(1, res.getSemesterNumber());
                    ps.setDouble(2, res.getTotalMarks());
                    ps.setDouble(3, res.getMaxMarks());
                    ps.setDouble(4, res.getPercentage());
                    ps.setDouble(5, res.getTotalCredits());
                    ps.setDouble(6, res.getTotalCreditPoints());
                    ps.setDouble(7, res.getSgpa());
                    if (res.getYgpa() != null) ps.setDouble(8, res.getYgpa()); else ps.setNull(8, Types.DECIMAL);
                    if (res.getCgpa() != null) ps.setDouble(9, res.getCgpa()); else ps.setNull(9, Types.DECIMAL);
                    if (res.getDgpa() != null) ps.setDouble(10, res.getDgpa()); else ps.setNull(10, Types.DECIMAL);
                    ps.setString(11, res.getResultStatus());
                    ps.setInt(12, res.getBacklogCount());
                    ps.setBoolean(13, res.isPublished());
                    ps.setInt(14, existingResultId);
                    ps.executeUpdate();
                }

                if (reason != null && !reason.trim().isEmpty() && oldSgpa != null && Math.abs(oldSgpa - res.getSgpa()) > 0.001) {
                    String histSql = "INSERT INTO result_history (result_id, student_id, exam_id, field_changed, old_value, new_value, modified_by_user_id, reason) " +
                            "VALUES (?, ?, ?, 'RESULT_RECALCULATED', ?, ?, ?, ?)";
                    try (PreparedStatement psHist = conn.prepareStatement(histSql)) {
                        psHist.setInt(1, existingResultId);
                        psHist.setInt(2, res.getStudentId());
                        psHist.setInt(3, res.getExamId());
                        psHist.setString(4, "SGPA: " + oldSgpa + " (" + oldStatus + ")");
                        psHist.setString(5, "SGPA: " + res.getSgpa() + " (" + res.getResultStatus() + ")");
                        if (modifiedByUserId != null) psHist.setInt(6, modifiedByUserId);
                        else psHist.setNull(6, Types.INTEGER);
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

    public List<ResultHistory> getResultHistory(int examId, Integer studentId) {
        List<ResultHistory> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT h.*, s.roll_no, s.full_name, sub.subject_code, sub.subject_name, e.exam_name, u.username as mod_user " +
                "FROM result_history h " +
                "JOIN students s ON h.student_id = s.student_id " +
                "JOIN examinations e ON h.exam_id = e.exam_id " +
                "LEFT JOIN subjects sub ON h.subject_id = sub.subject_id " +
                "LEFT JOIN users u ON h.modified_by_user_id = u.user_id " +
                "WHERE h.exam_id = ? ");
        if (studentId != null && studentId > 0) {
            sql.append("AND h.student_id = ? ");
        }
        sql.append("ORDER BY h.modified_at DESC");

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql.toString())) {
            ps.setInt(1, examId);
            if (studentId != null && studentId > 0) {
                ps.setInt(2, studentId);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int rid = rs.getInt("result_id");
                    Integer resId = rs.wasNull() ? null : rid;
                    int sid = rs.getInt("subject_id");
                    Integer subId = rs.wasNull() ? null : sid;
                    int muid = rs.getInt("modified_by_user_id");
                    Integer modUserId = rs.wasNull() ? null : muid;

                    ResultHistory h = new ResultHistory(
                            rs.getInt("history_id"),
                            resId,
                            rs.getInt("student_id"),
                            subId,
                            rs.getInt("exam_id"),
                            rs.getString("field_changed"),
                            rs.getString("old_value"),
                            rs.getString("new_value"),
                            modUserId,
                            rs.getTimestamp("modified_at"),
                            rs.getString("reason")
                    );
                    h.setRollNo(rs.getString("roll_no"));
                    h.setStudentName(rs.getString("full_name"));
                    h.setSubjectCode(rs.getString("subject_code"));
                    h.setSubjectName(rs.getString("subject_name"));
                    h.setExamName(rs.getString("exam_name"));
                    h.setModifiedByUsername(rs.getString("mod_user"));
                    list.add(h);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public boolean publishExamResults(int examId, int adminUserId, String notes) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            conn.setAutoCommit(false);

            // 1. Update results is_published
            try (PreparedStatement ps1 = conn.prepareStatement("UPDATE results SET is_published = true WHERE exam_id = ?")) {
                ps1.setInt(1, examId);
                ps1.executeUpdate();
            }

            // 2. Update examination status
            try (PreparedStatement ps2 = conn.prepareStatement("UPDATE examinations SET is_published = true, published_at = CURRENT_TIMESTAMP, status = 'PUBLISHED' WHERE exam_id = ?")) {
                ps2.setInt(1, examId);
                ps2.executeUpdate();
            }

            // 3. Count stats
            int total = 0, passed = 0, backlog = 0, failed = 0;
            try (PreparedStatement ps3 = conn.prepareStatement("SELECT result_status, COUNT(*) as cnt FROM results WHERE exam_id = ? GROUP BY result_status")) {
                ps3.setInt(1, examId);
                try (ResultSet rs = ps3.executeQuery()) {
                    while (rs.next()) {
                        String st = rs.getString("result_status");
                        int c = rs.getInt("cnt");
                        total += c;
                        if ("P".equalsIgnoreCase(st)) passed += c;
                        else if ("XP".equalsIgnoreCase(st)) backlog += c;
                        else failed += c;
                    }
                }
            }

            // 4. Record publication
            String pubSql = "INSERT INTO result_publications (exam_id, published_by_user_id, total_students_processed, total_passed, total_backlog, total_failed, notes) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            try (PreparedStatement ps4 = conn.prepareStatement(pubSql)) {
                ps4.setInt(1, examId);
                ps4.setInt(2, adminUserId);
                ps4.setInt(3, total);
                ps4.setInt(4, passed);
                ps4.setInt(5, backlog);
                ps4.setInt(6, failed);
                ps4.setString(7, notes != null ? notes : "Regular publication");
                ps4.executeUpdate();
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

    private List<ResultItem> loadResultItems(Connection conn, int studentId, int examId) throws SQLException {
        List<ResultItem> items = new ArrayList<>();
        String sql = "SELECT m.*, sub.subject_code, sub.subject_name, sub.credits " +
                "FROM marks m " +
                "JOIN subjects sub ON m.subject_id = sub.subject_id " +
                "WHERE m.student_id = ? AND m.exam_id = ? " +
                "ORDER BY sub.course_type ASC, sub.subject_code ASC";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new ResultItem(
                            rs.getString("subject_code"),
                            rs.getString("subject_name"),
                            rs.getString("letter_grade"),
                            rs.getInt("grade_point"),
                            rs.getDouble("credits"),
                            rs.getDouble("credit_points"),
                            rs.getDouble("total_marks"),
                            100.0
                    ));
                }
            }
        }
        return items;
    }

    private Result mapRow(ResultSet rs) throws SQLException {
        BigDecimal ygpaVal = rs.getBigDecimal("ygpa");
        BigDecimal cgpaVal = rs.getBigDecimal("cgpa");
        BigDecimal dgpaVal = rs.getBigDecimal("dgpa");

        Result r = new Result(
                rs.getInt("result_id"),
                rs.getInt("student_id"),
                rs.getInt("exam_id"),
                rs.getInt("semester_number"),
                rs.getDouble("total_marks"),
                rs.getDouble("max_marks"),
                rs.getDouble("percentage"),
                rs.getDouble("total_credits"),
                rs.getDouble("total_credit_points"),
                rs.getDouble("sgpa"),
                ygpaVal != null ? ygpaVal.doubleValue() : null,
                cgpaVal != null ? cgpaVal.doubleValue() : null,
                dgpaVal != null ? dgpaVal.doubleValue() : null,
                rs.getString("result_status"),
                rs.getInt("backlog_count"),
                rs.getBoolean("is_published"),
                rs.getTimestamp("calculated_at")
        );
        r.setStudentName(rs.getString("full_name"));
        r.setRollNo(rs.getString("roll_no"));
        r.setRegistrationNo(rs.getString("registration_no"));
        r.setCollegeName(rs.getString("college_name"));
        r.setProgramName(rs.getString("program_name"));
        r.setExamName(rs.getString("exam_name"));
        r.setAcademicSession(rs.getString("academic_session"));
        return r;
    }
}
