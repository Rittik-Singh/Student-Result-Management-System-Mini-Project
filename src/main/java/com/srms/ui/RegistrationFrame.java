package com.srms.ui;

import com.srms.model.Department;
import com.srms.service.AuthService;
import com.srms.service.SubjectService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

public class RegistrationFrame extends JFrame {

    private final AuthService authService = new AuthService();
    private final SubjectService subjectService = new SubjectService();

    private JTextField rollNoField;
    private JTextField regNoField;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<Department> deptComboBox;
    private JComboBox<Integer> semComboBox;
    private JPasswordField passField;
    private JPasswordField confirmPassField;

    public RegistrationFrame() {
        setTitle("Student Registration Portal - MAKAUT SRM System");
        setSize(560, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(18, 24, 18, 24));
        JLabel title = new JLabel("New Student Registration");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Enroll student profile to access published semester results and marksheets");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(new Color(220, 230, 245));
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        // Form
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BG_MAIN);
        formPanel.setBorder(new EmptyBorder(20, 24, 20, 24));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        rollNoField = new JTextField(20);
        regNoField = new JTextField(20);
        nameField = new JTextField(20);
        emailField = new JTextField(20);
        phoneField = new JTextField(20);

        deptComboBox = new JComboBox<>();
        List<Department> depts = subjectService.getAllDepartments();
        for (Department d : depts) {
            deptComboBox.addItem(d);
        }

        semComboBox = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});

        passField = new JPasswordField(20);
        confirmPassField = new JPasswordField(20);

        addFormField(formPanel, gbc, 0, "University Roll No *:", rollNoField);
        addFormField(formPanel, gbc, 1, "Registration No *:", regNoField);
        addFormField(formPanel, gbc, 2, "Full Name *:", nameField);
        addFormField(formPanel, gbc, 3, "Email Address *:", emailField);
        addFormField(formPanel, gbc, 4, "Mobile Number:", phoneField);
        addFormField(formPanel, gbc, 5, "Department *:", deptComboBox);
        addFormField(formPanel, gbc, 6, "Current Semester *:", semComboBox);
        addFormField(formPanel, gbc, 7, "Password (min 6 chars) *:", passField);
        addFormField(formPanel, gbc, 8, "Confirm Password *:", confirmPassField);

        add(new JScrollPane(formPanel), BorderLayout.CENTER);

        // Bottom Actions
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        btnPanel.setBackground(Color.WHITE);

        JButton registerBtn = UITheme.createSuccessButton("Register Account");
        registerBtn.addActionListener(e -> handleRegister());

        JButton cancelBtn = UITheme.createSecondaryButton("Back to Login");
        cancelBtn.addActionListener(e -> {
            new LoginFrame().setVisible(true);
            dispose();
        });

        btnPanel.add(registerBtn);
        btnPanel.add(cancelBtn);
        add(btnPanel, BorderLayout.SOUTH);
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

    private void handleRegister() {
        Department selDept = (Department) deptComboBox.getSelectedItem();
        if (selDept == null) {
            JOptionPane.showMessageDialog(this, "Please select a department.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            boolean ok = authService.registerStudent(
                    rollNoField.getText(),
                    regNoField.getText(),
                    nameField.getText(),
                    emailField.getText(),
                    phoneField.getText(),
                    selDept.getDeptId(),
                    (Integer) semComboBox.getSelectedItem(),
                    new String(passField.getPassword()),
                    new String(confirmPassField.getPassword())
            );

            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Registration Successful!\nYour student account has been created.\nYou can now login with your Roll Number: " + rollNoField.getText().trim(),
                        "Registration Success", JOptionPane.INFORMATION_MESSAGE);
                new LoginFrame().setVisible(true);
                dispose();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Registration Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
