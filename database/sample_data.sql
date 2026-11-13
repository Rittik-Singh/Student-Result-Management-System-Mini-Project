-- Student Result Management System (MAKAUT Grading Pattern)
-- Sample Seed Data for Departments, Semesters, and Subjects

-- Departments
INSERT INTO departments (dept_id, dept_code, dept_name) VALUES
(1, 'CSE', 'Computer Science and Engineering'),
(2, 'ECE', 'Electronics and Communication Engineering'),
(3, 'EE', 'Electrical Engineering'),
(4, 'ME', 'Mechanical Engineering'),
(5, 'CE', 'Civil Engineering'),
(6, 'IT', 'Information Technology');

-- Semesters
INSERT INTO semesters (semester_id, semester_number, semester_name) VALUES
(1, 1, 'First Year First Semester'),
(2, 2, 'First Year Second Semester'),
(3, 3, 'Second Year First Semester'),
(4, 4, 'Second Year Second Semester'),
(5, 5, 'Third Year First Semester'),
(6, 6, 'Third Year Second Semester'),
(7, 7, 'Fourth Year First Semester'),
(8, 8, 'Fourth Year Second Semester');

-- Subjects (Curriculum Mapping as per MAKAUT B.Tech CSE Syllabus)
-- Semester 1
INSERT INTO subjects (subject_code, subject_name, dept_id, semester_number, credits, course_type, max_ca_marks, max_final_marks, max_total_marks) VALUES
('BSPH101', 'Physics I (Gr A)', 1, 1, 4.0, 'THEORY', 25.0, 70.0, 100.0),
('BSM101', 'Mathematics - IA', 1, 1, 4.0, 'THEORY', 25.0, 70.0, 100.0),
('ESEE101', 'Basic Electrical Engineering', 1, 1, 4.0, 'THEORY', 25.0, 70.0, 100.0),
('BSPH191', 'Physics I Laboratory (Gr A)', 1, 1, 1.5, 'PRACTICAL', 40.0, 60.0, 100.0),
('ESEE191', 'Basic Electrical Engineering Laboratory', 1, 1, 1.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('ESME192', 'Workshop/Manufacturing Practices (Gr A)', 1, 1, 3.0, 'PRACTICAL', 40.0, 60.0, 100.0);

-- Semester 2
INSERT INTO subjects (subject_code, subject_name, dept_id, semester_number, credits, course_type, max_ca_marks, max_final_marks, max_total_marks) VALUES
('BSCH201', 'Chemistry-I (Gr-A)', 1, 2, 4.0, 'THEORY', 25.0, 70.0, 100.0),
('BSM201', 'Mathematics - IIA', 1, 2, 4.0, 'THEORY', 25.0, 70.0, 100.0),
('ESCS201', 'Programming for Problem Solving', 1, 2, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('HMHU201', 'English', 1, 2, 2.0, 'THEORY', 25.0, 70.0, 100.0),
('BSCH291', 'Chemistry-I Laboratory (Gr-A)', 1, 2, 1.5, 'PRACTICAL', 40.0, 60.0, 100.0),
('ESCS291', 'Programming for Problem Solving Lab', 1, 2, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('ESME291', 'Engineering Graphics & Design (Gr-A)', 1, 2, 3.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('HMHU291', 'Language Laboratory', 1, 2, 1.0, 'PRACTICAL', 40.0, 60.0, 100.0);

-- Semester 3
INSERT INTO subjects (subject_code, subject_name, dept_id, semester_number, credits, course_type, max_ca_marks, max_final_marks, max_total_marks) VALUES
('ESC301', 'Analog and Digital Electronics', 1, 3, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS301', 'Data Structure & Algorithms', 1, 3, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS302', 'Computer Organisation', 1, 3, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('BSC301', 'Mathematics-III (Differential Calculus)', 1, 3, 2.0, 'THEORY', 25.0, 70.0, 100.0),
('HSMC301', 'Economics for Engineers (Humanities-II)', 1, 3, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS393', 'IT Workshop (Sci Lab/MATLAB/Python/R)', 1, 3, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('ESC391', 'Analog and Digital Electronics Lab', 1, 3, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('PCC-CS391', 'Data Structure & Algorithms Lab', 1, 3, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('PCC-CS392', 'Computer Organisation Lab', 1, 3, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0);

-- Semester 4
INSERT INTO subjects (subject_code, subject_name, dept_id, semester_number, credits, course_type, max_ca_marks, max_final_marks, max_total_marks) VALUES
('PCC-CS401', 'Discrete Mathematics', 1, 4, 4.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS402', 'Computer Architecture', 1, 4, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS403', 'Formal Language & Automata Theory', 1, 4, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS404', 'Design & Analysis of Algorithms', 1, 4, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('BSC401', 'Biology', 1, 4, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('MC401', 'Environmental Sciences', 1, 4, 1.0, 'MANDATORY', 25.0, 70.0, 100.0),
('PCC-CS492', 'Computer Architecture Lab', 1, 4, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('PCC-CS494', 'Design & Analysis of Algorithms Lab', 1, 4, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0);

-- Semester 5
INSERT INTO subjects (subject_code, subject_name, dept_id, semester_number, credits, course_type, max_ca_marks, max_final_marks, max_total_marks) VALUES
('ESC501', 'Software Engineering', 1, 5, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS501', 'Compiler Design', 1, 5, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS502', 'Operating Systems', 1, 5, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS503', 'Object Oriented Programming', 1, 5, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('HSMC501', 'Introduction to Industrial Management', 1, 5, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PEC-IT501B', 'Artificial Intelligence', 1, 5, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('MC-CS501', 'Constitution of India', 1, 5, 0.0, 'MANDATORY', 25.0, 70.0, 100.0),
('ESC591', 'Software Engineering Lab', 1, 5, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('PCC-CS592', 'Operating System Lab', 1, 5, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('PCC-CS593', 'Object Oriented Programming Lab', 1, 5, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0);

-- Semester 6
INSERT INTO subjects (subject_code, subject_name, dept_id, semester_number, credits, course_type, max_ca_marks, max_final_marks, max_total_marks) VALUES
('PCC-CS601', 'Database Management Systems', 1, 6, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PCC-CS602', 'Computer Networks', 1, 6, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PEC-IT601B', 'Distributed Systems', 1, 6, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PEC-IT602D', 'Pattern Recognition', 1, 6, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('OEC-IT601A', 'Numerical Methods', 1, 6, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PROJ-CS601', 'Research Methodology', 1, 6, 3.0, 'SESSIONAL', 25.0, 70.0, 100.0),
('PCC-CS691', 'Database Management System Lab', 1, 6, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0),
('PCC-CS692', 'Computer Networks Lab', 1, 6, 2.0, 'PRACTICAL', 40.0, 60.0, 100.0);

-- Semester 7
INSERT INTO subjects (subject_code, subject_name, dept_id, semester_number, credits, course_type, max_ca_marks, max_final_marks, max_total_marks) VALUES
('PEC-CS701E', 'Machine Learning', 1, 7, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PEC-CS702B', 'Soft Computing', 1, 7, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('OEC-CS701A', 'Operation Research', 1, 7, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('HSMC701', 'Project Management and Entrepreneurship', 1, 7, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PROJ-CS781', 'Project-II', 1, 7, 6.0, 'SESSIONAL', 25.0, 70.0, 100.0);

-- Semester 8
INSERT INTO subjects (subject_code, subject_name, dept_id, semester_number, credits, course_type, max_ca_marks, max_final_marks, max_total_marks) VALUES
('PEC-CS801B', 'Cryptography and Network Security', 1, 8, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('OEC-CS801A', 'Big Data Analytics', 1, 8, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('OEC-CS802A', 'E-Commerce & ERP', 1, 8, 3.0, 'THEORY', 25.0, 70.0, 100.0),
('PROJ-CS881', 'Project-III', 1, 8, 6.0, 'SESSIONAL', 25.0, 70.0, 100.0);
