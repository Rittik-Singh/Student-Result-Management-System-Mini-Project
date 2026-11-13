package com.srms.ui;

import com.srms.model.Result;
import com.srms.model.ResultItem;
import com.srms.model.Student;
import com.srms.model.User;
import com.srms.service.ReportService;
import com.srms.service.ResultService;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class StudentDashboard extends JFrame {

    private final User currentUser;
    private final Student currentStudent;
    private final ResultService resultService = new ResultService();
    private final ReportService reportService = new ReportService();

    private JComboBox<String> examComboBox;
    private List<Result> studentResults;
    private JTable marksTable;
    private DefaultTableModel tableModel;

    private JLabel sgpaValLabel;
    private JLabel statusValLabel;
    private JLabel totalCreditsValLabel;
    private JLabel totalCpValLabel;
    private JLabel percentageValLabel;
    private JLabel cgpaValLabel;
    private JLabel ygpaValLabel;

    public StudentDashboard(User user, Student student) {
        this.currentUser = user;
        this.currentStudent = student;

        setTitle("Student Academic Portal - " + student.getFullName() + " (" + student.getRollNo() + ")");
        setSize(980, 720);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(14, 22, 14, 22));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1));
        headerLeft.setBackground(UITheme.PRIMARY);
        JLabel title = new JLabel("STUDENT ACADEMIC & RESULT PORTAL");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("MAKAUT CBCS Semester Evaluation | " + student.getCollegeName());
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(new Color(220, 230, 245));
        headerLeft.add(title);
        headerLeft.add(sub);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        headerRight.setBackground(UITheme.PRIMARY);

        JLabel userLbl = new JLabel(student.getFullName() + " (" + student.getRollNo() + ")");
        userLbl.setFont(UITheme.FONT_BOLD);
        userLbl.setForeground(Color.WHITE);

        JButton logoutBtn = UITheme.createSecondaryButton("Logout");
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        headerRight.add(userLbl);
        headerRight.add(logoutBtn);

        headerPanel.add(headerLeft, BorderLayout.WEST);
        headerPanel.add(headerRight, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Main Content
        JPanel mainPanel = new JPanel(new BorderLayout(14, 14));
        mainPanel.setBackground(UITheme.BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Profile & Cumulative Stats Card
        JPanel topCard = createProfileBanner();
        mainPanel.add(topCard, BorderLayout.NORTH);

        // Center: Exam Selector, Grade Card Table & Action Bar
        JPanel centerCard = new JPanel(new BorderLayout(10, 10));
        centerCard.setBackground(UITheme.CARD_BG);
        centerCard.setBorder(new CompoundBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true), new EmptyBorder(14, 16, 14, 16)));

        // Exam Selector Bar
        JPanel selectorBar = new JPanel(new BorderLayout());
        selectorBar.setBackground(UITheme.CARD_BG);

        JPanel leftSel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        leftSel.setBackground(UITheme.CARD_BG);
        JLabel selLbl = new JLabel("Select Examination / Semester:");
        selLbl.setFont(UITheme.FONT_BOLD);
        examComboBox = new JComboBox<>();
        examComboBox.setFont(UITheme.FONT_REGULAR);
        examComboBox.addActionListener(e -> updateSelectedResultView());
        leftSel.add(selLbl);
        leftSel.add(examComboBox);

        JPanel rightSel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightSel.setBackground(UITheme.CARD_BG);
        JButton downloadPdfBtn = UITheme.createPrimaryButton("Download Provisional Grade Card (PDF)");
        downloadPdfBtn.addActionListener(e -> handleDownloadPdf());
        rightSel.add(downloadPdfBtn);

        selectorBar.add(leftSel, BorderLayout.WEST);
        selectorBar.add(rightSel, BorderLayout.EAST);
        centerCard.add(selectorBar, BorderLayout.NORTH);

        // Subject Grade Table
        String[] columns = {"Subject Code", "Subjects Offered", "Letter Grade", "Points", "Credit", "Credit Points"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        marksTable = new JTable(tableModel);
        marksTable.setFillsViewportHeight(true);
        marksTable.setFont(UITheme.FONT_REGULAR);
        marksTable.getTableHeader().setFont(UITheme.FONT_BOLD);

        // Center align numerical columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        marksTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        marksTable.getColumnModel().getColumn(1).setPreferredWidth(320);
        marksTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        marksTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        marksTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        marksTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);

        JScrollPane scrollPane = new JScrollPane(marksTable);
        centerCard.add(scrollPane, BorderLayout.CENTER);

        // Summary Metric Box below Table
        JPanel summaryPanel = createSummaryBox();
        centerCard.add(summaryPanel, BorderLayout.SOUTH);

        mainPanel.add(centerCard, BorderLayout.CENTER);
        add(mainPanel, BorderLayout.CENTER);

        // Load data
        loadStudentResults();
    }

    private JPanel createProfileBanner() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 12, 10));
        panel.setBackground(UITheme.BG_MAIN);

        panel.add(createMiniCard("Student Name & Roll", currentStudent.getFullName(), currentStudent.getRollNo(), UITheme.PRIMARY));
        panel.add(createMiniCard("Registration Details", currentStudent.getRegistrationNo(), currentStudent.getDeptName(), UITheme.ACCENT));
        
        cgpaValLabel = new JLabel("Loading...");
        cgpaValLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        cgpaValLabel.setForeground(UITheme.SUCCESS);

        ygpaValLabel = new JLabel("Loading...");
        ygpaValLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        ygpaValLabel.setForeground(UITheme.PRIMARY);

        JPanel cgpaCard = UITheme.createCardPanel();
        cgpaCard.setLayout(new GridLayout(2, 1));
        JLabel cgpaTitle = new JLabel("Cumulative CGPA");
        cgpaTitle.setFont(UITheme.FONT_SMALL);
        cgpaTitle.setForeground(UITheme.TEXT_SECONDARY);
        cgpaCard.add(cgpaTitle);
        cgpaCard.add(cgpaValLabel);

        JPanel ygpaCard = UITheme.createCardPanel();
        ygpaCard.setLayout(new GridLayout(2, 1));
        JLabel ygpaTitle = new JLabel("Yearly Grade (YGPA)");
        ygpaTitle.setFont(UITheme.FONT_SMALL);
        ygpaTitle.setForeground(UITheme.TEXT_SECONDARY);
        ygpaCard.add(ygpaTitle);
        ygpaCard.add(ygpaValLabel);

        panel.add(cgpaCard);
        panel.add(ygpaCard);

        return panel;
    }

    private JPanel createMiniCard(String title, String val1, String val2, Color col) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(3, 1, 0, 2));
        JLabel tLbl = new JLabel(title);
        tLbl.setFont(UITheme.FONT_SMALL);
        tLbl.setForeground(UITheme.TEXT_SECONDARY);

        JLabel v1Lbl = new JLabel(val1);
        v1Lbl.setFont(UITheme.FONT_BOLD);
        v1Lbl.setForeground(col);

        JLabel v2Lbl = new JLabel(val2);
        v2Lbl.setFont(UITheme.FONT_SMALL);
        v2Lbl.setForeground(UITheme.TEXT_PRIMARY);

        card.add(tLbl);
        card.add(v1Lbl);
        card.add(v2Lbl);
        return card;
    }

    private JPanel createSummaryBox() {
        JPanel box = new JPanel(new FlowLayout(FlowLayout.CENTER, 30, 8));
        box.setBackground(new Color(241, 245, 249));
        box.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));

        totalCreditsValLabel = new JLabel("0.0");
        totalCreditsValLabel.setFont(UITheme.FONT_BOLD);

        totalCpValLabel = new JLabel("0.0");
        totalCpValLabel.setFont(UITheme.FONT_BOLD);

        sgpaValLabel = new JLabel("0.00");
        sgpaValLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sgpaValLabel.setForeground(UITheme.PRIMARY);

        statusValLabel = new JLabel("P");
        statusValLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        statusValLabel.setForeground(UITheme.SUCCESS);

        percentageValLabel = new JLabel("0.0%");
        percentageValLabel.setFont(UITheme.FONT_BOLD);

        box.add(createMetric("Total Credits:", totalCreditsValLabel));
        box.add(createMetric("Credit Points:", totalCpValLabel));
        box.add(createMetric("SGPA:", sgpaValLabel));
        box.add(createMetric("Result Status:", statusValLabel));
        box.add(createMetric("Percentage:", percentageValLabel));

        return box;
    }

    private JPanel createMetric(String title, JLabel valueLabel) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(new Color(241, 245, 249));
        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_BOLD);
        t.setForeground(UITheme.TEXT_SECONDARY);
        p.add(t);
        p.add(valueLabel);
        return p;
    }

    private void loadStudentResults() {
        studentResults = resultService.getAllResultsByStudent(currentStudent.getStudentId());
        examComboBox.removeAllItems();

        if (studentResults.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No published examination results found for your account yet.", "Notice", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        Result latest = null;
        for (Result r : studentResults) {
            String semName = "Semester " + r.getSemesterNumber() + " (" + (r.getExamName() != null ? r.getExamName() : "Exam") + ")";
            examComboBox.addItem(semName);
            latest = r;
        }

        // Set cumulative CGPA / YGPA from latest record
        if (latest != null) {
            cgpaValLabel.setText(latest.getCgpa() != null ? String.format("%.2f", latest.getCgpa()) : "N/A");
            ygpaValLabel.setText(latest.getYgpa() != null ? String.format("%.2f", latest.getYgpa()) : "N/A");
            examComboBox.setSelectedIndex(studentResults.size() - 1);
        }
    }

    private void updateSelectedResultView() {
        int idx = examComboBox.getSelectedIndex();
        if (idx < 0 || idx >= studentResults.size()) return;

        Result r = studentResults.get(idx);
        tableModel.setRowCount(0);

        for (ResultItem item : r.getItems()) {
            tableModel.addRow(new Object[]{
                    item.getSubjectCode(),
                    item.getSubjectName(),
                    item.getLetterGrade(),
                    item.getPoints(),
                    String.format("%.1f", item.getCredit()),
                    String.format("%.1f", item.getCreditPoints())
            });
        }

        totalCreditsValLabel.setText(String.format("%.1f", r.getTotalCredits()));
        totalCpValLabel.setText(String.format("%.1f", r.getTotalCreditPoints()));
        sgpaValLabel.setText(String.format("%.2f", r.getSgpa()));
        statusValLabel.setText(r.getResultStatus() + " (" + r.getStatusDescription() + ")");
        if ("P".equalsIgnoreCase(r.getResultStatus())) {
            statusValLabel.setForeground(UITheme.SUCCESS);
        } else if ("XP".equalsIgnoreCase(r.getResultStatus())) {
            statusValLabel.setForeground(UITheme.WARNING);
        } else {
            statusValLabel.setForeground(UITheme.DANGER);
        }
        percentageValLabel.setText(String.format("%.2f%%", r.getPercentage()));
    }

    private void handleDownloadPdf() {
        int idx = examComboBox.getSelectedIndex();
        if (idx < 0 || idx >= studentResults.size()) {
            JOptionPane.showMessageDialog(this, "Please select an examination first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Result r = studentResults.get(idx);
        try {
            File pdfFile = reportService.generateStudentMarksheetPdf(currentStudent.getStudentId(), r.getExamId(), "reports");
            int choice = JOptionPane.showConfirmDialog(this,
                    "Official Provisional Grade Card generated successfully:\n" + pdfFile.getAbsolutePath() +
                            "\n\nWould you like to open it now?",
                    "Grade Card Downloaded",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.INFORMATION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error generating grade card: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
