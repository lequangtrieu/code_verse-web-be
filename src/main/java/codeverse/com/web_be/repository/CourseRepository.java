package codeverse.com.web_be.repository;

import codeverse.com.web_be.dto.response.CourseResponse.Course.SimpleCourseCardDto;
import codeverse.com.web_be.dto.response.CourseResponse.*;
import codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseMoreInfoDTO;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.LessonProgress;
import codeverse.com.web_be.enums.CourseStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface CourseRepository extends JpaRepository<Course, Long> {
    List<Course> findByInstructorId(Long instructorId);

    List<Course> findByInstructorUsername(String username);

    Course findCourseById(Long courseId);

//    List<Course> findAllByIsDeletedFalseAndIsPublishedTrue();

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name as category, c.price, c.discount, " +
            "u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalLessons, " +
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c) as rating, " +
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c) as ratingCount, " +
            "(SELECT COUNT(DISTINCT pt.user) FROM CourseEnrollment pt WHERE pt.course = c) as totalStudents, " +
            "false as isTrending, " +
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalDurations," +
            "CAST(c.language AS string)) " +
            "FROM Course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE c.isDeleted = false AND c.status = \"PUBLISHED\"")
    List<CourseResponse> selectAllCourses();

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseDetail.CourseMoreInfoDTO(" +
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course.id = :courseId), " + // rating
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course.id = :courseId), " +                            // ratingCount
            "(SELECT COUNT(DISTINCT pt.user) FROM CourseEnrollment pt WHERE pt.course.id = :courseId), " +         // totalStudents
            "false, " +                                                                                              // isTrending
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course.id = :courseId), " +            // totalLessons
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course.id = :courseId), " + // totalDurations
            "cat.name, " +                                                                                           // category
            "u.name, u.id) " +                                                                                              // instructor
            "FROM Course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE c.id = :courseId AND c.isDeleted = false AND c.status = 'PUBLISHED'")
    CourseMoreInfoDTO selectCourseMoreInfoById(@Param("courseId") Long courseId);


    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name, u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " +              // totalLessons
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c), " + // rating
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c), " +                             // ratingCount
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " + // totalDurations
            "pt.completionPercentage) " +                                                                // completionPercentage
            "FROM CourseEnrollment pt " +
            "JOIN pt.course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE pt.user.id = :userId " +
            "AND pt.completionPercentage < 100 " +
            "AND c.isDeleted = false AND c.status = 'PUBLISHED'")
    List<CourseProgressResponse> findInProgressCourseResponsesByUserId(@Param("userId") Long userId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name, u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " +              // totalLessons
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c), " + // rating
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c), " +                             // ratingCount
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " + // totalDurations
            "pt.completionPercentage) " +                                                                // completionPercentage
            "FROM CourseEnrollment pt " +
            "JOIN pt.course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE pt.user.id = :userId " +
            "AND pt.completionPercentage = 100 " +
            "AND c.isDeleted = false AND c.status = 'PUBLISHED'")
    List<CourseProgressResponse> findCompletedCourseResponsesByUserId(@Param("userId") Long userId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name as category, c.price, c.discount, " +
            "u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalLessons, " +
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c) as rating, " +
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c) as ratingCount, " +
            "(SELECT COUNT(DISTINCT pt2.user) FROM CourseEnrollment pt2 WHERE pt2.course = c) as totalStudents, " +
            "false as isTrending, " +
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c) as totalDurations," +
            "CAST(c.language AS string))  " +
            "FROM Course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE c.isDeleted = false AND c.status = \"PUBLISHED\" " +
            "AND NOT EXISTS (SELECT 1 FROM CourseEnrollment pt WHERE pt.course = c AND pt.user.id = :userId)")
    List<CourseResponse> findSuggestedCourseResponsesByUserId(@Param("userId") Long userId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseProgressResponse(" +
            "c.id, c.title, c.description, c.thumbnailUrl, CAST(c.level AS string), cat.name, u.name, " +
            "(SELECT COUNT(l) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " +              // totalLessons
            "(SELECT COALESCE(ROUND(AVG(cr.rating), 1), 0) FROM CourseRating cr WHERE cr.course = c), " + // rating
            "(SELECT COUNT(cr) FROM CourseRating cr WHERE cr.course = c), " +                             // ratingCount
            "(SELECT COALESCE(SUM(l.duration), 0) FROM Lesson l JOIN l.courseModule ms WHERE ms.course = c), " + // totalDurations
            "pt.completionPercentage) " +                                                                // completionPercentage
            "FROM CourseEnrollment pt " +
            "JOIN pt.course c " +
            "LEFT JOIN c.category cat " +
            "LEFT JOIN c.instructor u " +
            "WHERE pt.user.id = :userId " +
            "AND c.isDeleted = false AND c.status = 'PUBLISHED'")
    List<CourseProgressResponse> findAllCoursesWithProgressByUserId(@Param("userId") Long userId);

    @Query("SELECT lp FROM LessonProgress lp " +
            "WHERE lp.user.id = :userId AND lp.lesson.courseModule.course.id = :courseId")
    List<LessonProgress> findByUserIdAndCourseId(@Param("userId") Long userId,
                                                 @Param("courseId") Long courseId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.CourseModuleDTO(" +
            "cm.id as id, " +
            "cm.title as title) " +
            "FROM CourseModule cm " +
            "WHERE cm.course.id = :courseId")
    List<CourseModuleDTO> getModulesByCourseId(@Param("courseId") Long courseId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.LessonDTO(" +
            "l.id as id, " +
            "l.orderIndex as orderIndex, " +
            "l.lessonType as lessonType, " +
            "l.expReward as expGained, " +
            "l.title as title) " +
            "FROM Lesson l " +
            "WHERE l.courseModule.id = :moduleId")
    List<LessonDTO> getLessonsByModuleId(@Param("moduleId") Long moduleId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.TheoryDTO(" +
            "t.title as title, " +
            "t.content as content) " +
            "FROM Theory t " +
            "WHERE t.lesson.id = :lessonId")
    TheoryDTO getTheoryByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.ExerciseDTO(" +
            "e.id as id, " +
            "e.title as title, " +
            "e.instruction as instruction) " +
            "FROM Exercise e " +
            "WHERE e.lesson.id = :lessonId")
    ExerciseDTO getExerciseByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.TaskDTO(" +
            "e.id as id, " +
            "e.description as description)" +
            "FROM ExerciseTask e " +
            "WHERE e.exercise.id = :exerciseID")
    List<TaskDTO> getExerciseTaskByExerciseID(@Param("exerciseID") Long exerciseID);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.QuestionDTO(" +
            "q.id as id, " +
            "q.quizType as quizType, " +
            "q.question as question) " +
            "FROM QuizQuestion q " +
            "WHERE q.lesson.id = :lessonId")
    List<QuestionDTO> getQuestionByLessonId(@Param("lessonId") Long lessonId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.AnswersDTO(" +
            "q.id as id, " +
            "q.answer as answer, " +
            "q.isCorrect as isCorrect) " +
            "FROM QuizAnswer q " +
            "WHERE q.question.id = :questionId")
    List<AnswersDTO> getAnswersByQuestionID(@Param("questionId") Long questionId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.TestCaseDTO(" +
            "t.id as id, " +
            "t.input as input, " +
            "t.expectedOutput as expected)" +
            "FROM TestCase t " +
            "WHERE t.exercise.id = :exerciseID")
    List<TestCaseDTO> getTestCaseByExerciseId(@Param("exerciseID") Long exerciseID);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.Course.SimpleCourseCardDto(" +
            "c.id, c.title, c.thumbnailUrl, c.price, c.discount) " +
            "FROM Course c " +
            "WHERE c.instructor.id = :instructorId AND c.id <> :courseId AND c.status = 'PUBLISHED' AND c.isDeleted = false")
    List<SimpleCourseCardDto> findOtherCoursesByInstructor(@Param("instructorId") Long instructorId,
                                                           @Param("courseId") Long courseId);

    @Query("SELECT new codeverse.com.web_be.dto.response.CourseResponse.Course.SimpleCourseCardDto(" +
            "c.id, c.title, c.thumbnailUrl, c.price, c.discount) " +
            "FROM Course c " +
            "WHERE c.status = 'PUBLISHED' AND c.isDeleted = false " +
            "ORDER BY (SELECT COUNT(e) FROM CourseEnrollment e WHERE e.course = c) DESC")
    List<SimpleCourseCardDto> findPopularCourses(Pageable pageable);

    List<Course> findByStatus(CourseStatus status);
    long countByStatus(CourseStatus status);

    @Query("SELECT COUNT(c) FROM Course c WHERE c.createdAt BETWEEN :fromDate AND :toDate")
    long countNewCourses(@Param("fromDate") LocalDateTime fromDate, @Param("toDate") LocalDateTime toDate);

}