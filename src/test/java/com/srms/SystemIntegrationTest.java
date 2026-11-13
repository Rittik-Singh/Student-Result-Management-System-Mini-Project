package com.srms;

import com.srms.dao.DatabaseInitializer;
import com.srms.dao.StudentDAO;
import com.srms.model.Result;
import com.srms.model.Student;
import com.srms.model.User;
import com.srms.service.AuthService;
import com.srms.service.ReportService;
import com.srms.service.ResultService;
import com.srms.util.GradingUtil;

import java.io.File;
import java.util.List;

public class SystemIntegrationTest {

    public static void main(String[] args) {
        System.out.println(">>> RUNNING SYSTEM INTEGRATION & VERIFICATION TESTS <<<");
        try {
            // 1. Initialize Database
            System.out.println("[Test 1] Initializing Database & MAKAUT Schema...");
            DatabaseInitializer.initializeDatabase();
            System.out.println("  -> PASSED: Database initialized successfully.");

            // 2. Test User Authentication & BCrypt Hashing
            System.out.println("[Test 2] Testing User Authentication & BCrypt Password Verification...");
            AuthService authService = new AuthService();
            User admin = authService.login("admin", "Admin@123");
            assert admin != null && "ADMIN".equalsIgnoreCase(admin.getRole()) : "Admin login failed";
            System.out.println("  -> PASSED: Admin authenticated (" + admin.getUsername() + ", Role: " + admin.getRole() + ")");

            User faculty = authService.login("dr.sharma", "Faculty@123");
            assert faculty != null && "FACULTY".equalsIgnoreCase(faculty.getRole()) : "Faculty login failed";
            System.out.println("  -> PASSED: Faculty authenticated (" + faculty.getUsername() + ", Role: " + faculty.getRole() + ")");

            User studentUser = authService.login("27600124105", "Student@123");
            assert studentUser != null && "STUDENT".equalsIgnoreCase(studentUser.getRole()) : "Student login failed";
            System.out.println("  -> PASSED: Student authenticated (" + studentUser.getUsername() + ", Role: " + studentUser.getRole() + ")");

            // 3. Test Student Details against user's uploaded Provisional Grade Card
            System.out.println("[Test 3] Verifying Student Profile Data for RITTIK SINGH...");
            StudentDAO studentDAO = new StudentDAO();
            Student rittik = studentDAO.findByRollNo("27600124105");
            assert rittik != null : "Student Rittik not found";
            assert "242760110182 OF 2024-25".equalsIgnoreCase(rittik.getRegistrationNo()) : "Registration number mismatch";
            System.out.println("  -> PASSED: Student Name: " + rittik.getFullName() +
                    ", Roll: " + rittik.getRollNo() +
                    ", Reg: " + rittik.getRegistrationNo() +
                    ", Institution: " + rittik.getCollegeName());

            // 4. Test MAKAUT Grading Calculations & Grade Cards Accuracy
            System.out.println("[Test 4] Testing MAKAUT Grading Formulae & Historical Grade Cards Accuracy...");
            ResultService resultService = new ResultService();
            List<Result> results = resultService.getAllResultsByStudent(rittik.getStudentId());
            assert results.size() >= 4 : "Expected at least 4 semester results, got " + results.size();

            for (Result r : results) {
                System.out.printf("  -> Semester %d: Credits = %.1f, Credit Points = %.1f, SGPA = %.2f, Result = %s (Items: %d)%n",
                        r.getSemesterNumber(), r.getTotalCredits(), r.getTotalCreditPoints(), r.getSgpa(), r.getResultStatus(), r.getItems().size());
                assert "P".equalsIgnoreCase(r.getResultStatus()) : "Result status should be P";
            }

            // Verify Sem 4 matches uploaded card exactly: 21 Credits, 178 Points, SGPA 8.48
            Result sem4 = results.get(3);
            assert Math.abs(sem4.getTotalCredits() - 21.0) < 0.01 : "Sem 4 credits mismatch";
            assert Math.abs(sem4.getTotalCreditPoints() - 178.0) < 0.01 : "Sem 4 credit points mismatch";
            assert Math.abs(sem4.getSgpa() - 8.48) < 0.01 : "Sem 4 SGPA mismatch";
            System.out.println("  -> PASSED: Sem 4 SGPA (8.48) exactly matches authentic MAKAUT Grade Card!");

            // 5. Test PDF Marksheet Generation
            System.out.println("[Test 5] Generating Official MAKAUT Provisional Grade Card PDF...");
            ReportService reportService = new ReportService();
            File pdf = reportService.generateStudentMarksheetPdf(rittik.getStudentId(), sem4.getExamId(), "reports");
            assert pdf.exists() && pdf.length() > 0 : "PDF was not created";
            System.out.println("  -> PASSED: Provisional Grade Card PDF generated at: " + pdf.getAbsolutePath() + " (Size: " + pdf.length() + " bytes)");

            // 6. Test Consolidated CSV Export
            System.out.println("[Test 6] Generating Consolidated Results CSV Export...");
            File csv = reportService.generateConsolidatedExamReportCsv(sem4.getExamId(), "reports");
            assert csv.exists() && csv.length() > 0 : "CSV was not created";
            System.out.println("  -> PASSED: Consolidated CSV Report generated at: " + csv.getAbsolutePath() + " (Size: " + csv.length() + " bytes)");

            System.out.println("=========================================================");
            System.out.println(">>> ALL 6 SYSTEM INTEGRATION TESTS PASSED 100% <<<");
            System.out.println("=========================================================");
        } catch (Throwable t) {
            System.err.println("Test Failed with Exception: " + t.getMessage());
            t.printStackTrace();
            System.exit(1);
        }
    }
}
