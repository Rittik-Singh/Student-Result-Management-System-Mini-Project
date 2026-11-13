package com.srms.ui;

import com.srms.config.DatabaseConnection;
import com.srms.model.*;
import com.srms.service.*;

import javax.swing.*;
import javax.swing.border.CompoundBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.util.List;

public class AdminDashboard extends JFrame {

    private final User currentUser;
    private final StudentService studentService = new StudentService();
    private final FacultyService facultyService = new FacultyService();
    private final SubjectService subjectService = new SubjectService();
    private final ExaminationService examService = new ExaminationService();
    private final MarksService marksService = new MarksService();
    private final ResultService resultService = new ResultService();
    private final ReportService reportService = new ReportService();

    // Tabbed Pane
    private JTabbedPane tabbedPane;

    // Overview Stats Labels
    private JLabel statStudentsLabel;
    private JLabel statFacultyLabel;
    private JLabel statSubjectsLabel;
    private JLabel statExamsLabel;
    private JLabel statPublishedLabel;
    private JLabel statPendingMarksLabel;

    // Student Management Components
    private JTable studentTable;
    private DefaultTableModel studentModel;
    private JTextField studentSearchField;
    private JComboBox<Department> studentDeptCombo;
    private JComboBox<String> studentSemCombo;

    // Faculty Management Components
    private JTable facultyTable;
    private DefaultTableModel facultyModel;

    // Subject Management Components
    private JTable subjectTable;
    private DefaultTableModel subjectModel;

    // Examination Management Components
    private JTable examTable;
    private DefaultTableModel examModel;

    // Result Management Components
    private JComboBox<Examination> resultExamCombo;
    private JTable resultTable;
    private DefaultTableModel resultModel;

    // Audit History Components
    private JComboBox<Examination> auditExamCombo;
    private JTable auditTable;
    private DefaultTableModel auditModel;

    // Topper Rankings Components
    private JComboBox<Examination> topperExamCombo;
    private JTable topperTable;
    private DefaultTableModel topperModel;
    private JLabel topperPassRateLabel;
    private JLabel topperAvgSgpaLabel;

    // Reports Components
    private JComboBox<Examination> reportExamCombo;
    private JComboBox<Student> reportStudentCombo;
    private JTable reportPreviewTable;
    private DefaultTableModel reportPreviewModel;

    public AdminDashboard(User user) {
        this.currentUser = user;

        setTitle("MAKAUT Student Result Management System - Administrator Portal");
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(14, 22, 14, 22));

        JPanel leftHeader = new JPanel(new GridLayout(2, 1));
        leftHeader.setBackground(UITheme.PRIMARY);
        JLabel title = new JLabel("MAKAUT STUDENT RESULT MANAGEMENT SYSTEM");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        JLabel sub = new JLabel("System Administration & Examination Controller Portal | Active DB: " + DatabaseConnection.getActiveDbInfo());
        sub.setFont(UITheme.FONT_SMALL);
        sub.setForeground(new Color(220, 230, 245));
        leftHeader.add(title);
        leftHeader.add(sub);

