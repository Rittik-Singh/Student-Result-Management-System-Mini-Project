package com.srms.ui;

import com.srms.config.DatabaseConnection;
import com.srms.dao.DatabaseInitializer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConfigDialog extends JDialog {

    private JTextField hostField;
    private JTextField portField;
    private JTextField dbNameField;
    private JTextField userField;
    private JPasswordField passField;
    private JLabel statusLabel;
    private boolean configUpdated = false;

    public DatabaseConfigDialog(Frame owner) {
        super(owner, "Database Configuration & Connection Manager", true);
        setSize(480, 420);
        setLocationRelativeTo(owner);
        setLayout(new BorderLayout());

        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(UITheme.PRIMARY);
        headerPanel.setBorder(new EmptyBorder(14, 18, 14, 18));
        JLabel title = new JLabel("Database Settings");
        title.setFont(UITheme.FONT_TITLE);
        title.setForeground(Color.WHITE);
        JLabel subtitle = new JLabel("Configure MySQL Server or use the Embedded Offline Database");
        subtitle.setFont(UITheme.FONT_SMALL);
        subtitle.setForeground(new Color(220, 230, 245));
        headerPanel.add(title, BorderLayout.NORTH);
        headerPanel.add(subtitle, BorderLayout.SOUTH);
        add(headerPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(UITheme.BG_MAIN);
        formPanel.setBorder(new EmptyBorder(18, 20, 18, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        hostField = new JTextField(DatabaseConnection.getHost(), 18);
        portField = new JTextField(String.valueOf(DatabaseConnection.getPort()), 6);
        dbNameField = new JTextField(DatabaseConnection.getDbName(), 18);
        userField = new JTextField(DatabaseConnection.getUsername(), 18);
        passField = new JPasswordField(DatabaseConnection.getPassword(), 18);

        addFormField(formPanel, gbc, 0, "MySQL Host:", hostField);
        addFormField(formPanel, gbc, 1, "MySQL Port:", portField);
        addFormField(formPanel, gbc, 2, "Database Name:", dbNameField);
        addFormField(formPanel, gbc, 3, "Username:", userField);
        addFormField(formPanel, gbc, 4, "Password:", passField);

        statusLabel = new JLabel("Active: " + DatabaseConnection.getActiveDbInfo());
        statusLabel.setFont(UITheme.FONT_SMALL);
        statusLabel.setForeground(UITheme.SECONDARY);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(10, 5, 5, 5);
        formPanel.add(statusLabel, gbc);

        add(formPanel, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 12));
        btnPanel.setBackground(Color.WHITE);

        JButton testBtn = UITheme.createSecondaryButton("Test MySQL");
        testBtn.addActionListener(e -> testConnection());

        JButton saveBtn = UITheme.createPrimaryButton("Save & Connect MySQL");
        saveBtn.addActionListener(e -> applyConfig());

        JButton embeddedBtn = UITheme.createSuccessButton("Use Embedded Database");
        embeddedBtn.addActionListener(e -> useEmbedded());

        JButton closeBtn = UITheme.createSecondaryButton("Close");
        closeBtn.addActionListener(e -> dispose());

        btnPanel.add(testBtn);
        btnPanel.add(saveBtn);
        btnPanel.add(embeddedBtn);
        btnPanel.add(closeBtn);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String label, JComponent comp) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(label);
        lbl.setFont(UITheme.FONT_BOLD);
        panel.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        panel.add(comp, gbc);
    }

    private void testConnection() {
        String host = hostField.getText().trim();
        int port = Integer.parseInt(portField.getText().trim());
        String db = dbNameField.getText().trim();
        String user = userField.getText().trim();
        String pass = new String(passField.getPassword());

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            String url = "jdbc:mysql://" + host + ":" + port + "/" + db + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
            try (Connection conn = DriverManager.getConnection(url, user, pass)) {
                JOptionPane.showMessageDialog(this, "Success: Connected to MySQL database successfully!", "Connection Test", JOptionPane.INFORMATION_MESSAGE);
                statusLabel.setText("Active: MySQL (" + host + ":" + port + "/" + db + ")");
                statusLabel.setForeground(UITheme.SUCCESS);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Failed to connect to MySQL:\n" + ex.getMessage(), "Connection Error", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("MySQL connection failed: " + ex.getMessage());
            statusLabel.setForeground(UITheme.DANGER);
        }
    }

    private void applyConfig() {
        try {
            int port = Integer.parseInt(portField.getText().trim());
            DatabaseConnection.setConfig(
                    hostField.getText().trim(),
                    port,
                    dbNameField.getText().trim(),
                    userField.getText().trim(),
                    new String(passField.getPassword())
            );
            DatabaseInitializer.initializeDatabase();
            configUpdated = true;
            JOptionPane.showMessageDialog(this, "Connected and initialized MySQL database successfully!", "Configuration Saved", JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error applying configuration: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void useEmbedded() {
        DatabaseInitializer.initializeDatabase();
        configUpdated = true;
        JOptionPane.showMessageDialog(this, "Switched to Embedded Database mode (offline storage with full MAKAUT curriculum and sample records).", "Embedded Mode", JOptionPane.INFORMATION_MESSAGE);
        dispose();
    }

    public boolean isConfigUpdated() {
        return configUpdated;
    }
}
