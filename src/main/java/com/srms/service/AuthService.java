package com.srms.service;

import com.srms.dao.StudentDAO;
import com.srms.dao.UserDAO;
import com.srms.model.Student;
import com.srms.model.User;
import com.srms.util.PasswordUtil;
import com.srms.util.ValidationUtil;

public class AuthService {

    private final UserDAO userDAO = new UserDAO();
    private final StudentDAO studentDAO = new StudentDAO();

    public User login(String identifier, String password) throws Exception {
        if (!ValidationUtil.isNotEmpty(identifier)) {
            throw new IllegalArgumentException("Username, Roll Number, or Email is required.");
        }
        if (!ValidationUtil.isNotEmpty(password)) {
            throw new IllegalArgumentException("Password is required.");
        }

        User user = userDAO.findByUsernameOrEmail(identifier);
        if (user == null) {
            throw new IllegalArgumentException("No account found matching '" + identifier + "'.");
        }

        if (!user.isActive()) {
            throw new IllegalStateException("Your account status is " + user.getStatus() + ". Please contact administrator.");
        }

        boolean match = PasswordUtil.checkPassword(password, user.getPasswordHash());
        if (!match) {
            throw new IllegalArgumentException("Invalid password. Please check your credentials.");
        }

        return user;
    }

    public boolean registerStudent(String rollNo, String regNo, String fullName, String email,
                                   String phone, int deptId, int semester, String password, String confirmPassword) throws Exception {
        if (!ValidationUtil.isValidRollNumber(rollNo)) {
            throw new IllegalArgumentException("Invalid Roll Number format.");
        }
        if (!ValidationUtil.isNotEmpty(regNo)) {
            throw new IllegalArgumentException("Registration Number is required.");
        }
        if (!ValidationUtil.isNotEmpty(fullName)) {
            throw new IllegalArgumentException("Full Name is required.");
        }
        if (!ValidationUtil.isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid Email address.");
        }
        if (!ValidationUtil.isValidPassword(password)) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!password.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        // Check if user/student exists
        if (userDAO.findByUsernameOrEmail(rollNo) != null) {
            throw new IllegalArgumentException("An account with Roll Number " + rollNo + " already exists.");
        }
        if (userDAO.findByUsernameOrEmail(email) != null) {
            throw new IllegalArgumentException("An account with Email " + email + " already exists.");
        }
        if (studentDAO.findByRollNo(rollNo) != null) {
            throw new IllegalArgumentException("Student with Roll Number " + rollNo + " is already registered.");
        }

        Student student = new Student();
        student.setRollNo(rollNo.trim());
        student.setRegistrationNo(regNo.trim());
        student.setFullName(fullName.trim());
        student.setEmail(email.trim());
        student.setPhone(phone != null ? phone.trim() : "");
        student.setDeptId(deptId);
        student.setCurrentSemester(semester);
        student.setCollegeName("276-BUDGE BUDGE INSTITUTE OF TECHNOLOGY");
        student.setProgramName("BACHELOR OF TECHNOLOGY IN COMPUTER SCIENCE & ENGINEERING");
        student.setAdmissionYear(2024);

        return studentDAO.createStudentWithUser(student, password);
    }

    public boolean resetPassword(String identifier, String newPassword, String confirmPassword) throws Exception {
        if (!ValidationUtil.isNotEmpty(identifier)) {
            throw new IllegalArgumentException("Username, Roll Number, or Email is required.");
        }
        if (!ValidationUtil.isValidPassword(newPassword)) {
            throw new IllegalArgumentException("Password must be at least 6 characters long.");
        }
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("Passwords do not match.");
        }

        User user = userDAO.findByUsernameOrEmail(identifier);
        if (user == null) {
            throw new IllegalArgumentException("No account found for: " + identifier);
        }

        String newHash = PasswordUtil.hashPassword(newPassword);
        return userDAO.updatePassword(user.getUserId(), newHash);
    }
}
