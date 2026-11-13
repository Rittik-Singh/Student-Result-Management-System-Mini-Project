package com.srms;

import com.srms.dao.DatabaseInitializer;
import com.srms.ui.LoginFrame;
import com.srms.ui.UITheme;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {
        // Setup modern UI Theme
        UITheme.setupLookAndFeel();

        // Initialize Database Schema and MAKAUT Curriculum Seed Data
        System.out.println("===================================================================");
        System.out.println("   STUDENT RESULT MANAGEMENT SYSTEM (MAKAUT GRADING PATTERN)      ");
        System.out.println("   Powered by Java Swing & MySQL (with Offline Embedded Fallback)  ");
        System.out.println("===================================================================");
        DatabaseInitializer.initializeDatabase();

        // Launch Application
        SwingUtilities.invokeLater(() -> {
            LoginFrame loginFrame = new LoginFrame();
            loginFrame.setVisible(true);
        });
    }
}
