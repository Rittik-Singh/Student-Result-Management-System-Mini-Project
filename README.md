# Student Result Management System (SRMS) — MAKAUT Grading Pattern

[![Java](https://img.shields.io/badge/Java-17%2B-orange.svg)](https://www.oracle.com/java/)
[![Maven](https://img.shields.io/badge/Build-Maven%203.9-blue.svg)](https://maven.apache.org/)
[![Database](https://img.shields.io/badge/Database-MySQL%20%7C%20Embedded%20H2-brightgreen.svg)](https://dev.mysql.com/)
[![UI](https://img.shields.io/badge/UI-Java%20Swing%20%2B%20FlatLaf-lightgrey.svg)](https://www.formdev.com/flatlaf/)
[![Report](https://img.shields.io/badge/PDF-OpenPDF%20Vector%20Grade%20Card-red.svg)](https://github.com/LibrePDF/OpenPDF)

A production-grade desktop application engineered for higher education institutions following the **Maulana Abul Kalam Azad University of Technology (MAKAUT, formerly WBUT)** B.Tech Choice Based Credit System (CBCS).

---

## 🚀 Key Features

* **Layered Clean Architecture**: Presentation (Java Swing + FlatLaf Light UI), Business Logic (Services), Data Access Objects (DAO with PreparedStatement security), Database (MySQL + H2 zero-config fallback).
* **Role-Based Access Control (RBAC)**:
  * **Administrator**: 9 full consoles for Students, Faculty, Subjects, Exams, Marks, Results, Topper Rankings, Audit Logs, and PDF Grade Cards.
  * **Faculty**: Subject marks entry, batch calculations, and status submission.
  * **Student**: Personal profile, semester-wise results, backlog tracking, and official PDF Provisional Grade Card download.
* **Exact MAKAUT Grading Rules**:
  * Letter Grades: $O$ (90–100, 10), $E$ (80–89, 9), $A$ (70–79, 8), $B$ (60–69, 7), $C$ (50–59, 6), $D$ (40–49, 5), $F$ (<40, 2), $I$ (Incomplete, 2).
  * Weighted SGPA, YGPA, DGPA, and CGPA calculations with Credit Points.
  * Promotion rules: $P$ (Passed), $XP$ (Promoted with Backlog), $X$ (Year Lag / Failed).
* **Vector PDF Grade Card Generator**: Generates high-fidelity 2-page Provisional Grade Cards matching authentic university marksheet layouts.
* **Smart Dual-Mode Database**: Connects directly to MySQL 8.x when available; seamlessly falls back to embedded H2 (MySQL Mode) out of the box with zero setup.

---

## ⚡ Quick Start

### 1. Launch with Pre-Compiled Fat JAR (Windows)
Double-click `run.bat` or run in terminal:
```cmd
run.bat
```
Alternatively:
```bash
java -jar target/student-result-management-system-1.0.0-jar-with-dependencies.jar
```

### 2. Build from Source
```bash
mvn clean package
```

---

## 🔑 Pre-Seeded Default Credentials

| Role | Username / Identifier | Password | Access / Scope |
| :--- | :--- | :--- | :--- |
| **Administrator** | `admin` | `Admin@123` | Full Institutional Management |
| **Faculty** | `dr.sharma` | `Faculty@123` | Department of Computer Science & Engineering |
| **Faculty** | `prof.ghosh` | `Faculty@123` | Department of CSE & Mathematics |
| **Student** | `27600124105` | `Student@123` | B.Tech CSE (Rittik Singh - Sems 1 to 4) |
| **Student** | `27600124106` | `Student@123` | B.Tech CSE (Priya Mukherjee) |
| **Student** | `27600124107` | `Student@123` | B.Tech CSE (Anirban Das) |

---

## 📂 Project Structure

```text
StudentResultManagementSystem/
├── database/
│   ├── schema.sql              # Relational schema (DDL, triggers, indexes)
│   └── sample_data.sql          # Seed data (Departments, Courses, Exams, Marks)
├── docs/
│   └── COMPREHENSIVE_DOCUMENTATION.md # Detailed 400+ line technical architecture
├── reports/                     # Output directory for generated PDF Grade Cards & CSVs
├── src/main/java/com/srms/
│   ├── dao/                     # JDBC DAOs & DatabaseInitializer
│   ├── model/                   # Domain entities
│   ├── service/                 # Business logic & calculation services
│   ├── ui/                      # Swing dashboards & authentication views
│   ├── util/                    # Grading, BCrypt, Validation, PDF generators
│   └── Main.java                # Application launcher
├── pom.xml                      # Maven project configuration
├── run.bat                      # One-click Windows runner
└── README.md                    # Project overview
```

---

## 📖 Comprehensive Documentation

For the full system specification, entity relationship diagrams, mathematical grading proofs, UI wireframes, and test audits, refer to:
* [COMPREHENSIVE_DOCUMENTATION.md](docs/COMPREHENSIVE_DOCUMENTATION.md)