        JPanel rightHeader = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 5));
        rightHeader.setBackground(UITheme.PRIMARY);

        JLabel adminLbl = new JLabel("Logged in: " + user.getUsername() + " (ADMIN)");
        adminLbl.setFont(UITheme.FONT_BOLD);
        adminLbl.setForeground(Color.WHITE);

        JButton dbBtn = UITheme.createSecondaryButton("DB Settings");
        dbBtn.addActionListener(e -> {
            new DatabaseConfigDialog(this).setVisible(true);
            refreshAllTabs();
        });

        JButton logoutBtn = UITheme.createSecondaryButton("Logout");
        logoutBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        rightHeader.add(adminLbl);
        rightHeader.add(dbBtn);
        rightHeader.add(logoutBtn);

        headerPanel.add(leftHeader, BorderLayout.WEST);
        headerPanel.add(rightHeader, BorderLayout.EAST);
        add(headerPanel, BorderLayout.NORTH);

        // Tabbed Pane for Modules
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(UITheme.FONT_BOLD);

        tabbedPane.addTab("Dashboard Overview", createOverviewTab());
        tabbedPane.addTab("Student Management", createStudentTab());
        tabbedPane.addTab("Faculty Management", createFacultyTab());
        tabbedPane.addTab("Subject Management", createSubjectTab());
        tabbedPane.addTab("Examination Management", createExamTab());
        tabbedPane.addTab("Result Processing", createResultTab());
        tabbedPane.addTab("Result Correction & Audit", createAuditTab());
        tabbedPane.addTab("Toppers & Analytics", createTopperTab());
        tabbedPane.addTab("Reports & Marksheet PDF", createReportsTab());

        add(tabbedPane, BorderLayout.CENTER);

        // Load initial data
        refreshAllTabs();
    }

    // =========================================================================
    // MODULE 2: DASHBOARD OVERVIEW
    // =========================================================================
    private JPanel createOverviewTab() {
        JPanel panel = new JPanel(new BorderLayout(14, 14));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(16, 20, 16, 20));

        // Stat Cards Panel
        JPanel statsPanel = new JPanel(new GridLayout(2, 3, 14, 14));
        statsPanel.setBackground(UITheme.BG_MAIN);

        statStudentsLabel = new JLabel("0");
        statFacultyLabel = new JLabel("0");
        statSubjectsLabel = new JLabel("0");
        statExamsLabel = new JLabel("0");
        statPublishedLabel = new JLabel("0");
        statPendingMarksLabel = new JLabel("0");

        statsPanel.add(createKpiCard("Registered Students", statStudentsLabel, "Active student academic records", UITheme.PRIMARY));
        statsPanel.add(createKpiCard("Faculty Members", statFacultyLabel, "Teaching & examining staff", UITheme.ACCENT));
        statsPanel.add(createKpiCard("Curriculum Subjects", statSubjectsLabel, "B.Tech CSE courses mapped", UITheme.SECONDARY));
        statsPanel.add(createKpiCard("Examinations", statExamsLabel, "Semester exam sessions", UITheme.PRIMARY));
        statsPanel.add(createKpiCard("Published Results", statPublishedLabel, "Available for student download", UITheme.SUCCESS));
        statsPanel.add(createKpiCard("Pending Marks Entry", statPendingMarksLabel, "Awaiting faculty submission", UITheme.WARNING));

        panel.add(statsPanel, BorderLayout.NORTH);

        // Center Quick Actions & Guidelines Card
        JPanel centerPanel = UITheme.createCardPanel();
        centerPanel.setLayout(new BorderLayout(10, 10));

        JLabel guideTitle = new JLabel("MAKAUT Academic Examination Workflow & Quick Actions");
        guideTitle.setFont(UITheme.FONT_SUBTITLE);
        centerPanel.add(guideTitle, BorderLayout.NORTH);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 10));
        actionsPanel.setBackground(Color.WHITE);

        JButton procBtn = UITheme.createPrimaryButton("Process Semester Results");
        procBtn.addActionListener(e -> tabbedPane.setSelectedIndex(5));

        JButton pubBtn = UITheme.createSuccessButton("Publish Exam Results");
        pubBtn.addActionListener(e -> tabbedPane.setSelectedIndex(4));

        JButton addStuBtn = UITheme.createSecondaryButton("+ Add New Student");
        addStuBtn.addActionListener(e -> openAddStudentDialog());

        JButton addSubBtn = UITheme.createSecondaryButton("+ Add New Subject");
        addSubBtn.addActionListener(e -> openAddSubjectDialog());

        JButton genPdfBtn = UITheme.createPrimaryButton("Generate PDF Marksheet");
        genPdfBtn.addActionListener(e -> tabbedPane.setSelectedIndex(8));

        actionsPanel.add(procBtn);
        actionsPanel.add(pubBtn);
        actionsPanel.add(addStuBtn);
        actionsPanel.add(addSubBtn);
        actionsPanel.add(genPdfBtn);

        centerPanel.add(actionsPanel, BorderLayout.CENTER);

        JTextArea infoText = new JTextArea(
                "System Operational Notes:\n" +
                "  1. Examination Setup: Define examination sessions and semesters in the Examination Management tab.\n" +
                "  2. Faculty Assignment: Assign subjects to faculty members to enable marks entry.\n" +
                "  3. Result Computation: Once faculty submit marks, click 'Process Semester Results' to compute SGPA, YGPA, and CGPA.\n" +
                "  4. Publication: Once approved, publish examination results to allow students to download their Provisional Grade Cards.\n" +
                "  5. Audited Corrections: Any modification to published marks is recorded in the Result Correction & Audit trail."
        );
        infoText.setFont(UITheme.FONT_REGULAR);
        infoText.setEditable(false);
        infoText.setBackground(new Color(248, 250, 252));
        infoText.setBorder(new EmptyBorder(10, 12, 10, 12));
        centerPanel.add(infoText, BorderLayout.SOUTH);

        panel.add(centerPanel, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createKpiCard(String title, JLabel valLabel, String subtext, Color col) {
        JPanel card = UITheme.createCardPanel();
        card.setLayout(new GridLayout(3, 1, 0, 4));

        JLabel t = new JLabel(title);
        t.setFont(UITheme.FONT_BOLD);
        t.setForeground(UITheme.TEXT_SECONDARY);

        valLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valLabel.setForeground(col);

        JLabel s = new JLabel(subtext);
        s.setFont(UITheme.FONT_SMALL);
        s.setForeground(UITheme.TEXT_SECONDARY);

        card.add(t);
        card.add(valLabel);
        card.add(s);
        return card;
    }

    // =========================================================================
    // MODULE 3: STUDENT MANAGEMENT
    // =========================================================================
    private JPanel createStudentTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Filter / Search Toolbar
        JPanel toolBar = UITheme.createCardPanel();
        toolBar.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        toolBar.add(new JLabel("Search (Roll/Name):"));
        studentSearchField = new JTextField(12);
        toolBar.add(studentSearchField);

        toolBar.add(new JLabel("Department:"));
        studentDeptCombo = new JComboBox<>();
        toolBar.add(studentDeptCombo);

        toolBar.add(new JLabel("Semester:"));
        studentSemCombo = new JComboBox<>(new String[]{"All", "1", "2", "3", "4", "5", "6", "7", "8"});
        toolBar.add(studentSemCombo);

        JButton filterBtn = UITheme.createPrimaryButton("Search / Filter");
        filterBtn.addActionListener(e -> loadStudents());
        toolBar.add(filterBtn);

        JButton resetBtn = UITheme.createSecondaryButton("Reset");
        resetBtn.addActionListener(e -> {
            studentSearchField.setText("");
            if (studentDeptCombo.getItemCount() > 0) studentDeptCombo.setSelectedIndex(0);
            studentSemCombo.setSelectedIndex(0);
            loadStudents();
        });
        toolBar.add(resetBtn);

        panel.add(toolBar, BorderLayout.NORTH);

        // Student Table
        String[] cols = {"ID", "Roll Number", "Full Name", "Registration No", "Dept", "Sem", "Email", "Phone"};
        studentModel = new DefaultTableModel(cols, 0) {
            @Override
            public boolean isCellEditable(int r, int c) { return false; }
        };
        studentTable = new JTable(studentModel);
        studentTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        studentTable.setFont(UITheme.FONT_REGULAR);
        studentTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        studentTable.getColumnModel().getColumn(0).setMaxWidth(50);
        studentTable.getColumnModel().getColumn(1).setPreferredWidth(120);
        studentTable.getColumnModel().getColumn(2).setPreferredWidth(180);
        studentTable.getColumnModel().getColumn(3).setPreferredWidth(180);

        panel.add(new JScrollPane(studentTable), BorderLayout.CENTER);

        // Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        actionPanel.setBackground(UITheme.BG_MAIN);

        JButton addBtn = UITheme.createSuccessButton("+ Add Student");
        addBtn.addActionListener(e -> openAddStudentDialog());

        JButton historyBtn = UITheme.createPrimaryButton("View Academic History");
        historyBtn.addActionListener(e -> viewSelectedStudentHistory());

        JButton delBtn = UITheme.createDangerButton("Delete Student");
        delBtn.addActionListener(e -> deleteSelectedStudent());

        actionPanel.add(addBtn);
        actionPanel.add(historyBtn);
        actionPanel.add(delBtn);
        panel.add(actionPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadStudents() {
        studentModel.setRowCount(0);
        String keyword = studentSearchField.getText().trim();
        Department selDept = (Department) studentDeptCombo.getSelectedItem();
        Integer deptId = (selDept != null && selDept.getDeptId() > 0) ? selDept.getDeptId() : null;

        String semStr = (String) studentSemCombo.getSelectedItem();
        Integer sem = (semStr != null && !"All".equals(semStr)) ? Integer.parseInt(semStr) : null;

        List<Student> list = studentService.searchStudents(keyword, deptId, sem);
        for (Student s : list) {
            studentModel.addRow(new Object[]{
                    s.getStudentId(),
                    s.getRollNo(),
                    s.getFullName(),
                    s.getRegistrationNo(),
                    s.getDeptCode(),
                    s.getCurrentSemester(),
                    s.getEmail(),
                    s.getPhone()
            });
        }
    }

    private void openAddStudentDialog() {
        JDialog dlg = new JDialog(this, "Add New Student Record", true);
        dlg.setSize(480, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField rollF = new JTextField(16);
        JTextField regF = new JTextField(16);
        JTextField nameF = new JTextField(16);
        JTextField emailF = new JTextField(16);
        JTextField phoneF = new JTextField(16);
        JComboBox<Department> deptF = new JComboBox<>();
        for (Department d : subjectService.getAllDepartments()) deptF.addItem(d);
        JComboBox<Integer> semF = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        JPasswordField passF = new JPasswordField("Student@123", 16);

        addFormField(form, gbc, 0, "Roll Number *:", rollF);
        addFormField(form, gbc, 1, "Registration No *:", regF);
        addFormField(form, gbc, 2, "Full Name *:", nameF);
        addFormField(form, gbc, 3, "Email *:", emailF);
        addFormField(form, gbc, 4, "Phone:", phoneF);
        addFormField(form, gbc, 5, "Department *:", deptF);
        addFormField(form, gbc, 6, "Semester *:", semF);
        addFormField(form, gbc, 7, "Default Password *:", passF);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UITheme.createPrimaryButton("Save Student");
        saveBtn.addActionListener(e -> {
            Department d = (Department) deptF.getSelectedItem();
            if (d == null) return;
            Student stu = new Student();
            stu.setRollNo(rollF.getText().trim());
            stu.setRegistrationNo(regF.getText().trim());
            stu.setFullName(nameF.getText().trim());
            stu.setEmail(emailF.getText().trim());
            stu.setPhone(phoneF.getText().trim());
            stu.setDeptId(d.getDeptId());
            stu.setCurrentSemester((Integer) semF.getSelectedItem());
            stu.setCollegeName("276-BUDGE BUDGE INSTITUTE OF TECHNOLOGY");
            stu.setProgramName("BACHELOR OF TECHNOLOGY IN COMPUTER SCIENCE & ENGINEERING");
            stu.setAdmissionYear(2024);

            boolean ok = studentService.createStudent(stu, new String(passF.getPassword()));
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Student registered successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadStudents();
                refreshOverviewStats();
            } else {
                JOptionPane.showMessageDialog(dlg, "Failed to register student. Ensure Roll Number and Email are unique.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton closeBtn = UITheme.createSecondaryButton("Cancel");
        closeBtn.addActionListener(e -> dlg.dispose());

        btnP.add(saveBtn);
        btnP.add(closeBtn);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void viewSelectedStudentHistory() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = (int) studentModel.getValueAt(row, 0);
        Student stu = studentService.getStudentById(studentId);
        List<Result> results = resultService.getAllResultsByStudent(studentId);

        JDialog dlg = new JDialog(this, "Academic History - " + stu.getFullName() + " (" + stu.getRollNo() + ")", true);
        dlg.setSize(750, 480);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout(10, 10));

        JPanel pInfo = new JPanel(new GridLayout(2, 2, 8, 4));
        pInfo.setBackground(Color.WHITE);
        pInfo.setBorder(new EmptyBorder(10, 14, 10, 14));
        pInfo.add(new JLabel("Name: " + stu.getFullName()));
        pInfo.add(new JLabel("Roll No: " + stu.getRollNo()));
        pInfo.add(new JLabel("Registration No: " + stu.getRegistrationNo()));
        pInfo.add(new JLabel("Program: " + stu.getProgramName()));
        dlg.add(pInfo, BorderLayout.NORTH);

        String[] cols = {"Semester", "Exam Name", "Total Marks", "Credits", "Credit Points", "SGPA", "YGPA", "CGPA", "Status"};
        DefaultTableModel mod = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        for (Result r : results) {
            mod.addRow(new Object[]{
                    "Sem " + r.getSemesterNumber(),
                    r.getExamName(),
                    r.getTotalMarks(),
                    r.getTotalCredits(),
                    r.getTotalCreditPoints(),
                    String.format("%.2f", r.getSgpa()),
                    r.getYgpa() != null ? String.format("%.2f", r.getYgpa()) : "-",
                    r.getCgpa() != null ? String.format("%.2f", r.getCgpa()) : "-",
                    r.getResultStatus()
            });
        }
        JTable tbl = new JTable(mod);
        dlg.add(new JScrollPane(tbl), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 8));
        JButton close = UITheme.createSecondaryButton("Close");
        close.addActionListener(e -> dlg.dispose());
        bot.add(close);
        dlg.add(bot, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void deleteSelectedStudent() {
        int row = studentTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int studentId = (int) studentModel.getValueAt(row, 0);
        String name = (String) studentModel.getValueAt(row, 2);
        int opt = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to delete student: " + name + "?\nThis will also remove associated marks and user accounts.",
                "Confirm Deletion", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (opt == JOptionPane.YES_OPTION) {
            boolean ok = studentService.deleteStudent(studentId);
            if (ok) {
                JOptionPane.showMessageDialog(this, "Student deleted successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadStudents();
                refreshOverviewStats();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete student.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    // =========================================================================
    // MODULE 4: FACULTY MANAGEMENT
    // =========================================================================
    private JPanel createFacultyTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        String[] cols = {"ID", "Code", "Faculty Name", "Designation", "Department", "Email", "Phone", "Status"};
        facultyModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        facultyTable = new JTable(facultyModel);
        facultyTable.setFont(UITheme.FONT_REGULAR);
        facultyTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        panel.add(new JScrollPane(facultyTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        btnPanel.setBackground(UITheme.BG_MAIN);

        JButton addBtn = UITheme.createSuccessButton("+ Add Faculty");
        addBtn.addActionListener(e -> openAddFacultyDialog());

        JButton assignSubBtn = UITheme.createPrimaryButton("Assign Subject to Faculty");
        assignSubBtn.addActionListener(e -> openAssignSubjectDialog());

        JButton delBtn = UITheme.createDangerButton("Delete Faculty");
        delBtn.addActionListener(e -> deleteSelectedFaculty());

        btnPanel.add(addBtn);
        btnPanel.add(assignSubBtn);
        btnPanel.add(delBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadFaculty() {
        facultyModel.setRowCount(0);
        List<Faculty> list = facultyService.getAllFaculty();
        for (Faculty f : list) {
            facultyModel.addRow(new Object[]{
                    f.getFacultyId(),
                    f.getFacultyCode(),
                    f.getFullName(),
                    f.getDesignation(),
                    f.getDeptCode(),
                    f.getEmail(),
                    f.getPhone(),
                    f.getStatus()
            });
        }
    }

    private void openAddFacultyDialog() {
        JDialog dlg = new JDialog(this, "Add Faculty Member", true);
        dlg.setSize(440, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField codeF = new JTextField(16);
        JTextField nameF = new JTextField(16);
        JTextField desigF = new JTextField("Assistant Professor", 16);
        JTextField emailF = new JTextField(16);
        JTextField phoneF = new JTextField(16);
        JComboBox<Department> deptF = new JComboBox<>();
        for (Department d : subjectService.getAllDepartments()) deptF.addItem(d);
        JPasswordField passF = new JPasswordField("Faculty@123", 16);

        addFormField(form, gbc, 0, "Faculty Code *:", codeF);
        addFormField(form, gbc, 1, "Full Name *:", nameF);
        addFormField(form, gbc, 2, "Designation *:", desigF);
        addFormField(form, gbc, 3, "Department *:", deptF);
        addFormField(form, gbc, 4, "Email *:", emailF);
        addFormField(form, gbc, 5, "Phone:", phoneF);
        addFormField(form, gbc, 6, "Default Password *:", passF);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UITheme.createPrimaryButton("Save Faculty");
        saveBtn.addActionListener(e -> {
            Department d = (Department) deptF.getSelectedItem();
            if (d == null) return;
            Faculty f = new Faculty();
            f.setFacultyCode(codeF.getText().trim());
            f.setFullName(nameF.getText().trim());
            f.setDesignation(desigF.getText().trim());
            f.setDeptId(d.getDeptId());
            f.setEmail(emailF.getText().trim());
            f.setPhone(phoneF.getText().trim());
            f.setStatus("ACTIVE");

            boolean ok = facultyService.createFaculty(f, new String(passF.getPassword()));
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Faculty created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadFaculty();
                refreshOverviewStats();
            } else {
                JOptionPane.showMessageDialog(dlg, "Failed to create faculty. Verify Code & Email are unique.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton closeBtn = UITheme.createSecondaryButton("Cancel");
        closeBtn.addActionListener(e -> dlg.dispose());

        btnP.add(saveBtn);
        btnP.add(closeBtn);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void openAssignSubjectDialog() {
        int row = facultyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a faculty member from the table first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int facId = (int) facultyModel.getValueAt(row, 0);
        String facName = (String) facultyModel.getValueAt(row, 2);

        JDialog dlg = new JDialog(this, "Assign Subject to " + facName, true);
        dlg.setSize(480, 260);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        JComboBox<Subject> subCombo = new JComboBox<>();
        for (Subject s : subjectService.getAllSubjects()) subCombo.addItem(s);
        JTextField sessionF = new JTextField("2025-26", 12);
        JComboBox<Integer> semF = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});

        addFormField(form, gbc, 0, "Select Subject *:", subCombo);
        addFormField(form, gbc, 1, "Academic Session *:", sessionF);
        addFormField(form, gbc, 2, "Semester *:", semF);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UITheme.createPrimaryButton("Assign Subject");
        saveBtn.addActionListener(e -> {
            Subject s = (Subject) subCombo.getSelectedItem();
            if (s == null) return;
            boolean ok = facultyService.assignSubject(facId, s.getSubjectId(), sessionF.getText().trim(), (Integer) semF.getSelectedItem());
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Subject assigned to faculty successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
            } else {
                JOptionPane.showMessageDialog(dlg, "Assignment already exists or error occurred.", "Notice", JOptionPane.WARNING_MESSAGE);
            }
        });
        JButton closeBtn = UITheme.createSecondaryButton("Cancel");
        closeBtn.addActionListener(e -> dlg.dispose());

        btnP.add(saveBtn);
        btnP.add(closeBtn);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void deleteSelectedFaculty() {
        int row = facultyTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a faculty member to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int facId = (int) facultyModel.getValueAt(row, 0);
        String name = (String) facultyModel.getValueAt(row, 2);
        int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete faculty: " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            facultyService.deleteFaculty(facId);
            loadFaculty();
            refreshOverviewStats();
        }
    }

    // =========================================================================
    // MODULE 5: SUBJECT MANAGEMENT
    // =========================================================================
    private JPanel createSubjectTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        String[] cols = {"ID", "Code", "Subject Name", "Dept", "Sem", "Credits", "Type", "Max CA", "Max Final", "Total"};
        subjectModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        subjectTable = new JTable(subjectModel);
        subjectTable.setFont(UITheme.FONT_REGULAR);
        subjectTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        panel.add(new JScrollPane(subjectTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        btnPanel.setBackground(UITheme.BG_MAIN);

        JButton addBtn = UITheme.createSuccessButton("+ Add Subject");
        addBtn.addActionListener(e -> openAddSubjectDialog());

        JButton delBtn = UITheme.createDangerButton("Delete Subject");
        delBtn.addActionListener(e -> deleteSelectedSubject());

        btnPanel.add(addBtn);
        btnPanel.add(delBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadSubjects() {
        subjectModel.setRowCount(0);
        List<Subject> list = subjectService.getAllSubjects();
        for (Subject s : list) {
            subjectModel.addRow(new Object[]{
                    s.getSubjectId(),
                    s.getSubjectCode(),
                    s.getSubjectName(),
                    s.getDeptCode(),
                    s.getSemesterNumber(),
                    s.getCredits(),
                    s.getCourseType(),
                    s.getMaxCaMarks(),
                    s.getMaxFinalMarks(),
                    s.getMaxTotalMarks()
            });
        }
    }

    private void openAddSubjectDialog() {
        JDialog dlg = new JDialog(this, "Add New Subject", true);
        dlg.setSize(460, 440);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField codeF = new JTextField(14);
        JTextField nameF = new JTextField(14);
        JComboBox<Department> deptF = new JComboBox<>();
        for (Department d : subjectService.getAllDepartments()) deptF.addItem(d);
        JComboBox<Integer> semF = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        JTextField credF = new JTextField("3.0", 6);
        JComboBox<String> typeF = new JComboBox<>(new String[]{"THEORY", "PRACTICAL", "SESSIONAL", "MANDATORY"});
        JTextField caF = new JTextField("25.0", 6);
        JTextField finalF = new JTextField("70.0", 6);

        typeF.addActionListener(e -> {
            String t = (String) typeF.getSelectedItem();
            if ("PRACTICAL".equalsIgnoreCase(t)) {
                caF.setText("40.0");
                finalF.setText("60.0");
            } else {
                caF.setText("25.0");
                finalF.setText("70.0");
            }
        });

        addFormField(form, gbc, 0, "Subject Code *:", codeF);
        addFormField(form, gbc, 1, "Subject Name *:", nameF);
        addFormField(form, gbc, 2, "Department *:", deptF);
        addFormField(form, gbc, 3, "Semester *:", semF);
        addFormField(form, gbc, 4, "Credits *:", credF);
        addFormField(form, gbc, 5, "Course Type *:", typeF);
        addFormField(form, gbc, 6, "Max Continuous Assessment:", caF);
        addFormField(form, gbc, 7, "Max Final Exam Marks:", finalF);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UITheme.createPrimaryButton("Save Subject");
        saveBtn.addActionListener(e -> {
            Department d = (Department) deptF.getSelectedItem();
            if (d == null) return;
            try {
                Subject s = new Subject();
                s.setSubjectCode(codeF.getText().trim().toUpperCase());
                s.setSubjectName(nameF.getText().trim());
                s.setDeptId(d.getDeptId());
                s.setSemesterNumber((Integer) semF.getSelectedItem());
                s.setCredits(Double.parseDouble(credF.getText().trim()));
                s.setCourseType((String) typeF.getSelectedItem());
                s.setMaxCaMarks(Double.parseDouble(caF.getText().trim()));
                s.setMaxFinalMarks(Double.parseDouble(finalF.getText().trim()));
                s.setMaxTotalMarks(100.0);
                s.setStatus("ACTIVE");

                boolean ok = subjectService.createSubject(s);
                if (ok) {
                    JOptionPane.showMessageDialog(dlg, "Subject added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dlg.dispose();
                    loadSubjects();
                    refreshOverviewStats();
                } else {
                    JOptionPane.showMessageDialog(dlg, "Failed to add subject. Ensure subject code is unique.", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dlg, "Invalid number format: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton closeBtn = UITheme.createSecondaryButton("Cancel");
        closeBtn.addActionListener(e -> dlg.dispose());

        btnP.add(saveBtn);
        btnP.add(closeBtn);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void deleteSelectedSubject() {
        int row = subjectTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a subject to delete.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int subId = (int) subjectModel.getValueAt(row, 0);
        String name = (String) subjectModel.getValueAt(row, 2);
        int opt = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete subject: " + name + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (opt == JOptionPane.YES_OPTION) {
            subjectService.deleteSubject(subId);
            loadSubjects();
            refreshOverviewStats();
        }
    }

    // =========================================================================
    // MODULE 6: EXAMINATION MANAGEMENT
    // =========================================================================
    private JPanel createExamTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        String[] cols = {"ID", "Exam Code", "Examination Name", "Session", "Sem", "Type", "Status", "Published?"};
        examModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        examTable = new JTable(examModel);
        examTable.setFont(UITheme.FONT_REGULAR);
        examTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        panel.add(new JScrollPane(examTable), BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        btnPanel.setBackground(UITheme.BG_MAIN);

        JButton addBtn = UITheme.createSuccessButton("+ Create Examination");
        addBtn.addActionListener(e -> openCreateExamDialog());

        JButton togglePubBtn = UITheme.createPrimaryButton("Publish / Unpublish Results");
        togglePubBtn.addActionListener(e -> toggleExamPublication());

        btnPanel.add(addBtn);
        btnPanel.add(togglePubBtn);
        panel.add(btnPanel, BorderLayout.SOUTH);

        return panel;
    }

    private void loadExaminations() {
        examModel.setRowCount(0);
        List<Examination> list = examService.getAllExaminations();
        for (Examination ex : list) {
            examModel.addRow(new Object[]{
                    ex.getExamId(),
                    ex.getExamCode(),
                    ex.getExamName(),
                    ex.getAcademicSession(),
                    ex.getSemesterNumber(),
                    ex.getExamType(),
                    ex.getStatus(),
                    ex.isPublished() ? "YES" : "NO"
            });
        }
    }

    private void openCreateExamDialog() {
        JDialog dlg = new JDialog(this, "Create New Examination Record", true);
        dlg.setSize(440, 320);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField codeF = new JTextField(14);
        JTextField nameF = new JTextField(16);
        JTextField sessionF = new JTextField("2025-26", 12);
        JComboBox<Integer> semF = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        JComboBox<String> typeF = new JComboBox<>(new String[]{"REGULAR", "BACKLOG"});

        addFormField(form, gbc, 0, "Exam Code *:", codeF);
        addFormField(form, gbc, 1, "Exam Name *:", nameF);
        addFormField(form, gbc, 2, "Academic Session *:", sessionF);
        addFormField(form, gbc, 3, "Semester *:", semF);
        addFormField(form, gbc, 4, "Exam Type *:", typeF);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UITheme.createPrimaryButton("Create Exam");
        saveBtn.addActionListener(e -> {
            Examination ex = new Examination();
            ex.setExamCode(codeF.getText().trim().toUpperCase());
            ex.setExamName(nameF.getText().trim());
            ex.setAcademicSession(sessionF.getText().trim());
            ex.setSemesterNumber((Integer) semF.getSelectedItem());
            ex.setExamType((String) typeF.getSelectedItem());
            ex.setStatus("SCHEDULED");
            ex.setPublished(false);

            boolean ok = examService.createExamination(ex);
            if (ok) {
                JOptionPane.showMessageDialog(dlg, "Examination created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadExaminations();
                refreshOverviewStats();
            } else {
                JOptionPane.showMessageDialog(dlg, "Failed to create examination. Check for unique code.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton closeBtn = UITheme.createSecondaryButton("Cancel");
        closeBtn.addActionListener(e -> dlg.dispose());

        btnP.add(saveBtn);
        btnP.add(closeBtn);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    private void toggleExamPublication() {
        int row = examTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an examination from the table.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int examId = (int) examModel.getValueAt(row, 0);
        String name = (String) examModel.getValueAt(row, 2);
        String pubStr = (String) examModel.getValueAt(row, 7);
        boolean currentlyPub = "YES".equalsIgnoreCase(pubStr);

        if (!currentlyPub) {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Publish results for: " + name + "?\nStudents will immediately be able to view and download their Provisional Grade Cards.",
                    "Confirm Publication", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                resultService.publishResults(examId, currentUser.getUserId(), "Official result publication by Administrator");
                JOptionPane.showMessageDialog(this, "Results published successfully!", "Published", JOptionPane.INFORMATION_MESSAGE);
                loadExaminations();
                refreshOverviewStats();
            }
        } else {
            int opt = JOptionPane.showConfirmDialog(this,
                    "Unpublish results for: " + name + "?\nResults will be temporarily hidden from students.",
                    "Confirm Unpublish", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (opt == JOptionPane.YES_OPTION) {
                examService.updatePublishStatus(examId, false);
                JOptionPane.showMessageDialog(this, "Results unpublished.", "Notice", JOptionPane.INFORMATION_MESSAGE);
                loadExaminations();
                refreshOverviewStats();
            }
        }
    }

    // =========================================================================
    // MODULE 8 & 9: RESULT PROCESSING & MANAGEMENT
    // =========================================================================
    private JPanel createResultTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 6));

        topBar.add(new JLabel("Select Examination:"));
        resultExamCombo = new JComboBox<>();
        resultExamCombo.addActionListener(e -> loadResultsForSelectedExam());
        topBar.add(resultExamCombo);

        JButton procAllBtn = UITheme.createPrimaryButton("Process / Recalculate All Results");
        procAllBtn.addActionListener(e -> processResultsForSelectedExam());
        topBar.add(procAllBtn);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Roll No", "Student Name", "Total Marks", "Max Marks", "Percentage", "Credits", "Credit Points", "SGPA", "YGPA", "CGPA", "Status", "Backlogs"};
        resultModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        resultTable = new JTable(resultModel);
        resultTable.setFont(UITheme.FONT_REGULAR);
        resultTable.getTableHeader().setFont(UITheme.FONT_BOLD);

        DefaultTableCellRenderer centerRend = new DefaultTableCellRenderer();
        centerRend.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 2; i < cols.length; i++) {
            resultTable.getColumnModel().getColumn(i).setCellRenderer(centerRend);
        }

        panel.add(new JScrollPane(resultTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 6));
        bot.setBackground(UITheme.BG_MAIN);

        JButton viewCardBtn = UITheme.createPrimaryButton("View Grade Card");
        viewCardBtn.addActionListener(e -> {
            tabbedPane.setSelectedIndex(8); // Switch to report tab
        });

        bot.add(viewCardBtn);
        panel.add(bot, BorderLayout.SOUTH);

        return panel;
    }

    private void loadResultsForSelectedExam() {
        Examination ex = (Examination) resultExamCombo.getSelectedItem();
        resultModel.setRowCount(0);
        if (ex == null) return;

        List<Result> results = resultService.getAllResultsByExam(ex.getExamId());
        for (Result r : results) {
            resultModel.addRow(new Object[]{
                    r.getRollNo(),
                    r.getStudentName(),
                    r.getTotalMarks(),
                    r.getMaxMarks(),
                    String.format("%.2f%%", r.getPercentage()),
                    String.format("%.1f", r.getTotalCredits()),
                    String.format("%.1f", r.getTotalCreditPoints()),
                    String.format("%.2f", r.getSgpa()),
                    r.getYgpa() != null ? String.format("%.2f", r.getYgpa()) : "-",
                    r.getCgpa() != null ? String.format("%.2f", r.getCgpa()) : "-",
                    r.getResultStatus(),
                    r.getBacklogCount()
            });
        }
    }

    private void processResultsForSelectedExam() {
        Examination ex = (Examination) resultExamCombo.getSelectedItem();
        if (ex == null) return;

        try {
            int count = resultService.processAllResultsForExam(ex.getExamId(), currentUser.getUserId());
            JOptionPane.showMessageDialog(this,
                    "Processed academic results for " + count + " students according to MAKAUT rules!",
                    "Batch Processing Complete", JOptionPane.INFORMATION_MESSAGE);
            loadResultsForSelectedExam();
            refreshOverviewStats();
        } catch (Exception ex2) {
            JOptionPane.showMessageDialog(this, "Error processing results: " + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // MODULE 12: RESULT CORRECTION & AUDIT TRAIL
    // =========================================================================
    private JPanel createAuditTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 6));

        topBar.add(new JLabel("Examination:"));
        auditExamCombo = new JComboBox<>();
        auditExamCombo.addActionListener(e -> loadAuditHistory());
        topBar.add(auditExamCombo);

        JButton correctMarksBtn = UITheme.createPrimaryButton("+ Make Authorized Marks Correction");
        correctMarksBtn.addActionListener(e -> openCorrectionDialog());
        topBar.add(correctMarksBtn);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Timestamp", "Student Roll", "Name", "Subject", "Field Changed", "Old Value", "New Value", "Modified By", "Reason for Correction"};
        auditModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        auditTable = new JTable(auditModel);
        auditTable.setFont(UITheme.FONT_REGULAR);
        auditTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        auditTable.getColumnModel().getColumn(0).setPreferredWidth(130);
        auditTable.getColumnModel().getColumn(8).setPreferredWidth(250);

        panel.add(new JScrollPane(auditTable), BorderLayout.CENTER);
        return panel;
    }

    private void loadAuditHistory() {
        auditModel.setRowCount(0);
        Examination ex = (Examination) auditExamCombo.getSelectedItem();
        if (ex == null) return;

        List<ResultHistory> list = resultService.getResultHistory(ex.getExamId(), null);
        for (ResultHistory h : list) {
            auditModel.addRow(new Object[]{
                    h.getModifiedAt(),
                    h.getRollNo(),
                    h.getStudentName(),
                    h.getSubjectCode() != null ? h.getSubjectCode() : "-",
                    h.getFieldChanged(),
                    h.getOldValue(),
                    h.getNewValue(),
                    h.getModifiedByUsername() != null ? h.getModifiedByUsername() : "Admin",
                    h.getReason()
            });
        }
    }

    private void openCorrectionDialog() {
        Examination ex = (Examination) auditExamCombo.getSelectedItem();
        if (ex == null) return;

        JDialog dlg = new JDialog(this, "Authorized Marks Correction & Result Recalculation", true);
        dlg.setSize(520, 420);
        dlg.setLocationRelativeTo(this);
        dlg.setLayout(new BorderLayout());

        JPanel form = new JPanel(new GridBagLayout());
        form.setBorder(new EmptyBorder(16, 20, 16, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        JTextField rollF = new JTextField(14);
        JComboBox<Subject> subCombo = new JComboBox<>();
        for (Subject s : subjectService.getSubjectsBySemester(ex.getSemesterNumber(), null)) {
            subCombo.addItem(s);
        }
        JTextField caF = new JTextField(8);
        JTextField finalF = new JTextField(8);
        JTextField reasonF = new JTextField(18);

        addFormField(form, gbc, 0, "Student Roll Number *:", rollF);
        addFormField(form, gbc, 1, "Subject *:", subCombo);
        addFormField(form, gbc, 2, "New Continuous Assessment (CA):", caF);
        addFormField(form, gbc, 3, "New Final Exam Marks:", finalF);
        addFormField(form, gbc, 4, "Mandatory Reason for Correction *:", reasonF);

        dlg.add(form, BorderLayout.CENTER);

        JPanel btnP = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        JButton saveBtn = UITheme.createPrimaryButton("Save & Recalculate Result");
        saveBtn.addActionListener(e -> {
            String roll = rollF.getText().trim();
            String reason = reasonF.getText().trim();
            if (reason.isEmpty()) {
                JOptionPane.showMessageDialog(dlg, "Reason for correction is required by MAKAUT regulations.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Student stu = studentService.getStudentByRollNo(roll);
            if (stu == null) {
                JOptionPane.showMessageDialog(dlg, "Student not found with Roll Number: " + roll, "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            Subject sub = (Subject) subCombo.getSelectedItem();
            if (sub == null) return;

            try {
                double ca = Double.parseDouble(caF.getText().trim());
                double fin = Double.parseDouble(finalF.getText().trim());

                Marks m = new Marks();
                m.setStudentId(stu.getStudentId());
                m.setSubjectId(sub.getSubjectId());
                m.setExamId(ex.getExamId());
                m.setCaMarks(ca);
                m.setFinalMarks(fin);
                m.setSubmitted(true);

                // Save marks with audit trail
                marksService.saveMarks(m, currentUser.getUserId(), reason);

                // Automatically recalculate semester result
                resultService.calculateAndSaveStudentResult(stu.getStudentId(), ex.getExamId(), currentUser.getUserId(), "Recalculated following marks correction: " + reason);

                JOptionPane.showMessageDialog(dlg, "Marks corrected and semester result recalculated successfully!\nAudit entry logged.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dlg.dispose();
                loadAuditHistory();
                loadResultsForSelectedExam();
            } catch (Exception ex3) {
                JOptionPane.showMessageDialog(dlg, "Error: " + ex3.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        JButton closeBtn = UITheme.createSecondaryButton("Cancel");
        closeBtn.addActionListener(e -> dlg.dispose());

        btnP.add(saveBtn);
        btnP.add(closeBtn);
        dlg.add(btnP, BorderLayout.SOUTH);
        dlg.setVisible(true);
    }

    // =========================================================================
    // MODULE 10: TOPPER RANKINGS & ANALYTICS
    // =========================================================================
    private JPanel createTopperTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 6));

        topBar.add(new JLabel("Examination Session:"));
        topperExamCombo = new JComboBox<>();
        topperExamCombo.addActionListener(e -> loadToppers());
        topBar.add(topperExamCombo);

        JButton refreshBtn = UITheme.createPrimaryButton("Refresh Rankings");
        refreshBtn.addActionListener(e -> loadToppers());
        topBar.add(refreshBtn);

        topperPassRateLabel = new JLabel("Pass: 0%");
        topperPassRateLabel.setFont(UITheme.FONT_BOLD);
        topperAvgSgpaLabel = new JLabel("Avg SGPA: 0.00");
        topperAvgSgpaLabel.setFont(UITheme.FONT_BOLD);

        topBar.add(Box.createHorizontalStrut(20));
        topBar.add(topperPassRateLabel);
        topBar.add(Box.createHorizontalStrut(10));
        topBar.add(topperAvgSgpaLabel);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Rank", "Roll Number", "Student Name", "Total Marks", "Percentage", "Credits", "Credit Points", "SGPA", "Result Status"};
        topperModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        topperTable = new JTable(topperModel);
        topperTable.setFont(UITheme.FONT_REGULAR);
        topperTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        panel.add(new JScrollPane(topperTable), BorderLayout.CENTER);

        return panel;
    }

    private void loadToppers() {
        topperModel.setRowCount(0);
        Examination ex = (Examination) topperExamCombo.getSelectedItem();
        if (ex == null) return;

        List<Result> toppers = resultService.getToppers(ex.getExamId(), 20);
        int rank = 1;
        for (Result r : toppers) {
            topperModel.addRow(new Object[]{
                    "#" + (rank++),
                    r.getRollNo(),
                    r.getStudentName(),
                    r.getTotalMarks(),
                    String.format("%.2f%%", r.getPercentage()),
                    String.format("%.1f", r.getTotalCredits()),
                    String.format("%.1f", r.getTotalCreditPoints()),
                    String.format("%.2f", r.getSgpa()),
                    r.getResultStatus()
            });
        }

        List<Result> all = resultService.getAllResultsByExam(ex.getExamId());
        if (!all.isEmpty()) {
            int passed = 0;
            double sumSgpa = 0.0;
            for (Result r : all) {
                if ("P".equalsIgnoreCase(r.getResultStatus())) passed++;
                sumSgpa += r.getSgpa();
            }
            topperPassRateLabel.setText(String.format("Pass Rate: %.1f%% (%d/%d)", (passed * 100.0) / all.size(), passed, all.size()));
            topperAvgSgpaLabel.setText(String.format("Class Avg SGPA: %.2f", sumSgpa / all.size()));
        }
    }

    // =========================================================================
    // MODULE 11 & 12: REPORTS & MARKSHEET GENERATOR
    // =========================================================================
    private JPanel createReportsTab() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(UITheme.BG_MAIN);
        panel.setBorder(new EmptyBorder(12, 16, 12, 16));

        JPanel topBar = UITheme.createCardPanel();
        topBar.setLayout(new FlowLayout(FlowLayout.LEFT, 14, 6));

        topBar.add(new JLabel("Examination:"));
        reportExamCombo = new JComboBox<>();
        reportExamCombo.addActionListener(e -> updateReportPreview());
        topBar.add(reportExamCombo);

        topBar.add(new JLabel("Student:"));
        reportStudentCombo = new JComboBox<>();
        reportStudentCombo.addActionListener(e -> updateReportPreview());
        topBar.add(reportStudentCombo);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Subject Code", "Subject Name", "Letter Grade", "Points", "Credit", "Credit Points"};
        reportPreviewModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        reportPreviewTable = new JTable(reportPreviewModel);
        reportPreviewTable.setFont(UITheme.FONT_REGULAR);
        reportPreviewTable.getTableHeader().setFont(UITheme.FONT_BOLD);
        panel.add(new JScrollPane(reportPreviewTable), BorderLayout.CENTER);

        JPanel bot = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 8));
        bot.setBackground(UITheme.BG_MAIN);

        JButton pdfBtn = UITheme.createPrimaryButton("Generate Official Provisional Grade Card (PDF)");
        pdfBtn.addActionListener(e -> generateMarksheetPdf());

        JButton csvBtn = UITheme.createSecondaryButton("Export Consolidated Exam Results (CSV)");
        csvBtn.addActionListener(e -> exportExamCsv());

        bot.add(pdfBtn);
        bot.add(csvBtn);
        panel.add(bot, BorderLayout.SOUTH);

        return panel;
    }

    private void updateReportPreview() {
        reportPreviewModel.setRowCount(0);
        Examination ex = (Examination) reportExamCombo.getSelectedItem();
        Student stu = (Student) reportStudentCombo.getSelectedItem();
        if (ex == null || stu == null) return;

        Result r = resultService.getResultByStudentAndExam(stu.getStudentId(), ex.getExamId());
        if (r == null) return;

        for (ResultItem item : r.getItems()) {
            reportPreviewModel.addRow(new Object[]{
                    item.getSubjectCode(),
                    item.getSubjectName(),
                    item.getLetterGrade(),
                    item.getPoints(),
                    String.format("%.1f", item.getCredit()),
                    String.format("%.1f", item.getCreditPoints())
            });
        }
    }

    private void generateMarksheetPdf() {
        Examination ex = (Examination) reportExamCombo.getSelectedItem();
        Student stu = (Student) reportStudentCombo.getSelectedItem();
        if (ex == null || stu == null) {
            JOptionPane.showMessageDialog(this, "Please select examination and student.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            File pdfFile = reportService.generateStudentMarksheetPdf(stu.getStudentId(), ex.getExamId(), "reports");
            int choice = JOptionPane.showConfirmDialog(this,
                    "Generated Provisional Grade Card PDF:\n" + pdfFile.getAbsolutePath() + "\n\nWould you like to open it?",
                    "PDF Generated", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE);

            if (choice == JOptionPane.YES_OPTION && Desktop.isDesktopSupported()) {
                Desktop.getDesktop().open(pdfFile);
            }
        } catch (Exception ex2) {
            JOptionPane.showMessageDialog(this, "Error generating PDF: " + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportExamCsv() {
        Examination ex = (Examination) reportExamCombo.getSelectedItem();
        if (ex == null) return;

        try {
            File csv = reportService.generateConsolidatedExamReportCsv(ex.getExamId(), "reports");
            JOptionPane.showMessageDialog(this, "Consolidated result exported to CSV:\n" + csv.getAbsolutePath(), "Export Complete", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception ex2) {
            JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex2.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    // =========================================================================
    // REFRESH & SYNCHRONIZATION HELPERS
    // =========================================================================
    private void refreshAllTabs() {
        refreshOverviewStats();
        populateDepartmentCombos();
        populateExamCombos();
        populateStudentCombos();
        loadStudents();
        loadFaculty();
        loadSubjects();
        loadExaminations();
    }

    private void refreshOverviewStats() {
        statStudentsLabel.setText(String.valueOf(studentService.getTotalStudentCount()));
        statFacultyLabel.setText(String.valueOf(facultyService.getTotalFacultyCount()));
        statSubjectsLabel.setText(String.valueOf(subjectService.getTotalSubjectCount()));
        statExamsLabel.setText(String.valueOf(examService.getTotalExamCount()));
        statPublishedLabel.setText(String.valueOf(examService.getPublishedExamCount()));
        statPendingMarksLabel.setText(String.valueOf(marksService.getPendingMarksCount()));
    }

    private void populateDepartmentCombos() {
        studentDeptCombo.removeAllItems();
        Department allDept = new Department(0, "ALL", "All Departments");
        studentDeptCombo.addItem(allDept);
        for (Department d : subjectService.getAllDepartments()) {
            studentDeptCombo.addItem(d);
        }
    }

    private void populateExamCombos() {
        List<Examination> exams = examService.getAllExaminations();

        resultExamCombo.removeAllItems();
        auditExamCombo.removeAllItems();
        topperExamCombo.removeAllItems();
        reportExamCombo.removeAllItems();

        for (Examination ex : exams) {
            resultExamCombo.addItem(ex);
            auditExamCombo.addItem(ex);
            topperExamCombo.addItem(ex);
            reportExamCombo.addItem(ex);
        }

        if (!exams.isEmpty()) {
            loadResultsForSelectedExam();
            loadAuditHistory();
            loadToppers();
        }
    }

    private void populateStudentCombos() {
        List<Student> students = studentService.getAllStudents();
        reportStudentCombo.removeAllItems();
        for (Student s : students) {
            reportStudentCombo.addItem(s);
        }
        updateReportPreview();
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0.35;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.65;
        panel.add(comp, gbc);
    }
}
