package com.amul.demo.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class StudentDataAccessService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public StudentDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    List<Student> selectAllStudents() {
        String sql = "" +
                "SELECT " +
                " student_id, " +
                " first_name, " +
                " last_name, " +
                " email, " +
                " gender " +
                "FROM student";

        return jdbcTemplate.query(sql, mapStudentFomDb());
    }

    int insertStudent(UUID studentId, Student student) {
        String sql = "" +
                "INSERT INTO student (" +
                " student_id, " +
                " first_name, " +
                " last_name, " +
                " email, " +
                " gender) " +
                "VALUES (?, ?, ?, ?, ?::gender)";
        return jdbcTemplate.update(
                sql,
                studentId,
                student.getFirstName(),
                student.getLastName(),
                student.getEmail(),
                student.getGender().name().toUpperCase()
        );
    }

    @SuppressWarnings("ConstantConditions")
    boolean isEmailTaken(String email) {
        String sql = "" +
                "SELECT EXISTS ( " +
                " SELECT 1 " +
                " FROM student " +
                " WHERE email = ?" +
                ")";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{email},
                Boolean.class // More concise way to get a boolean
        );
    }

    @SuppressWarnings("ConstantConditions")
    boolean selectExistsStudentById(UUID studentId) {
        String sql = "" +
                "SELECT EXISTS ( " +
                "   SELECT 1 " +
                "   FROM student " +
                "   WHERE student_id = ? " +
                ")";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{studentId},
                Boolean.class
        );
    }


    List<StudentCourse> selectAllStudentCourses(UUID studentId) {
        String sql = "" +
                "SELECT " +
                " student.student_id, " +
                " course.course_id, " +
                " course.name, " +
                " course.description," +
                " course.department," +
                " course.teacher_name," +
                " student_course.start_date, " +
                " student_course.end_date, " +
                " student_course.grade " +
                "FROM student " +
                "JOIN student_course USING (student_id) " +
                "JOIN course         USING (course_id) " +
                "WHERE student.student_id = ?";
        return jdbcTemplate.query(
                sql,
                new Object[]{studentId},
                mapStudentCourseFromDb()
        );
    }

    private RowMapper<StudentCourse> mapStudentCourseFromDb() {
        return (resultSet, i) -> {
            String gradeStr = resultSet.getString("grade");
            Integer grade = (gradeStr != null) ? Integer.parseInt(gradeStr) : null;

            return new StudentCourse(
                    UUID.fromString(resultSet.getString("student_id")),
                    UUID.fromString(resultSet.getString("course_id")),
                    resultSet.getString("name"),
                    resultSet.getString("description"),
                    resultSet.getString("department"),
                    resultSet.getString("teacher_name"),
                    resultSet.getDate("start_date").toLocalDate(),
                    resultSet.getDate("end_date").toLocalDate(),
                    grade
            );
        };
    }

    private RowMapper<Student> mapStudentFomDb() {
        return (resultSet, i) -> {
            // No need for Optional.ofNullable for these as they are non-nullable in the database schema typically
            UUID studentId = UUID.fromString(resultSet.getString("student_id"));
            String firstName = resultSet.getString("first_name");
            String lastName = resultSet.getString("last_name");
            String email = resultSet.getString("email");
            Student.Gender gender = Student.Gender.valueOf(resultSet.getString("gender").toUpperCase());

            return new Student(
                    studentId,
                    firstName,
                    lastName,
                    email,
                    gender
            );
        };
    }

    int updateEmail(UUID studentId, String email) {
        String sql = "" +
                "UPDATE student " +
                "SET email = ? " +
                "WHERE student_id = ?";
        return jdbcTemplate.update(sql, email, studentId);
    }

    int updateFirstName(UUID studentId, String firstName) {
        String sql = "" +
                "UPDATE student " +
                "SET first_name = ? " +
                "WHERE student_id = ?";
        return jdbcTemplate.update(sql, firstName, studentId);
    }

    int updateLastName(UUID studentId, String lastName) {
        String sql = "" +
                "UPDATE student " +
                "SET last_name = ? " +
                "WHERE student_id = ?";
        return jdbcTemplate.update(sql, lastName, studentId);
    }

    @SuppressWarnings("ConstantConditions")
    boolean selectExistsEmail(UUID studentId, String email) {
        String sql = "" +
                "SELECT EXISTS ( " +
                "   SELECT 1 " +
                "   FROM student " +
                "   WHERE student_id <> ? " +
                "    AND email = ? " +
                ")";
        return jdbcTemplate.queryForObject(
                sql,
                new Object[]{studentId, email},
                Boolean.class // More concise way to get a boolean
        );
    }

    int deleteStudentById(UUID studentId) {
        String sql = "" +
                "DELETE FROM student " +
                "WHERE student_id = ?";
        return jdbcTemplate.update(sql, studentId);
    }
}