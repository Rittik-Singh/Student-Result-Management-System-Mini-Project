-- Student Result Management System (MAKAUT Grading Pattern)
-- Database Schema Definition (Compatible with MySQL 8.0+ and H2 MySQL Mode)

CREATE TABLE IF NOT EXISTS departments (
    dept_id INT AUTO_INCREMENT PRIMARY KEY,
    dept_code VARCHAR(20) NOT NULL UNIQUE,
    dept_name VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS semesters (
    semester_id INT AUTO_INCREMENT PRIMARY KEY,
    semester_number INT NOT NULL UNIQUE,
    semester_name VARCHAR(50) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    user_id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL, -- 'ADMIN', 'FACULTY', 'STUDENT'
    email VARCHAR(150) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE', -- 'ACTIVE', 'INACTIVE', 'BLOCKED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS students (
    student_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    roll_no VARCHAR(50) NOT NULL UNIQUE,
    registration_no VARCHAR(80) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    dept_id INT NOT NULL,
    current_semester INT NOT NULL DEFAULT 1,
    college_name VARCHAR(200) NOT NULL DEFAULT '276-BUDGE BUDGE INSTITUTE OF TECHNOLOGY',
    program_name VARCHAR(200) NOT NULL DEFAULT 'BACHELOR OF TECHNOLOGY IN COMPUTER SCIENCE & ENGINEERING',
    admission_year INT NOT NULL DEFAULT 2024,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

CREATE TABLE IF NOT EXISTS faculty (
    faculty_id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT UNIQUE,
    faculty_code VARCHAR(50) NOT NULL UNIQUE,
    full_name VARCHAR(150) NOT NULL,
    email VARCHAR(150) NOT NULL,
    phone VARCHAR(20),
    dept_id INT NOT NULL,
    designation VARCHAR(100) NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE SET NULL,
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

CREATE TABLE IF NOT EXISTS subjects (
    subject_id INT AUTO_INCREMENT PRIMARY KEY,
    subject_code VARCHAR(50) NOT NULL UNIQUE,
    subject_name VARCHAR(200) NOT NULL,
    dept_id INT NOT NULL,
    semester_number INT NOT NULL,
    credits DECIMAL(3,1) NOT NULL,
    course_type VARCHAR(30) NOT NULL DEFAULT 'THEORY', -- 'THEORY', 'PRACTICAL', 'SESSIONAL', 'MANDATORY'
    max_ca_marks DECIMAL(5,2) NOT NULL DEFAULT 25.0,
    max_final_marks DECIMAL(5,2) NOT NULL DEFAULT 70.0,
    max_total_marks DECIMAL(5,2) NOT NULL DEFAULT 100.0,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    FOREIGN KEY (dept_id) REFERENCES departments(dept_id)
);

CREATE TABLE IF NOT EXISTS examinations (
    exam_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_code VARCHAR(50) NOT NULL UNIQUE,
    exam_name VARCHAR(200) NOT NULL,
    academic_session VARCHAR(50) NOT NULL,
    semester_number INT NOT NULL,
    exam_type VARCHAR(20) NOT NULL DEFAULT 'REGULAR', -- 'REGULAR', 'BACKLOG'
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    published_at TIMESTAMP NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'SCHEDULED', -- 'SCHEDULED', 'MARKS_ENTRY', 'PROCESSED', 'PUBLISHED', 'CLOSED'
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS faculty_subjects (
    id INT AUTO_INCREMENT PRIMARY KEY,
    faculty_id INT NOT NULL,
    subject_id INT NOT NULL,
    academic_session VARCHAR(50) NOT NULL,
    semester_number INT NOT NULL,
    FOREIGN KEY (faculty_id) REFERENCES faculty(faculty_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
    UNIQUE (faculty_id, subject_id, academic_session)
);

CREATE TABLE IF NOT EXISTS marks (
    marks_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    subject_id INT NOT NULL,
    exam_id INT NOT NULL,
    ca_marks DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    final_marks DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    total_marks DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    letter_grade VARCHAR(5) NOT NULL DEFAULT 'F',
    grade_point INT NOT NULL DEFAULT 2,
    credit_points DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    is_submitted BOOLEAN NOT NULL DEFAULT FALSE,
    entered_by_faculty_id INT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (subject_id) REFERENCES subjects(subject_id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES examinations(exam_id) ON DELETE CASCADE,
    UNIQUE (student_id, subject_id, exam_id)
);

CREATE TABLE IF NOT EXISTS results (
    result_id INT AUTO_INCREMENT PRIMARY KEY,
    student_id INT NOT NULL,
    exam_id INT NOT NULL,
    semester_number INT NOT NULL,
    total_marks DECIMAL(7,2) NOT NULL DEFAULT 0.0,
    max_marks DECIMAL(7,2) NOT NULL DEFAULT 0.0,
    percentage DECIMAL(5,2) NOT NULL DEFAULT 0.0,
    total_credits DECIMAL(4,1) NOT NULL DEFAULT 0.0,
    total_credit_points DECIMAL(7,2) NOT NULL DEFAULT 0.0,
    sgpa DECIMAL(4,2) NOT NULL DEFAULT 0.0,
    ygpa DECIMAL(4,2) NULL,
    cgpa DECIMAL(4,2) NULL,
    dgpa DECIMAL(4,2) NULL,
    result_status VARCHAR(20) NOT NULL DEFAULT 'P', -- 'P', 'XP', 'X'
    backlog_count INT NOT NULL DEFAULT 0,
    is_published BOOLEAN NOT NULL DEFAULT FALSE,
    calculated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (student_id) REFERENCES students(student_id) ON DELETE CASCADE,
    FOREIGN KEY (exam_id) REFERENCES examinations(exam_id) ON DELETE CASCADE,
    UNIQUE (student_id, exam_id)
);

CREATE TABLE IF NOT EXISTS result_history (
    history_id INT AUTO_INCREMENT PRIMARY KEY,
    result_id INT NULL,
    student_id INT NOT NULL,
    subject_id INT NULL,
    exam_id INT NOT NULL,
    field_changed VARCHAR(50) NOT NULL,
    old_value VARCHAR(100) NOT NULL,
    new_value VARCHAR(100) NOT NULL,
    modified_by_user_id INT NULL,
    modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    reason VARCHAR(500) NOT NULL
);

CREATE TABLE IF NOT EXISTS result_publications (
    publication_id INT AUTO_INCREMENT PRIMARY KEY,
    exam_id INT NOT NULL,
    published_by_user_id INT NULL,
    published_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total_students_processed INT NOT NULL DEFAULT 0,
    total_passed INT NOT NULL DEFAULT 0,
    total_backlog INT NOT NULL DEFAULT 0,
    total_failed INT NOT NULL DEFAULT 0,
    notes TEXT,
    FOREIGN KEY (exam_id) REFERENCES examinations(exam_id) ON DELETE CASCADE
);

CREATE INDEX idx_student_roll ON students(roll_no);
CREATE INDEX idx_marks_student_exam ON marks(student_id, exam_id);
CREATE INDEX idx_results_student_exam ON results(student_id, exam_id);
CREATE INDEX idx_results_exam_sgpa ON results(exam_id, sgpa DESC);
