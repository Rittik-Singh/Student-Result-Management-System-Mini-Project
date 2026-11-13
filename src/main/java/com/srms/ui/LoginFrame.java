package com.srms.ui;

import com.srms.config.DatabaseConnection;
import com.srms.model.Faculty;
import com.srms.model.Student;
import com.srms.model.User;
import com.srms.service.AuthService;
import com.srms.service.FacultyService;
import com.srms.service.StudentService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final AuthService authService = new AuthService();
    private final StudentService studentService = new StudentService();
    private final FacultyService facultyService = new FacultyService();

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    private JLabel dbStatusLabel;

    public LoginFrame() {
        setTitle("Student Result Management System - MAKAUT Grading Pattern");
        setSize(520, 560);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(20, 24, 20, 24));

        JLabel titleLabel = new JLabel("STUDENT RESULT MANAGEMENT SYSTEM");
        titleLabel.setFont(UITheme.FONT_TITLE);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel subLabel = new JLabel("MAKAUT Grading Pattern (B.Tech CBCS Curriculum)");
        subLabel.setFont(UITheme.FONT_SMALL);
        subLabel.setForeground(new Color(220, 230, 245));
        subLabel.setHorizontalAlignment(SwingConstants.CENTER);

        headerPanel.add(titleLabel, BorderLayout.CENTER);
        headerPanel.add(subLabel, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Center Login Form
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setBackground(UITheme.BG_MAIN);
        centerPanel.setBorder(new EmptyBorder(16, 28, 16, 28));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        // Role Selector
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        JLabel roleLbl = new JLabel("Login Role:");
        roleLbl.setFont(UITheme.FONT_BOLD);
        centerPanel.add(roleLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        roleComboBox = new JComboBox<>(new String[]{"ADMIN", "FACULTY", "STUDENT"});
        roleComboBox.setFont(UITheme.FONT_REGULAR);
        centerPanel.add(roleComboBox, gbc);

        // Username
        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        JLabel userLbl = new JLabel("Username / Roll No:");
        userLbl.setFont(UITheme.FONT_BOLD);
        centerPanel.add(userLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        usernameField = new JTextField("admin", 18);
        usernameField.setFont(UITheme.FONT_REGULAR);
        centerPanel.add(usernameField, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.weightx = 0.3;
        JLabel passLbl = new JLabel("Password:");
        passLbl.setFont(UITheme.FONT_BOLD);
        centerPanel.add(passLbl, gbc);

        gbc.gridx = 1; gbc.weightx = 0.7;
        passwordField = new JPasswordField("Admin@123", 18);
        passwordField.setFont(UITheme.FONT_REGULAR);
        centerPanel.add(passwordField, gbc);

        // Quick Login Demo Autofill Buttons
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JPanel quickDemoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 6, 4));
        quickDemoPanel.setBackground(UITheme.BG_MAIN);
        quickDemoPanel.setBorder(BorderFactory.createTitledBorder("Quick Demo Autofill"));

        JButton adminFillBtn = new JButton("Admin");
        adminFillBtn.setFont(UITheme.FONT_SMALL);
        adminFillBtn.addActionListener(e -> {
            roleComboBox.setSelectedItem("ADMIN");
            usernameField.setText("admin");
            passwordField.setText("Admin@123");
        });

        JButton facultyFillBtn = new JButton("Faculty (CSE)");
        facultyFillBtn.setFont(UITheme.FONT_SMALL);
        facultyFillBtn.addActionListener(e -> {
            roleComboBox.setSelectedItem("FACULTY");
            usernameField.setText("dr.sharma");
            passwordField.setText("Faculty@123");
        });

        JButton studentFillBtn = new JButton("Student (Rittik)");
        studentFillBtn.setFont(UITheme.FONT_SMALL);
        studentFillBtn.addActionListener(e -> {
            roleComboBox.setSelectedItem("STUDENT");
            usernameField.setText("27600124105");
            passwordField.setText("Student@123");
        });

        quickDemoPanel.add(adminFillBtn);
        quickDemoPanel.add(facultyFillBtn);
        quickDemoPanel.add(studentFillBtn);
        centerPanel.add(quickDemoPanel, gbc);

        // Login Action Button
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 2;
        gbc.insets = new Insets(12, 6, 6, 6);
        JButton loginBtn = UITheme.createPrimaryButton("Secure Login");
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.addActionListener(e -> handleLogin());
        centerPanel.add(loginBtn, gbc);

        // Secondary Links: Register, Forgot Password
        gbc.gridx = 0; gbc.gridy = 5; gbc.gridwidth = 2;
        JPanel linksPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 14, 4));
        linksPanel.setBackground(UITheme.BG_MAIN);

        JButton regBtn = new JButton("New Student Registration");
        regBtn.setFont(UITheme.FONT_SMALL);
        regBtn.setForeground(UITheme.PRIMARY);
        regBtn.setBorderPainted(false);
        regBtn.setContentAreaFilled(false);
        regBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regBtn.addActionListener(e -> {
            new RegistrationFrame().setVisible(true);
            dispose();
        });

        JButton forgotBtn = new JButton("Forgot Password?");
        forgotBtn.setFont(UITheme.FONT_SMALL);
        forgotBtn.setForeground(UITheme.SECONDARY);
        forgotBtn.setBorderPainted(false);
        forgotBtn.setContentAreaFilled(false);
        forgotBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        forgotBtn.addActionListener(e -> new ForgotPasswordDialog(this).setVisible(true));

        linksPanel.add(regBtn);
        linksPanel.add(new JLabel("|"));
        linksPanel.add(forgotBtn);
        centerPanel.add(linksPanel, gbc);

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Status Bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.setBackground(new Color(241, 245, 249));
        statusPanel.setBorder(new EmptyBorder(8, 14, 8, 14));

        dbStatusLabel = new JLabel("DB: " + DatabaseConnection.getActiveDbInfo());
        dbStatusLabel.setFont(UITheme.FONT_SMALL);
        dbStatusLabel.setForeground(UITheme.SECONDARY);
        statusPanel.add(dbStatusLabel, BorderLayout.WEST);

        JButton dbSettingsBtn = new JButton("Database Settings");
        dbSettingsBtn.setFont(UITheme.FONT_SMALL);
        dbSettingsBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dbSettingsBtn.addActionListener(e -> {
            DatabaseConfigDialog dialog = new DatabaseConfigDialog(this);
            dialog.setVisible(true);
            updateDbStatus();
        });
        statusPanel.add(dbSettingsBtn, BorderLayout.EAST);

        add(statusPanel, BorderLayout.SOUTH);

        // Enter key to login
        getRootPane().setDefaultButton(loginBtn);
    }

    private void updateDbStatus() {
        dbStatusLabel.setText("DB: " + DatabaseConnection.getActiveDbInfo());
    }

    private void handleLogin() {
        String identifier = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String selectedRole = (String) roleComboBox.getSelectedItem();

        try {
            User user = authService.login(identifier, password);

            if (selectedRole != null && !selectedRole.equalsIgnoreCase(user.getRole())) {
                JOptionPane.showMessageDialog(this,
                        "Role Mismatch! Your account has role '" + user.getRole() + "', but you selected '" + selectedRole + "'.",
                        "Authentication Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Redirect based on role
            switch (user.getRole().toUpperCase()) {
                case "ADMIN" -> {
                    new AdminDashboard(user).setVisible(true);
                    dispose();
                }
                case "FACULTY" -> {
                    Faculty fac = facultyService.getFacultyByUserId(user.getUserId());
                    if (fac == null) {
                        JOptionPane.showMessageDialog(this, "Faculty profile record not found for user.", "Profile Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    new FacultyDashboard(user, fac).setVisible(true);
                    dispose();
                }
                case "STUDENT" -> {
                    Student stu = studentService.getStudentByUserId(user.getUserId());
                    if (stu == null) {
                        // Fallback check by roll_no = username
                        stu = studentService.getStudentByRollNo(user.getUsername());
                    }
                    if (stu == null) {
                        JOptionPane.showMessageDialog(this, "Student profile record not found for user.", "Profile Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    new StudentDashboard(user, stu).setVisible(true);
                    dispose();
                }
                default -> JOptionPane.showMessageDialog(this, "Unknown user role: " + user.getRole(), "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Login Failed", JOptionPane.ERROR_MESSAGE);
        }
    }
}
