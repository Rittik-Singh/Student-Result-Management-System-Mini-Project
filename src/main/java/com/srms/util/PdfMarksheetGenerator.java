package com.srms.util;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.srms.model.Result;
import com.srms.model.ResultItem;
import com.srms.model.Student;

import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PdfMarksheetGenerator {

    public static File generateProvisionalGradeCard(Student student, Result result, String outputDirectory) throws Exception {
        File dir = new File(outputDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        String safeRoll = student.getRollNo().replaceAll("[^a-zA-Z0-9_-]", "_");
        String filename = "GradeCard_" + safeRoll + "_Sem" + result.getSemesterNumber() + ".pdf";
        File pdfFile = new File(dir, filename);

        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(pdfFile));
        document.open();

        Font universityFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, Font.BOLD, Color.BLACK);
        Font subUniFont = FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, Color.DARK_GRAY);
        Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11, Font.BOLD, Color.BLACK);
        Font boldFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8.5f, Font.BOLD, Color.BLACK);
        Font regularFont = FontFactory.getFont(FontFactory.HELVETICA, 8.5f, Font.NORMAL, Color.BLACK);
        Font smallFont = FontFactory.getFont(FontFactory.HELVETICA, 7.5f, Font.ITALIC, Color.DARK_GRAY);

        // Header
        Paragraph p1 = new Paragraph("MAULANA ABUL KALAM AZAD UNIVERSITY OF TECHNOLOGY, WEST BENGAL", universityFont);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);

        Paragraph p2 = new Paragraph("(Formerly known as West Bengal University of Technology)", subUniFont);
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);

        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 4)));

        Paragraph p3 = new Paragraph("PROVISIONAL GRADE CARD", titleFont);
        p3.setAlignment(Element.ALIGN_CENTER);
        document.add(p3);

        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 6)));

        // Exam Title Banner
        String examTitle = (result.getExamName() != null && !result.getExamName().isEmpty())
                ? result.getExamName().toUpperCase()
                : "SEMESTER " + result.getSemesterNumber() + " EXAMINATION";
        
        PdfPTable examTable = new PdfPTable(1);
        examTable.setWidthPercentage(100);
        PdfPCell examCell = new PdfPCell(new Phrase(examTitle, boldFont));
        examCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        examCell.setBackgroundColor(new Color(245, 245, 245));
        examCell.setPadding(5);
        examTable.addCell(examCell);
        document.add(examTable);

        // Student Info Box
        PdfPTable infoTable = new PdfPTable(new float[]{50, 50});
        infoTable.setWidthPercentage(100);
        
        addInfoCell(infoTable, "NAME : " + student.getFullName().toUpperCase(), boldFont, Element.ALIGN_LEFT);
        addInfoCell(infoTable, "ROLL NO. : " + student.getRollNo(), boldFont, Element.ALIGN_LEFT);
        
        PdfPCell regCell = new PdfPCell(new Phrase("REGISTRATION NO : " + (student.getRegistrationNo() != null ? student.getRegistrationNo() : "N/A"), boldFont));
        regCell.setColspan(2);
        regCell.setPadding(4);
        infoTable.addCell(regCell);

        PdfPCell progCell = new PdfPCell(new Phrase("PROGRAM: " + (student.getProgramName() != null ? student.getProgramName().toUpperCase() : "BACHELOR OF TECHNOLOGY"), boldFont));
        progCell.setColspan(2);
        progCell.setPadding(4);
        infoTable.addCell(progCell);

        PdfPCell collCell = new PdfPCell(new Phrase("COLLEGE / INSTITUTION: " + (student.getCollegeName() != null ? student.getCollegeName().toUpperCase() : "INSTITUTE OF TECHNOLOGY"), boldFont));
        collCell.setColspan(2);
        collCell.setPadding(4);
        infoTable.addCell(collCell);

        document.add(infoTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 6)));

        // Subject Marks Table
        PdfPTable marksTable = new PdfPTable(new float[]{18, 44, 11, 9, 9, 9});
        marksTable.setWidthPercentage(100);

        addTableHeader(marksTable, "Subject Code", boldFont);
        addTableHeader(marksTable, "Subjects Offered", boldFont);
        addTableHeader(marksTable, "Letter Grade", boldFont);
        addTableHeader(marksTable, "Points", boldFont);
        addTableHeader(marksTable, "Credit", boldFont);
        addTableHeader(marksTable, "Credit Points", boldFont);

        for (ResultItem item : result.getItems()) {
            addTableCell(marksTable, item.getSubjectCode(), regularFont, Element.ALIGN_LEFT);
            addTableCell(marksTable, item.getSubjectName(), regularFont, Element.ALIGN_LEFT);
            addTableCell(marksTable, item.getLetterGrade(), boldFont, Element.ALIGN_CENTER);
            addTableCell(marksTable, String.valueOf(item.getPoints()), regularFont, Element.ALIGN_CENTER);
            addTableCell(marksTable, String.format("%.1f", item.getCredit()), regularFont, Element.ALIGN_CENTER);
            addTableCell(marksTable, String.format("%.1f", item.getCreditPoints()), regularFont, Element.ALIGN_CENTER);
        }

        // Total Row
        PdfPCell totalTitleCell = new PdfPCell(new Phrase("Total", boldFont));
        totalTitleCell.setColspan(4);
        totalTitleCell.setHorizontalAlignment(Element.ALIGN_RIGHT);
        totalTitleCell.setPadding(5);
        marksTable.addCell(totalTitleCell);

        PdfPCell totalCreditsCell = new PdfPCell(new Phrase(String.format("%.1f", result.getTotalCredits()), boldFont));
        totalCreditsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalCreditsCell.setPadding(5);
        marksTable.addCell(totalCreditsCell);

        PdfPCell totalPointsCell = new PdfPCell(new Phrase(String.format("%.1f", result.getTotalCreditPoints()), boldFont));
        totalPointsCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        totalPointsCell.setPadding(5);
        marksTable.addCell(totalPointsCell);

        document.add(marksTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 6)));

        // SGPA & Result Table
        PdfPTable sgpaTable = new PdfPTable(new float[]{50, 50});
        sgpaTable.setWidthPercentage(100);

        String semSuffix = getSemesterLabel(result.getSemesterNumber());
        PdfPCell sgpaCell = new PdfPCell(new Phrase("SGPA   " + semSuffix + " : " + String.format("%.2f", result.getSgpa()), boldFont));
        sgpaCell.setPadding(6);
        sgpaTable.addCell(sgpaCell);

        PdfPCell emptyRight = new PdfPCell(new Phrase("", regularFont));
        emptyRight.setPadding(6);
        sgpaTable.addCell(emptyRight);

        PdfPCell resultCell = new PdfPCell(new Phrase("RESULT   " + semSuffix + " : " + result.getResultStatus(), boldFont));
        resultCell.setPadding(6);
        sgpaTable.addCell(resultCell);

        PdfPCell descCell = new PdfPCell(new Phrase(result.getStatusDescription(), regularFont));
        descCell.setPadding(6);
        sgpaTable.addCell(descCell);

        document.add(sgpaTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));

        // Discrepancy Note
        Paragraph note1 = new Paragraph("Please report of any discrepancy through college within 7 days,", smallFont);
        note1.setAlignment(Element.ALIGN_RIGHT);
        document.add(note1);

        Paragraph note2 = new Paragraph("Otherwise, University will not responsible for any errors in transcripts (if any)", smallFont);
        note2.setAlignment(Element.ALIGN_RIGHT);
        document.add(note2);

        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 25)));

        // Footer Signatures
        PdfPTable footerTable = new PdfPTable(new float[]{50, 50});
        footerTable.setWidthPercentage(100);
        footerTable.getDefaultCell().setBorder(Rectangle.NO_BORDER);

        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        String currentDate = sdf.format(new Date());

        PdfPCell leftFooter = new PdfPCell();
        leftFooter.setBorder(Rectangle.NO_BORDER);
        leftFooter.addElement(new Paragraph("Kolkata", regularFont));
        leftFooter.addElement(new Paragraph(currentDate, regularFont));
        footerTable.addCell(leftFooter);

        PdfPCell rightFooter = new PdfPCell();
        rightFooter.setBorder(Rectangle.NO_BORDER);
        rightFooter.setHorizontalAlignment(Element.ALIGN_RIGHT);
        Paragraph sigLine = new Paragraph("____________________________", regularFont);
        sigLine.setAlignment(Element.ALIGN_RIGHT);
        Paragraph coeText = new Paragraph("Controller of Examinations", boldFont);
        coeText.setAlignment(Element.ALIGN_RIGHT);
        rightFooter.addElement(sigLine);
        rightFooter.addElement(coeText);
        footerTable.addCell(rightFooter);

        document.add(footerTable);

        // Page 2: Grading System Guide & Rules
        document.newPage();
        Paragraph guideTitle = new Paragraph("MAKAUT ACADEMIC GRADING SYSTEM & EVALUATION SCHEME", titleFont);
        guideTitle.setAlignment(Element.ALIGN_CENTER);
        document.add(guideTitle);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 8)));

        PdfPTable guideTable = new PdfPTable(new float[]{30, 20, 30, 20});
        guideTable.setWidthPercentage(100);
        addTableHeader(guideTable, "Classification", boldFont);
        addTableHeader(guideTable, "Letter Grade", boldFont);
        addTableHeader(guideTable, "Score on 100 Percentage Points", boldFont);
        addTableHeader(guideTable, "Points", boldFont);

        addGuideRow(guideTable, "Outstanding", "O", "100 to 90", "10", regularFont, boldFont);
        addGuideRow(guideTable, "Excellent", "E", "89 to 80", "9", regularFont, boldFont);
        addGuideRow(guideTable, "Very Good", "A", "79 to 70", "8", regularFont, boldFont);
        addGuideRow(guideTable, "Good", "B", "69 to 60", "7", regularFont, boldFont);
        addGuideRow(guideTable, "Fair", "C", "59 to 50", "6", regularFont, boldFont);
        addGuideRow(guideTable, "Below Average", "D", "49 to 40", "5", regularFont, boldFont);
        addGuideRow(guideTable, "Failed", "F", "Below 40", "2", regularFont, boldFont);
        addGuideRow(guideTable, "Incomplete", "I", "---", "2", regularFont, boldFont);

        document.add(guideTable);
        document.add(new Paragraph(" ", FontFactory.getFont(FontFactory.HELVETICA, 10)));

        Paragraph rules = new Paragraph(
                "Result Status Classifications:\n" +
                "  - P  : Passed and Promoted (Cleared all semester subjects with minimum 'D' grade)\n" +
                "  - XP : Eligible for Promotion with Backlogs (Permitted to carry forward backlog subjects)\n" +
                "  - X  : Not eligible for Semester Promotion / Year Lag\n\n" +
                "Grade Point Calculation Formulas:\n" +
                "  - Credit Points = Grade Point x Subject Credits\n" +
                "  - SGPA (Semester Grade Point Average) = Total Credit Points / Total Subject Credits\n" +
                "  - YGPA (Yearly Grade Point Average) = (Odd Sem Credit Index + Even Sem Credit Index) / (Odd Sem Credits + Even Sem Credits)\n" +
                "  - DGPA (Degree Grade Point Average for 4-Yr B.Tech) = (YGPA1 + YGPA2 + 1.5*YGPA3 + 1.5*YGPA4) / 5\n" +
                "  - CGPA (Cumulative Grade Point Average) = Sum of all Semester Credit Points / Sum of all Semester Credits\n",
                regularFont
        );
        document.add(rules);

        document.close();
        return pdfFile;
    }

    private static void addInfoCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static void addTableHeader(PdfPTable table, String header, Font font) {
        PdfPCell cell = new PdfPCell(new Phrase(header, font));
        cell.setBackgroundColor(new Color(235, 235, 235));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setPadding(5);
        table.addCell(cell);
    }

    private static void addTableCell(PdfPTable table, String text, Font font, int alignment) {
        PdfPCell cell = new PdfPCell(new Phrase(text, font));
        cell.setHorizontalAlignment(alignment);
        cell.setPadding(4);
        table.addCell(cell);
    }

    private static void addGuideRow(PdfPTable table, String c1, String c2, String c3, String c4, Font regFont, Font boldFont) {
        addTableCell(table, c1, regFont, Element.ALIGN_LEFT);
        addTableCell(table, c2, boldFont, Element.ALIGN_CENTER);
        addTableCell(table, c3, regFont, Element.ALIGN_CENTER);
        addTableCell(table, c4, boldFont, Element.ALIGN_CENTER);
    }

    private static String getSemesterLabel(int sem) {
        String ord = switch (sem) {
            case 1 -> "1st";
            case 2 -> "2nd";
            case 3 -> "3rd";
            default -> sem + "th";
        };
        String type = (sem % 2 == 1) ? "ODD" : "EVEN";
        return type + ". (" + ord + ") SEMESTER";
    }
}
