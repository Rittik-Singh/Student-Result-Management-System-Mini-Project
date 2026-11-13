package com.srms.service;

import com.srms.dao.StudentDAO;
import com.srms.model.Result;
import com.srms.model.Student;
import com.srms.util.PdfMarksheetGenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.List;

public class ReportService {

    private final StudentDAO studentDAO = new StudentDAO();
    private final ResultService resultService = new ResultService();

    public File generateStudentMarksheetPdf(int studentId, int examId, String outputDir) throws Exception {
        Student student = studentDAO.findById(studentId);
        if (student == null) throw new IllegalArgumentException("Student not found for ID: " + studentId);

        Result result = resultService.getResultByStudentAndExam(studentId, examId);
        if (result == null) throw new IllegalArgumentException("No result found for student in this examination.");

        return PdfMarksheetGenerator.generateProvisionalGradeCard(student, result, outputDir);
    }

    public File generateConsolidatedExamReportCsv(int examId, String outputDir) throws Exception {
        File dir = new File(outputDir);
        if (!dir.exists()) dir.mkdirs();

        List<Result> results = resultService.getAllResultsByExam(examId);
        File csvFile = new File(dir, "Consolidated_Exam_" + examId + "_Report.csv");

        try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
            pw.println("Roll Number,Student Name,Registration No,Semester,Total Marks,Max Marks,Percentage,Total Credits,Total Credit Points,SGPA,YGPA,CGPA,Status,Backlogs");
            for (Result r : results) {
                pw.printf("%s,\"%s\",%s,%d,%.1f,%.1f,%.2f,%.1f,%.1f,%.2f,%s,%s,%s,%d%n",
                        r.getRollNo(),
                        r.getStudentName(),
                        r.getRegistrationNo(),
                        r.getSemesterNumber(),
                        r.getTotalMarks(),
                        r.getMaxMarks(),
                        r.getPercentage(),
                        r.getTotalCredits(),
                        r.getTotalCreditPoints(),
                        r.getSgpa(),
                        r.getYgpa() != null ? String.format("%.2f", r.getYgpa()) : "N/A",
                        r.getCgpa() != null ? String.format("%.2f", r.getCgpa()) : "N/A",
                        r.getResultStatus(),
                        r.getBacklogCount()
                );
            }
        }
        return csvFile;
    }
}
