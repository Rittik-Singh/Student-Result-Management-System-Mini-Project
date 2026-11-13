package com.srms.ui;

import com.srms.model.Faculty;
import com.srms.model.Marks;
import com.srms.model.Student;
import com.srms.model.Subject;
import com.srms.model.User;
import com.srms.service.ExaminationService;
import com.srms.service.FacultyService;
import com.srms.service.MarksService;
import com.srms.service.StudentService;
import com.srms.util.GradingUtil;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDashboard extends JFrame {

    private final User currentUser;
    private final Faculty currentFaculty;
    private final FacultyService facultyService = new FacultyService();
    private final ExaminationService examService = new ExaminationService();
    private final StudentService studentService = new StudentService();
    private final MarksService marksService = new MarksService();

    private JComboBox<Subject> subjectComboBox;
    private JComboBox<com.srms.model.Examination> examComboBox;
    private JTable marksTable;
    private DefaultTableModel tableModel;

    private JLabel statHighestLabel;
    private JLabel statLowestLabel;
    private JLabel statAverageLabel;
    private JLabel statPassRateLabel;
    private List<StudentMarksRow> currentRows = new ArrayList<>();

    private static class StudentMarksRow {
        Student student;
        Marks marks;
        double ca;
        double finalM;
        double total;
        String grade;
        int gp;
        double cp;
        boolean submitted;
    }

    public FacultyDashboard(User user, Faculty faculty) {
        this.currentUser = user;
        this.currentFaculty = faculty;

        setTitle("Faculty Academic Portal - " + faculty.getFullName() + " (" + faculty.getFacultyCode() + ")");
        setSize(1080, 750);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(14, 22, 14, 22));

        JPanel headerLeft = new JPanel(new GridLayout(2, 1));
        headerLeft.setBackground(UITheme.PRIMARY);
        JLabel title = new JLabel("FACULTY MARKS EVALUATION & ASSESSMENT PORTAL");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel(faculty.getDesignation() + " | Department of " + (faculty.getDeptName() != null ? faculty.getDeptName() : "CSE"));
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(new Color(220, 230, 245));
        headerLeft.add(title);
        headerLeft.add(sub);

        JPanel headerRight = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        headerRight.setBackground(UITheme.PRIMARY);

        JLabel userLbl = new JLabel(faculty.getFullName());
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

        // Main Panel
        JPanel mainPanel = new JPanel(new BorderLayout(12, 12));
        mainPanel.setBackground(UITheme.BG_MAIN);
        mainPanel.setBorder(new EmptyBorder(14, 20, 14, 20));

        // Top Filter & Subject Selection Card
        JPanel topCard = UITheme.createCardPanel();
        topCard.setLayout(new FlowLayout(FlowLayout.LEFT, 15, 6));

        topCard.add(new JLabel("Assigned Subject:"));
        subjectComboBox = new JComboBox<>();
        subjectComboBox.setFont(UITheme.FONT_REGULAR);
        topCard.add(subjectComboBox);

        topCard.add(new JLabel("Examination Session:"));
        examComboBox = new JComboBox<>();
        examComboBox.setFont(UITheme.FONT_REGULAR);
        topCard.add(examComboBox);

        JButton loadBtn = UITheme.createPrimaryButton("Load Student Roster");
        loadBtn.addActionListener(e -> loadStudentRoster());
        topCard.add(loadBtn);

        mainPanel.add(topCard, BorderLayout.NORTH);

        // Center Marks Table Card
        JPanel centerCard = UITheme.createCardPanel();
        centerCard.setLayout(new BorderLayout(10, 10));

        String[] cols = {"Roll No", "Student Name", "CA Marks", "Final Exam Marks", "Total (100)", "Letter Grade", "Points", "Credit Points", "Status"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int row, int col) {
                // CA Marks (col 2) and Final Exam Marks (col 3) are editable
                return col == 2 || col == 3;
            }
        };

        marksTable = new JTable(tableModel);
        marksTable.setFillsViewportHeight(true);
        marksTable.setFont(UITheme.FONT_REGULAR);
        marksTable.getTableHeader().setFont(UITheme.FONT_BOLD);

        DefaultTableCellRenderer centerRend = new DefaultTableCellRenderer();
        centerRend.setHorizontalAlignment(JLabel.CENTER);
        marksTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        marksTable.getColumnModel().getColumn(1).setPreferredWidth(220);
        marksTable.getColumnModel().getColumn(2).setCellRenderer(centerRend);
        marksTable.getColumnModel().getColumn(3).setCellRenderer(centerRend);
        marksTable.getColumnModel().getColumn(4).setCellRenderer(centerRend);
        marksTable.getColumnModel().getColumn(5).setCellRenderer(centerRend);
        marksTable.getColumnModel().getColumn(6).setCellRenderer(centerRend);
        marksTable.getColumnModel().getColumn(7).setCellRenderer(centerRend);
        marksTable.getColumnModel().getColumn(8).setCellRenderer(centerRend);

        // Listen for in-table edits to dynamically recalculate Total, Grade, and Points
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int col = e.getColumn();
            if (row >= 0 && (col == 2 || col == 3) && row < currentRows.size()) {
                recalcRow(row);
            }
        });

        centerCard.add(new JScrollPane(marksTable), BorderLayout.CENTER);

        // Bottom Stats & Action Bar
        JPanel bottomBar = new JPanel(new BorderLayout(10, 10));
        bottomBar.setBackground(UITheme.CARD_BG);

        JPanel statsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 6));
        statsPanel.setBackground(new Color(241, 245, 249));
        statsPanel.setBorder(new LineBorder(UITheme.BORDER_COLOR, 1, true));

        statHighestLabel = new JLabel("0.0");
        statHighestLabel.setFont(UITheme.FONT_BOLD);
        statLowestLabel = new JLabel("0.0");
        statLowestLabel.setFont(UITheme.FONT_BOLD);
        statAverageLabel = new JLabel("0.0");
        statAverageLabel.setFont(UITheme.FONT_BOLD);
        statPassRateLabel = new JLabel("0.0%");
        statPassRateLabel.setFont(UITheme.FONT_BOLD);

        statsPanel.add(createMiniStat("Class Highest:", statHighestLabel));
        statsPanel.add(createMiniStat("Class Lowest:", statLowestLabel));
        statsPanel.add(createMiniStat("Class Average:", statAverageLabel));
        statsPanel.add(createMiniStat("Pass Percentage:", statPassRateLabel));
        bottomBar.add(statsPanel, BorderLayout.WEST);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        actionsPanel.setBackground(UITheme.CARD_BG);

        JButton saveDraftBtn = UITheme.createSecondaryButton("Save Draft Marks");
        saveDraftBtn.addActionListener(e -> saveMarksBatch(false));

        JButton submitMarksBtn = UITheme.createSuccessButton("Submit Marks for Result Processing");
        submitMarksBtn.addActionListener(e -> saveMarksBatch(true));

        actionsPanel.add(saveDraftBtn);
        actionsPanel.add(submitMarksBtn);
        bottomBar.add(actionsPanel, BorderLayout.EAST);

        centerCard.add(bottomBar, BorderLayout.SOUTH);
        mainPanel.add(centerCard, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);

        // Initial loading of subjects and exams
        initData();
    }

    private JPanel createMiniStat(String title, JLabel val) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 4, 0));
        p.setBackground(new Color(241, 245, 249));
        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_SMALL);
        t.setForeground(UITheme.TEXT_SECONDARY);
        p.add(t);
        p.add(val);
        return p;
    }

    private void initData() {
        List<Subject> subjects = facultyService.getAssignedSubjects(currentFaculty.getFacultyId());
        subjectComboBox.removeAllItems();
        for (Subject s : subjects) {
            subjectComboBox.addItem(s);
        }

        List<com.srms.model.Examination> exams = examService.getAllExaminations();
        examComboBox.removeAllItems();
        for (com.srms.model.Examination ex : exams) {
            examComboBox.addItem(ex);
        }

        if (subjectComboBox.getItemCount() > 0 && examComboBox.getItemCount() > 0) {
            loadStudentRoster();
        }
    }

    private void loadStudentRoster() {
        Subject sub = (Subject) subjectComboBox.getSelectedItem();
        com.srms.model.Examination exam = (com.srms.model.Examination) examComboBox.getSelectedItem();
        if (sub == null || exam == null) return;

        currentRows.clear();
        tableModel.setRowCount(0);

        List<Student> students = studentService.searchStudents("", sub.getDeptId(), sub.getSemesterNumber());
        List<Marks> existingMarks = marksService.getMarksBySubjectAndExam(sub.getSubjectId(), exam.getExamId());

        for (Student stu : students) {
            StudentMarksRow row = new StudentMarksRow();
            row.student = stu;

            // Find existing mark if any
            Marks found = null;
            for (Marks m : existingMarks) {
                if (m.getStudentId() == stu.getStudentId()) {
                    found = m;
                    break;
                }
            }

            if (found != null) {
                row.marks = found;
                row.ca = found.getCaMarks();
                row.finalM = found.getFinalMarks();
                row.total = found.getTotalMarks();
                row.grade = found.getLetterGrade();
                row.gp = found.getGradePoint();
                row.cp = found.getCreditPoints();
                row.submitted = found.isSubmitted();
            } else {
                row.ca = 0.0;
                row.finalM = 0.0;
                row.total = 0.0;
                row.grade = "F";
                row.gp = 2;
                row.cp = sub.getCredits() * 2.0;
                row.submitted = false;
            }

            currentRows.add(row);
            tableModel.addRow(new Object[]{
                    stu.getRollNo(),
                    stu.getFullName(),
                    row.ca,
                    row.finalM,
                    row.total,
                    row.grade,
                    row.gp,
                    String.format("%.1f", row.cp),
                    row.submitted ? "Submitted" : "Draft / Pending"
            });
        }

        updateStats();
    }

    private void recalcRow(int rowIdx) {
        Subject sub = (Subject) subjectComboBox.getSelectedItem();
        if (sub == null || rowIdx >= currentRows.size()) return;

        try {
            double ca = Double.parseDouble(tableModel.getValueAt(rowIdx, 2).toString().trim());
            double fin = Double.parseDouble(tableModel.getValueAt(rowIdx, 3).toString().trim());

            if (ca < 0 || ca > sub.getMaxCaMarks()) {
                JOptionPane.showMessageDialog(this, "Continuous Assessment must be between 0 and " + sub.getMaxCaMarks(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (fin < 0 || fin > sub.getMaxFinalMarks()) {
                JOptionPane.showMessageDialog(this, "Final Exam Marks must be between 0 and " + sub.getMaxFinalMarks(), "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            double total = ca + fin;
            double scale100 = sub.getMaxTotalMarks() > 0 ? (total / sub.getMaxTotalMarks()) * 100.0 : total;
            GradingUtil.GradeInfo gi = GradingUtil.calculateGrade(scale100);
            double cp = GradingUtil.calculateCreditPoints(gi.getPoints(), sub.getCredits());

            StudentMarksRow r = currentRows.get(rowIdx);
            r.ca = ca;
            r.finalM = fin;
            r.total = total;
            r.grade = gi.getLetterGrade();
            r.gp = gi.getPoints();
            r.cp = cp;

            tableModel.setValueAt(total, rowIdx, 4);
            tableModel.setValueAt(gi.getLetterGrade(), rowIdx, 5);
            tableModel.setValueAt(gi.getPoints(), rowIdx, 6);
            tableModel.setValueAt(String.format("%.1f", cp), rowIdx, 7);

            updateStats();
        } catch (NumberFormatException ignored) {}
    }

    private void updateStats() {
        if (currentRows.isEmpty()) {
            statHighestLabel.setText("0.0");
            statLowestLabel.setText("0.0");
            statAverageLabel.setText("0.0");
            statPassRateLabel.setText("0.0%");
            return;
        }

        double max = 0.0, min = 100.0, sum = 0.0;
        int passCount = 0;

        for (StudentMarksRow r : currentRows) {
            if (r.total > max) max = r.total;
            if (r.total < min) min = r.total;
            sum += r.total;
            if (r.gp >= 5) passCount++;
        }

        statHighestLabel.setText(String.format("%.1f", max));
        statLowestLabel.setText(String.format("%.1f", min));
        statAverageLabel.setText(String.format("%.1f", sum / currentRows.size()));
        statPassRateLabel.setText(String.format("%.1f%%", (passCount * 100.0) / currentRows.size()));
    }

    private void saveMarksBatch(boolean submitFlag) {
        Subject sub = (Subject) subjectComboBox.getSelectedItem();
        com.srms.model.Examination exam = (com.srms.model.Examination) examComboBox.getSelectedItem();
        if (sub == null || exam == null) return;

        if (exam.isPublished()) {
            JOptionPane.showMessageDialog(this, "This examination results are already published! Marks cannot be modified directly.", "Action Restricted", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int count = 0;
            for (int i = 0; i < currentRows.size(); i++) {
                StudentMarksRow r = currentRows.get(i);
                Marks m = new Marks();
                m.setStudentId(r.student.getStudentId());
                m.setSubjectId(sub.getSubjectId());
                m.setExamId(exam.getExamId());
                m.setCaMarks(r.ca);
                m.setFinalMarks(r.finalM);
                m.setTotalMarks(r.total);
                m.setLetterGrade(r.grade);
                m.setGradePoint(r.gp);
                m.setCreditPoints(r.cp);
                m.setSubmitted(submitFlag);
                m.setEnteredByFacultyId(currentFaculty.getFacultyId());

                marksService.saveMarks(m, currentUser.getUserId(), submitFlag ? "Faculty submission" : "Faculty draft save");
                count++;
            }

            String msg = submitFlag
                    ? "Successfully submitted marks for " + count + " students! Ready for Administrator result processing."
                    : "Saved draft marks for " + count + " students successfully.";
            JOptionPane.showMessageDialog(this, msg, "Marks Saved", JOptionPane.INFORMATION_MESSAGE);
            loadStudentRoster();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error saving marks: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
