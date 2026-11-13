package com.srms.dao;

import com.srms.config.DatabaseConnection;
import com.srms.util.GradingUtil;
import com.srms.util.PasswordUtil;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class DatabaseInitializer {

    public static synchronized void initializeDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            boolean tablesExist = checkTableExists(conn);
            if (!tablesExist) {
                System.out.println("[DatabaseInitializer] Initializing schema and seed data...");
                executeSqlScript(conn, "database/schema.sql");
                executeSqlScript(conn, "database/sample_data.sql");
                seedUsersAndAcademicData(conn);
                System.out.println("[DatabaseInitializer] Database initialized successfully.");
            } else {
                System.out.println("[DatabaseInitializer] Database tables already present.");
            }
        } catch (Exception e) {
            System.err.println("[DatabaseInitializer] Error initializing database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static boolean checkTableExists(Connection conn) {
        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 FROM students LIMIT 1")) {
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private static void executeSqlScript(Connection conn, String relativePath) throws Exception {
        String content = "";
        if (Files.exists(Paths.get(relativePath))) {
            content = Files.readString(Paths.get(relativePath));
        } else {
            InputStream is = DatabaseInitializer.class.getClassLoader().getResourceAsStream(relativePath);
            if (is != null) {
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
                    StringBuilder sb = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        sb.append(line).append("\n");
                    }
                    content = sb.toString();
                }
            }
        }

        if (content.isEmpty()) return;

        // Strip comments and split by semicolon
        String[] statements = content.split(";");
        try (Statement stmt = conn.createStatement()) {
            for (String sql : statements) {
                String trimmed = sql.trim();
                if (!trimmed.isEmpty()) {
                    try {
                        stmt.execute(trimmed);
                    } catch (Exception e) {
                        // Ignore duplicate key or minor syntax differences in fallback
                        System.err.println("Warning running statement: " + e.getMessage());
                    }
                }
            }
        }
    }

    private static void seedUsersAndAcademicData(Connection conn) throws Exception {
        // 1. Create Admin User
        int adminUserId = insertUser(conn, "admin", "Admin@123", "ADMIN", "admin@makaut.ac.in", "ACTIVE");

        // 2. Create Faculty Users & Profiles
        int fac1UserId = insertUser(conn, "dr.sharma", "Faculty@123", "FACULTY", "amit.sharma@bbit.edu.in", "ACTIVE");
        int fac1Id = insertFaculty(conn, fac1UserId, "FAC-CSE-01", "Dr. Amit Sharma", "amit.sharma@bbit.edu.in", "+91 9830112233", 1, "Professor & HoD");

        int fac2UserId = insertUser(conn, "prof.das", "Faculty@123", "FACULTY", "sourav.das@bbit.edu.in", "ACTIVE");
        int fac2Id = insertFaculty(conn, fac2UserId, "FAC-CSE-02", "Prof. Sourav Das", "sourav.das@bbit.edu.in", "+91 9830223344", 1, "Assistant Professor");

        int fac3UserId = insertUser(conn, "prof.banerjee", "Faculty@123", "FACULTY", "priya.banerjee@bbit.edu.in", "ACTIVE");
        int fac3Id = insertFaculty(conn, fac3UserId, "FAC-CSE-03", "Prof. Priya Banerjee", "priya.banerjee@bbit.edu.in", "+91 9830334455", 1, "Associate Professor");

        // 3. Create Student Users & Profiles
        int s1UserId = insertUser(conn, "27600124105", "Student@123", "STUDENT", "rittik.singh@bbit.edu.in", "ACTIVE");
        int s1Id = insertStudent(conn, s1UserId, "27600124105", "242760110182 OF 2024-25", "RITTIK SINGH",
                "rittik.singh@bbit.edu.in", "+91 9876543210", 1, 4,
                "276-BUDGE BUDGE INSTITUTE OF TECHNOLOGY", "BACHELOR OF TECHNOLOGY IN COMPUTER SCIENCE & ENGINEERING", 2024);

        int s2UserId = insertUser(conn, "27600124106", "Student@123", "STUDENT", "ananya.m@bbit.edu.in", "ACTIVE");
        int s2Id = insertStudent(conn, s2UserId, "27600124106", "242760110183 OF 2024-25", "ANANYA MUKHERJEE",
                "ananya.m@bbit.edu.in", "+91 9876543211", 1, 4,
                "276-BUDGE BUDGE INSTITUTE OF TECHNOLOGY", "BACHELOR OF TECHNOLOGY IN COMPUTER SCIENCE & ENGINEERING", 2024);

        int s3UserId = insertUser(conn, "27600124107", "Student@123", "STUDENT", "debanjan.r@bbit.edu.in", "ACTIVE");
        int s3Id = insertStudent(conn, s3UserId, "27600124107", "242760110184 OF 2024-25", "DEBANJAN ROY",
                "debanjan.r@bbit.edu.in", "+91 9876543212", 1, 4,
                "276-BUDGE BUDGE INSTITUTE OF TECHNOLOGY", "BACHELOR OF TECHNOLOGY IN COMPUTER SCIENCE & ENGINEERING", 2024);

        int s4UserId = insertUser(conn, "27600124108", "Student@123", "STUDENT", "sneha.g@bbit.edu.in", "ACTIVE");
        int s4Id = insertStudent(conn, s4UserId, "27600124108", "242760110185 OF 2024-25", "SNEHA GHOSH",
                "sneha.g@bbit.edu.in", "+91 9876543213", 1, 4,
                "276-BUDGE BUDGE INSTITUTE OF TECHNOLOGY", "BACHELOR OF TECHNOLOGY IN COMPUTER SCIENCE & ENGINEERING", 2024);

        // 4. Create Examinations (Semester 1 to 4)
        int exam1Id = insertExam(conn, "EXAM-2024-SEM1", "FIRST YEAR FIRST SEMESTER EXAMINATION OF 2024-25", "2024-25", 1, true, "PUBLISHED");
        int exam2Id = insertExam(conn, "EXAM-2024-SEM2", "FIRST YEAR SECOND SEMESTER EXAMINATION OF 2024-25", "2024-25", 2, true, "PUBLISHED");
        int exam3Id = insertExam(conn, "EXAM-2025-SEM3", "SECOND YEAR FIRST SEMESTER EXAMINATION OF 2025-26", "2025-26", 3, true, "PUBLISHED");
        int exam4Id = insertExam(conn, "EXAM-2025-SEM4", "SECOND YEAR SECOND SEMESTER EXAMINATION OF 2025-26", "2025-26", 4, true, "PUBLISHED");

        // 5. Assign subjects to faculty
        assignFacultySubject(conn, fac1Id, "PCC-CS401", "2025-26", 4);
        assignFacultySubject(conn, fac2Id, "PCC-CS402", "2025-26", 4);
        assignFacultySubject(conn, fac3Id, "PCC-CS403", "2025-26", 4);
        assignFacultySubject(conn, fac1Id, "PCC-CS404", "2025-26", 4);
        assignFacultySubject(conn, fac2Id, "PCC-CS492", "2025-26", 4);
        assignFacultySubject(conn, fac1Id, "PCC-CS494", "2025-26", 4);

        // 6. Seed Rittik Singh's 4 Semesters of Results from the authentic MAKAUT Grade Cards
        // Semester 1: SGPA 7.11 (Total Credits: 17.5, Total Credit Points: 124.5)
        seedMarks(conn, s1Id, exam1Id, "BSPH101", 18.0, 45.0, "B", 7, 4.0, 28.0, fac1Id);
        seedMarks(conn, s1Id, exam1Id, "BSM101", 15.0, 40.0, "C", 6, 4.0, 24.0, fac2Id);
        seedMarks(conn, s1Id, exam1Id, "ESEE101", 12.0, 35.0, "D", 5, 4.0, 20.0, fac3Id);
        seedMarks(conn, s1Id, exam1Id, "BSPH191", 35.0, 48.0, "E", 9, 1.5, 13.5, fac1Id);
        seedMarks(conn, s1Id, exam1Id, "ESEE191", 34.0, 49.0, "E", 9, 1.0, 9.0, fac3Id);
        seedMarks(conn, s1Id, exam1Id, "ESME192", 38.0, 55.0, "O", 10, 3.0, 30.0, fac2Id);
        insertResult(conn, s1Id, exam1Id, 1, 381.0, 600.0, 63.5, 17.5, 124.5, 7.11, null, 7.11, null, "P", 0, true);

        // Semester 2: SGPA 7.80 (Total Credits: 20.5, Total Credit Points: 160.0)
        seedMarks(conn, s1Id, exam2Id, "BSCH201", 18.0, 44.0, "B", 7, 4.0, 28.0, fac1Id);
        seedMarks(conn, s1Id, exam2Id, "BSM201", 19.0, 45.0, "B", 7, 4.0, 28.0, fac2Id);
        seedMarks(conn, s1Id, exam2Id, "ESCS201", 16.0, 38.0, "C", 6, 3.0, 18.0, fac3Id);
        seedMarks(conn, s1Id, exam2Id, "HMHU201", 17.0, 47.0, "B", 7, 2.0, 14.0, fac1Id);
        seedMarks(conn, s1Id, exam2Id, "BSCH291", 38.0, 54.0, "O", 10, 1.5, 15.0, fac2Id);
        seedMarks(conn, s1Id, exam2Id, "ESCS291", 39.0, 55.0, "O", 10, 2.0, 20.0, fac3Id);
        seedMarks(conn, s1Id, exam2Id, "ESME291", 36.0, 49.0, "E", 9, 3.0, 27.0, fac1Id);
        seedMarks(conn, s1Id, exam2Id, "HMHU291", 38.0, 54.0, "O", 10, 1.0, 10.0, fac2Id);
        double ygpa1 = (124.5 + 160.0) / (17.5 + 20.5); // 284.5 / 38 = 7.49
        insertResult(conn, s1Id, exam2Id, 2, 542.0, 800.0, 67.75, 20.5, 160.0, 7.80, Math.round(ygpa1 * 100.0) / 100.0, 7.49, null, "P", 0, true);

        // Semester 3: SGPA 8.14 (Total Credits: 22.0, Total Credit Points: 179.0)
        seedMarks(conn, s1Id, exam3Id, "ESC301", 18.0, 46.0, "B", 7, 3.0, 21.0, fac1Id);
        seedMarks(conn, s1Id, exam3Id, "PCC-CS301", 17.0, 45.0, "B", 7, 3.0, 21.0, fac2Id);
        seedMarks(conn, s1Id, exam3Id, "PCC-CS302", 19.0, 48.0, "B", 7, 3.0, 21.0, fac3Id);
        seedMarks(conn, s1Id, exam3Id, "BSC301", 18.0, 45.0, "B", 7, 2.0, 14.0, fac1Id);
        seedMarks(conn, s1Id, exam3Id, "HSMC301", 21.0, 54.0, "A", 8, 3.0, 24.0, fac2Id);
        seedMarks(conn, s1Id, exam3Id, "PCC-CS393", 38.0, 54.0, "O", 10, 2.0, 20.0, fac3Id);
        seedMarks(conn, s1Id, exam3Id, "ESC391", 37.0, 55.0, "O", 10, 2.0, 20.0, fac1Id);
        seedMarks(conn, s1Id, exam3Id, "PCC-CS391", 38.0, 55.0, "O", 10, 2.0, 20.0, fac2Id);
        seedMarks(conn, s1Id, exam3Id, "PCC-CS392", 36.0, 48.0, "E", 9, 2.0, 18.0, fac3Id);
        insertResult(conn, s1Id, exam3Id, 3, 672.0, 900.0, 74.67, 22.0, 179.0, 8.14, null, 7.73, null, "P", 0, true);

        // Semester 4: SGPA 8.48 (Total Credits: 21.0, Total Credit Points: 178.0)
        seedMarks(conn, s1Id, exam4Id, "PCC-CS401", 19.0, 46.0, "B", 7, 4.0, 28.0, fac1Id);
        seedMarks(conn, s1Id, exam4Id, "PCC-CS402", 22.0, 60.0, "E", 9, 3.0, 27.0, fac2Id);
        seedMarks(conn, s1Id, exam4Id, "PCC-CS403", 21.0, 62.0, "E", 9, 3.0, 27.0, fac3Id);
        seedMarks(conn, s1Id, exam4Id, "PCC-CS404", 23.0, 61.0, "E", 9, 3.0, 27.0, fac1Id);
        seedMarks(conn, s1Id, exam4Id, "BSC401", 18.0, 45.0, "B", 7, 3.0, 21.0, fac2Id);
        seedMarks(conn, s1Id, exam4Id, "MC401", 20.0, 52.0, "A", 8, 1.0, 8.0, fac3Id);
        seedMarks(conn, s1Id, exam4Id, "PCC-CS492", 38.0, 56.0, "O", 10, 2.0, 20.0, fac2Id);
        seedMarks(conn, s1Id, exam4Id, "PCC-CS494", 39.0, 55.0, "O", 10, 2.0, 20.0, fac1Id);
        double ygpa2 = (179.0 + 178.0) / (22.0 + 21.0); // 357 / 43 = 8.30
        double cumulativeCp = 124.5 + 160.0 + 179.0 + 178.0; // 641.5
        double cumulativeCr = 17.5 + 20.5 + 22.0 + 21.0;   // 81.0
        double cgpa4 = cumulativeCp / cumulativeCr;        // 7.92
        insertResult(conn, s1Id, exam4Id, 4, 629.0, 800.0, 78.63, 21.0, 178.0, 8.48, Math.round(ygpa2 * 100.0) / 100.0, Math.round(cgpa4 * 100.0) / 100.0, null, "P", 0, true);

        // 7. Seed Marks for Ananya Mukherjee (Semester 4 Topper: SGPA 9.24)
        seedMarks(conn, s2Id, exam4Id, "PCC-CS401", 24.0, 68.0, "O", 10, 4.0, 40.0, fac1Id);
        seedMarks(conn, s2Id, exam4Id, "PCC-CS402", 23.0, 62.0, "E", 9, 3.0, 27.0, fac2Id);
        seedMarks(conn, s2Id, exam4Id, "PCC-CS403", 24.0, 67.0, "O", 10, 3.0, 30.0, fac3Id);
        seedMarks(conn, s2Id, exam4Id, "PCC-CS404", 23.0, 61.0, "E", 9, 3.0, 27.0, fac1Id);
        seedMarks(conn, s2Id, exam4Id, "BSC401", 21.0, 62.0, "E", 9, 3.0, 27.0, fac2Id);
        seedMarks(conn, s2Id, exam4Id, "MC401", 22.0, 60.0, "E", 9, 1.0, 9.0, fac3Id);
        seedMarks(conn, s2Id, exam4Id, "PCC-CS492", 39.0, 56.0, "O", 10, 2.0, 20.0, fac2Id);
        seedMarks(conn, s2Id, exam4Id, "PCC-CS494", 40.0, 56.0, "O", 10, 2.0, 20.0, fac1Id);
        insertResult(conn, s2Id, exam4Id, 4, 707.0, 800.0, 88.38, 21.0, 200.0, 9.52, 9.35, 9.24, null, "P", 0, true);

        // 8. Seed Marks for Debanjan Roy (Semester 4 SGPA 7.52)
        seedMarks(conn, s3Id, exam4Id, "PCC-CS401", 17.0, 43.0, "B", 7, 4.0, 28.0, fac1Id);
        seedMarks(conn, s3Id, exam4Id, "PCC-CS402", 18.0, 44.0, "B", 7, 3.0, 21.0, fac2Id);
        seedMarks(conn, s3Id, exam4Id, "PCC-CS403", 19.0, 45.0, "B", 7, 3.0, 21.0, fac3Id);
        seedMarks(conn, s3Id, exam4Id, "PCC-CS404", 17.0, 43.0, "B", 7, 3.0, 21.0, fac1Id);
        seedMarks(conn, s3Id, exam4Id, "BSC401", 18.0, 44.0, "B", 7, 3.0, 21.0, fac2Id);
        seedMarks(conn, s3Id, exam4Id, "MC401", 18.0, 43.0, "B", 7, 1.0, 7.0, fac3Id);
        seedMarks(conn, s3Id, exam4Id, "PCC-CS492", 37.0, 53.0, "O", 10, 2.0, 20.0, fac2Id);
        seedMarks(conn, s3Id, exam4Id, "PCC-CS494", 36.0, 53.0, "E", 9, 2.0, 18.0, fac1Id);
        insertResult(conn, s3Id, exam4Id, 4, 578.0, 800.0, 72.25, 21.0, 157.0, 7.48, 7.30, 7.35, null, "P", 0, true);

        // 9. Seed Marks for Sneha Ghosh (Semester 4 with Backlog in PCC-CS403: SGPA 6.24, Status XP)
        seedMarks(conn, s4Id, exam4Id, "PCC-CS401", 18.0, 44.0, "B", 7, 4.0, 28.0, fac1Id);
        seedMarks(conn, s4Id, exam4Id, "PCC-CS402", 17.0, 43.0, "B", 7, 3.0, 21.0, fac2Id);
        seedMarks(conn, s4Id, exam4Id, "PCC-CS403", 10.0, 22.0, "F", 2, 3.0, 6.0, fac3Id); // Failed / Backlog
        seedMarks(conn, s4Id, exam4Id, "PCC-CS404", 18.0, 44.0, "B", 7, 3.0, 21.0, fac1Id);
        seedMarks(conn, s4Id, exam4Id, "BSC401", 17.0, 43.0, "B", 7, 3.0, 21.0, fac2Id);
        seedMarks(conn, s4Id, exam4Id, "MC401", 18.0, 43.0, "B", 7, 1.0, 7.0, fac3Id);
        seedMarks(conn, s4Id, exam4Id, "PCC-CS492", 36.0, 52.0, "E", 9, 2.0, 18.0, fac2Id);
        seedMarks(conn, s4Id, exam4Id, "PCC-CS494", 37.0, 53.0, "O", 10, 2.0, 20.0, fac1Id);
        insertResult(conn, s4Id, exam4Id, 4, 513.0, 800.0, 64.13, 21.0, 142.0, 6.76, 6.50, 6.62, null, "XP", 1, true);
    }

    private static int insertUser(Connection conn, String username, String plainPass, String role, String email, String status) throws Exception {
        String hash = PasswordUtil.hashPassword(plainPass);
        String sql = "INSERT INTO users (username, password_hash, role, email, status) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, hash);
            ps.setString(3, role);
            ps.setString(4, email);
            ps.setString(5, status);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private static int insertFaculty(Connection conn, int userId, String code, String name, String email, String phone, int deptId, String designation) throws Exception {
        String sql = "INSERT INTO faculty (user_id, faculty_code, full_name, email, phone, dept_id, designation, status) VALUES (?, ?, ?, ?, ?, ?, ?, 'ACTIVE')";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, code);
            ps.setString(3, name);
            ps.setString(4, email);
            ps.setString(5, phone);
            ps.setInt(6, deptId);
            ps.setString(7, designation);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private static int insertStudent(Connection conn, int userId, String roll, String reg, String name, String email, String phone,
                                     int deptId, int sem, String college, String program, int admYear) throws Exception {
        String sql = "INSERT INTO students (user_id, roll_no, registration_no, full_name, email, phone, dept_id, current_semester, college_name, program_name, admission_year) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, userId);
            ps.setString(2, roll);
            ps.setString(3, reg);
            ps.setString(4, name);
            ps.setString(5, email);
            ps.setString(6, phone);
            ps.setInt(7, deptId);
            ps.setInt(8, sem);
            ps.setString(9, college);
            ps.setString(10, program);
            ps.setInt(11, admYear);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private static int insertExam(Connection conn, String code, String name, String session, int sem, boolean published, String status) throws Exception {
        String sql = "INSERT INTO examinations (exam_code, exam_name, academic_session, semester_number, is_published, status) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, code);
            ps.setString(2, name);
            ps.setString(3, session);
            ps.setInt(4, sem);
            ps.setBoolean(5, published);
            ps.setString(6, status);
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    private static void assignFacultySubject(Connection conn, int facId, String subCode, String session, int sem) {
        String sql = "INSERT INTO faculty_subjects (faculty_id, subject_id, academic_session, semester_number) " +
                "SELECT ?, subject_id, ?, ? FROM subjects WHERE subject_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, facId);
            ps.setString(2, session);
            ps.setInt(3, sem);
            ps.setString(4, subCode);
            ps.executeUpdate();
        } catch (Exception ignored) {}
    }

    private static void seedMarks(Connection conn, int studentId, int examId, String subCode,
                                  double ca, double finalM, String grade, int gp, double credits, double cp, int facId) throws Exception {
        double total = ca + finalM;
        String sql = "INSERT INTO marks (student_id, subject_id, exam_id, ca_marks, final_marks, total_marks, letter_grade, grade_point, credit_points, is_submitted, entered_by_faculty_id) " +
                "SELECT ?, subject_id, ?, ?, ?, ?, ?, ?, ?, true, ? FROM subjects WHERE subject_code = ?";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setDouble(3, ca);
            ps.setDouble(4, finalM);
            ps.setDouble(5, total);
            ps.setString(6, grade);
            ps.setInt(7, gp);
            ps.setDouble(8, cp);
            ps.setInt(9, facId);
            ps.setString(10, subCode);
            ps.executeUpdate();
        }
    }

    private static void insertResult(Connection conn, int studentId, int examId, int sem,
                                     double totalM, double maxM, double pct,
                                     double totalCr, double totalCp, double sgpa,
                                     Double ygpa, Double cgpa, Double dgpa,
                                     String status, int backlogs, boolean published) throws Exception {
        String sql = "INSERT INTO results (student_id, exam_id, semester_number, total_marks, max_marks, percentage, " +
                "total_credits, total_credit_points, sgpa, ygpa, cgpa, dgpa, result_status, backlog_count, is_published) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, studentId);
            ps.setInt(2, examId);
            ps.setInt(3, sem);
            ps.setDouble(4, totalM);
            ps.setDouble(5, maxM);
            ps.setDouble(6, pct);
            ps.setDouble(7, totalCr);
            ps.setDouble(8, totalCp);
            ps.setDouble(9, sgpa);
            if (ygpa != null) ps.setDouble(10, ygpa); else ps.setNull(10, java.sql.Types.DECIMAL);
            if (cgpa != null) ps.setDouble(11, cgpa); else ps.setNull(11, java.sql.Types.DECIMAL);
            if (dgpa != null) ps.setDouble(12, dgpa); else ps.setNull(12, java.sql.Types.DECIMAL);
            ps.setString(13, status);
            ps.setInt(14, backlogs);
            ps.setBoolean(15, published);
            ps.executeUpdate();
        }
    }
}
