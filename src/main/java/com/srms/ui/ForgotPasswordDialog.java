package com.srms.ui;

import com.srms.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class ForgotPasswordDialog extends JDialog {

    private final AuthService authService = new AuthService();
    private JTextField identifierField;
    private JPasswordField newPasswordField;
    private JPasswordField confirmPasswordField;

    public ForgotPasswordDialog(Frame owner) {
        super(owner, "Password Reset & Account Recovery", true);
        setSize(420, 320);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(12, 16, 12, 16));
        JLabel title = new JLabel("Reset Account Password");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        headerPanel.add(title, BorderLayout.CENTER);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BG_MAIN);
        formPanel.setBorder(new EmptyBorder(16, 18, 16, 18));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(6, 6, 6, 6);

        identifierField = new JTextField(18);
        newPasswordField = new JPasswordField(18);
        confirmPasswordField = new JPasswordField(18);

        addFormField(formPanel, gbc, 0, "Roll No / Username / Email:", identifierField);
        addFormField(formPanel, gbc, 1, "New Password (min 6 chars):", newPasswordField);
        addFormField(formPanel, gbc, 2, "Confirm New Password:", confirmPasswordField);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        btnPanel.setBackground(Color.WHITE);

        JButton resetBtn = UITheme.createPrimaryButton("Reset Password");
        resetBtn.addActionListener(e -> handleReset());

        JButton cancelBtn = UITheme.createSecondaryButton("Cancel");
        cancelBtn.addActionListener(e -> dispose());

        btnPanel.add(resetBtn);
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

    private void handleReset() {
        String identifier = identifierField.getText().trim();
        String pass = new String(newPasswordField.getPassword());
        String confirm = new String(confirmPasswordField.getPassword());

        try {
            boolean success = authService.resetPassword(identifier, pass, confirm);
            if (success) {
                JOptionPane.showMessageDialog(this, "Password reset successfully! You can now log in with your new password.", "Success", JOptionPane.INFORMATION_MESSAGE);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Could not update password. Please check your details.", "Failed", JOptionPane.WARNING_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
