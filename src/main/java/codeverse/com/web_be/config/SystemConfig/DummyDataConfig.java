package codeverse.com.web_be.config.SystemConfig;

import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.enums.*;
import codeverse.com.web_be.repository.*;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Configuration
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class DummyDataConfig {
    UserRepository userRepository;
    CategoryRepository categoryRepository;
    CourseRepository courseRepository;
    CourseModuleRepository courseModuleRepository;
    LessonRepository lessonRepository;
    TheoryRepository theoryRepository;
    ExerciseRepository exerciseRepository;
    PasswordEncoder passwordEncoder;
    CourseEnrollmentRepository courseEnrollmentRepository;
    CourseRatingRepository courseRatingRepository;
    LessonProgressRepository lessonProgressRepository;
    ExerciseTaskRepository exerciseTaskRepository;
    TestCaseRepository testCaseRepository;
    NotificationRepository notificationRepository;
    UserNotificationRepository userNotificationRepository;
    ReportReasonRepository reportReasonRepository;
    UserReportRepository userReportRepository;

    String password = "pass";
    String adminPassword = "admin";
    String thumbnailUrl1 = "https://vtiacademy.edu.vn/upload/images/artboard-1-copy-7-100.jpg";
    String thumbnailUrl2 = "https://letdiv.com/wp-content/uploads/2024/04/khoa-hoc-react.png";
    String certInstructor ="https://firebasestorage.googleapis.com/v0/b/codeverse-7830f.firebasestorage.app/o/images%2Fb4d226af-50cf-4699-bb5b-449b0ea21a26_cert_page-0001.jpg?alt=media";

    private static final boolean DUMMY_DATA = false;

    @Bean
    ApplicationRunner initDummyData() {
        return args -> {
            if (!DUMMY_DATA) {
                log.info("Update DUMMY_DATA to true to create dummy data");
                return;
            }

            // Tạo categories
            List<Category> categories = List.of(
                    Category.builder().name("Web Development").build(),
                    Category.builder().name("Mobile Development").build(),
                    Category.builder().name("Data Science").build(),
                    Category.builder().name("Machine Learning").build(),
                    Category.builder().name("Cloud Computing").build()
            );
            categoryRepository.saveAll(categories);

            // Tạo instructors
            List<User> instructors = List.of(
                    User.builder()
                            .username("admin@gmail.com")
                            .password(passwordEncoder.encode(adminPassword))
                            .isVerified(true)
                            .role(UserRole.ADMIN)
                            .name("ADMIN")
                            .createdAt(LocalDateTime.now())
                            .updatedAt(LocalDateTime.now())
                            .build(),
                    User.builder()
                            .username("tientnm@gmail.com")
                            .password(passwordEncoder.encode(password))
                            .name("Từ Nguyễn Minh Tiên")
                            .role(UserRole.ADMIN)
                            .isVerified(true)
                            .build(),
                    User.builder()
                            .username("trieulqde160447@gmail.com")
                            .password(passwordEncoder.encode(password))
                            .name("Lê Quang Triêu")
                            .isVerified(true)
                            .role(UserRole.LEARNER)
                            .build(),
                    User.builder()
                            .username("dolv@gmail.com")
                            .password(passwordEncoder.encode(password))
                            .name("Lê Văn Độ")
                            .isVerified(true)
                            .role(UserRole.LEARNER)
                            .build(),
                    User.builder()
                            .username("hienlt@gmail.com")
                            .password(passwordEncoder.encode(password))
                            .name("Lê Thu Hiền")
                            .isVerified(true)
                            .instructorStatus(InstructorStatus.APPROVED)
                            .teachingCredentials(certInstructor)
                            .role(UserRole.INSTRUCTOR)
                            .build(),
                    User.builder()
                            .username("john.doe@gmail.com")
                            .password(passwordEncoder.encode(password))
                            .name("John Doe")
                            .role(UserRole.INSTRUCTOR)
                            .instructorStatus(InstructorStatus.PENDING)
                            .isVerified(true)
                            .build(),
                    User.builder()
                            .username("jane.smith@gmail.com")
                            .password(passwordEncoder.encode(password))
                            .name("Jane Smith")
                            .role(UserRole.INSTRUCTOR)
                            .instructorStatus(InstructorStatus.PENDING)
                            .isVerified(true)
                            .build(),
                    User.builder()
                            .username("support@codeverse.com")
                            .password(passwordEncoder.encode(password))
                            .name("Support Staff")
                            .role(UserRole.ADMIN)
                            .isVerified(true)
                            .build(),
                    User.builder()
                            .username("instructor@gmail.com")
                            .password(passwordEncoder.encode(password))
                            .name("instructor Smith")
                            .role(UserRole.INSTRUCTOR)
                            .instructorStatus(InstructorStatus.REJECTED)
                            .isVerified(true)
                            .build()
            );
            userRepository.saveAll(instructors);

            // Tạo courses
            List<Course> courses = List.of(
                    Course.builder()
                            .title("Complete Web Development Bootcamp")
                            .description("Learn HTML, CSS, JavaScript, React, Node.js, MongoDB and more!")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.BEGINNER)
                            .category(categories.get(0))
                            .language(CodeLanguage.JAVA)
                            .price(new BigDecimal("99000"))
                            .discount(new BigDecimal("10.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("iOS App Development with Swift")
                            .description("Build iOS apps from scratch using Swift and Xcode")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .language(CodeLanguage.C)
                            .price(new BigDecimal("79000"))
                            .discount(new BigDecimal("15.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Data Science Fundamentals")
                            .description("Learn Python, NumPy, Pandas, and data visualization")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.BEGINNER)
                            .category(categories.get(2))
                            .language(CodeLanguage.CSHARP)
                            .price(new BigDecimal("89000"))
                            .discount(new BigDecimal("0.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Machine Learning with Python")
                            .description("Master machine learning algorithms and techniques")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .language(CodeLanguage.PYTHON)
                            .price(new BigDecimal("0"))
                            .discount(new BigDecimal("0.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("AWS Certified Solutions Architect")
                            .description("Prepare for AWS certification with hands-on projects")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .language(CodeLanguage.CPP)
                            .price(new BigDecimal("149000"))
                            .discount(new BigDecimal("50.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    // Thêm 10 khóa học mới
                    Course.builder()
                            .title("Flutter Mobile App Development")
                            .description("Build cross-platform mobile apps with Flutter and Dart")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .language(CodeLanguage.JAVASCRIPT)
                            .price(new BigDecimal("50000"))
                            .discount(new BigDecimal("0.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Advanced JavaScript Patterns")
                            .description("Master advanced JavaScript concepts and design patterns")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(0))
                            .language(CodeLanguage.JAVASCRIPT)
                            .price(new BigDecimal("75000"))
                            .discount(new BigDecimal("100.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Python for Data Analysis")
                            .description("Learn data analysis with Python, Pandas, and NumPy")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.BEGINNER)
                            .category(categories.get(2))
                            .language(CodeLanguage.PYTHON)
                            .price(new BigDecimal("25000"))
                            .discount(new BigDecimal("25.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Deep Learning with TensorFlow")
                            .description("Build and train neural networks with TensorFlow")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .language(CodeLanguage.C)
                            .price(new BigDecimal("100000"))
                            .discount(new BigDecimal("30.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("DevOps with Docker and Kubernetes")
                            .description("Master containerization and orchestration")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .language(CodeLanguage.JAVA)
                            .price(new BigDecimal("85000"))
                            .discount(new BigDecimal("15.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("React Native Mobile Development")
                            .description("Build native mobile apps with React Native")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .language(CodeLanguage.JAVASCRIPT)
                            .price(new BigDecimal("65000"))
                            .discount(new BigDecimal("45.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Full Stack Development with MERN")
                            .description("Build full stack applications with MongoDB, Express, React, and Node.js")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(0))
                            .language(CodeLanguage.JAVASCRIPT)
                            .price(new BigDecimal("90000"))
                            .discount(new BigDecimal("20.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Big Data Processing with Spark")
                            .description("Process and analyze big data with Apache Spark")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(2))
                            .language(CodeLanguage.CSHARP)
                            .price(new BigDecimal("95000"))
                            .discount(new BigDecimal("10.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Blockchain Development")
                            .description("Learn blockchain development and smart contracts")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(0))
                            .language(CodeLanguage.C)
                            .price(new BigDecimal("80000"))
                            .discount(new BigDecimal("35.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Game Development with Unity")
                            .description("Create games using Unity and C#")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .language(CodeLanguage.CSHARP)
                            .price(new BigDecimal("70000"))
                            .discount(new BigDecimal("40.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build()
            );
            courseRepository.saveAll(courses);

            // Tạo material sections cho tất cả khóa học
            List<CourseModule> courseModules = List.of(
                    // Web Development Course
                    CourseModule.builder()
                            .course(courses.get(0))
                            .title("Introduction to Web Development")
                            .orderIndex(1)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(0))
                            .title("HTML & CSS Fundamentals")
                            .orderIndex(2)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(0))
                            .title("JavaScript Basics")
                            .orderIndex(3)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(0))
                            .title("React.js Introduction")
                            .orderIndex(4)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(0))
                            .title("Backend Development with Node.js")
                            .orderIndex(5)
                            .build(),

                    // iOS Development Course
                    CourseModule.builder()
                            .course(courses.get(1))
                            .title("Introduction to iOS Development")
                            .orderIndex(1)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(1))
                            .title("Swift Fundamentals")
                            .orderIndex(2)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(1))
                            .title("UIKit Basics")
                            .orderIndex(3)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(1))
                            .title("SwiftUI Introduction")
                            .orderIndex(4)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(1))
                            .title("iOS App Architecture")
                            .orderIndex(5)
                            .build(),

                    // Data Science Course
                    CourseModule.builder()
                            .course(courses.get(2))
                            .title("Introduction to Data Science")
                            .orderIndex(1)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(2))
                            .title("Python for Data Science")
                            .orderIndex(2)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(2))
                            .title("NumPy and Pandas")
                            .orderIndex(3)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(2))
                            .title("Data Visualization")
                            .orderIndex(4)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(2))
                            .title("Data Analysis Projects")
                            .orderIndex(5)
                            .build(),

                    // Machine Learning Course
                    CourseModule.builder()
                            .course(courses.get(3))
                            .title("Introduction to Machine Learning")
                            .orderIndex(1)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(3))
                            .title("Supervised Learning")
                            .orderIndex(2)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(3))
                            .title("Unsupervised Learning")
                            .orderIndex(3)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(3))
                            .title("Deep Learning Basics")
                            .orderIndex(4)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(3))
                            .title("ML Project Implementation")
                            .orderIndex(5)
                            .build(),

                    // AWS Course
                    CourseModule.builder()
                            .course(courses.get(4))
                            .title("Introduction to AWS")
                            .orderIndex(1)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(4))
                            .title("EC2 and VPC")
                            .orderIndex(2)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(4))
                            .title("S3 and Storage Services")
                            .orderIndex(3)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(4))
                            .title("Database Services")
                            .orderIndex(4)
                            .build(),
                    CourseModule.builder()
                            .course(courses.get(4))
                            .title("Security and Compliance")
                            .orderIndex(5)
                            .build()
            );
            courseModuleRepository.saveAll(courseModules);

            // Tạo material sections cho 10 khóa học mới
            List<CourseModule> newCourseModules = new ArrayList<>();
            String[] sectionTitles = {
                    "Introduction and Setup",
                    "Core Concepts",
                    "Advanced Topics",
                    "Practical Projects",
                    "Best Practices and Optimization"
            };

            for (int i = 5; i < courses.size(); i++) {
                Course course = courses.get(i);
                for (int j = 0; j < 5; j++) {
                    newCourseModules.add(CourseModule.builder()
                            .course(course)
                            .title(course.getTitle() + " - " + sectionTitles[j])
                            .orderIndex(j + 1)
                            .build());
                }
            }
            courseModuleRepository.saveAll(newCourseModules);

            // Tạo lessons cho tất cả material sections
            List<Lesson> lessons = new ArrayList<>();
            String[] lessonTypes = {
                    "Overview and Introduction",
                    "Basic Concepts",
                    "Hands-on Practice",
                    "Advanced Techniques",
                    "Project Work"
            };

            // Tạo lessons cho material sections cũ
            for (CourseModule section : courseModules) {
                for (int i = 0; i < 5; i++) {
                    lessons.add(Lesson.builder()
                            .courseModule(section)
                            .title(section.getTitle() + " - " + lessonTypes[i])
                            .orderIndex(i + 1)
                            .duration(10)
                            .lessonType(LessonType.CODE)
                            .build());
                }
            }

            // Tạo lessons cho material sections mới
            for (CourseModule section : newCourseModules) {
                for (int i = 0; i < 5; i++) {
                    lessons.add(Lesson.builder()
                            .courseModule(section)
                            .title(section.getTitle() + " - " + lessonTypes[i])
                            .orderIndex(i + 1)
                            .duration(10)
                            .lessonType(LessonType.CODE)
                            .build());
                }
            }
            lessonRepository.saveAll(lessons);

            // Tạo theories cho tất cả lessons
            List<Theory> theories = new ArrayList<>();
            for (Lesson lesson : lessons) {
                theories.add(Theory.builder()
                        .lesson(lesson)
                        .title(lesson.getTitle() + " - Theory")
                        .content("https://firebasestorage.googleapis.com/v0/b/codeverse-7830f.firebasestorage.app/o/theories%2F376%2F52c7b32f-fc26-4383-9f08-656f1d8b062e_Theory.html?alt=media")
                        .build());
            }
            theoryRepository.saveAll(theories);

            // Tạo exercises cho tất cả lessons
            List<Exercise> exercises = new ArrayList<>();
            for (Lesson lesson : lessons) {
                exercises.add(Exercise.builder()
                        .lesson(lesson)
                        .title(lesson.getTitle() + " - Exercise")
                        .expReward(100 * lesson.getOrderIndex())
                        .instruction("Complete the exercise for " + lesson.getTitle() + ". Practice what you've learned...")
                        .build());
            }
            exerciseRepository.saveAll(exercises);

            // Tạo exercise tasks cho exercises
            List<ExerciseTask> exerciseTasks = new ArrayList<>();
            for (Exercise exercise : exercises) {
                exerciseTasks.add(ExerciseTask.builder()
                        .exercise(exercise)
                        .description("Task 1: Implement the basic functionality for " + exercise.getTitle() + "\n" +
                                "Requirements:\n" +
                                "1. Create a function that handles the main logic\n" +
                                "2. Implement error handling\n" +
                                "3. Add input validation\n" +
                                "4. Write unit tests")
                        .build());
                exerciseTasks.add(ExerciseTask.builder()
                        .exercise(exercise)
                        .description("Task 2: Optimize the solution for " + exercise.getTitle() + "\n" +
                                "Requirements:\n" +
                                "1. Improve time complexity\n" +
                                "2. Reduce memory usage\n" +
                                "3. Add comments and documentation\n" +
                                "4. Handle edge cases")
                        .build());
            }
            exerciseTaskRepository.saveAll(exerciseTasks);

            // Tạo test cases cho exercises
            List<TestCase> testCases = new ArrayList<>();
            for (Exercise exercise : exercises) {
                // Public test cases
                testCases.add(TestCase.builder()
                        .exercise(exercise)
                        .input("test input 1")
                        .expectedOutput("expected output 1")
                        .priority(TestCasePriority.REQUIRED)
                        .isPublic(true)
                        .build());
                testCases.add(TestCase.builder()
                        .exercise(exercise)
                        .input("test input 2")
                        .expectedOutput("expected output 2")
                        .priority(TestCasePriority.REQUIRED)
                        .isPublic(true)
                        .build());

                // Private test cases
                testCases.add(TestCase.builder()
                        .exercise(exercise)
                        .input("private test input 1")
                        .expectedOutput("private expected output 1")
                        .priority(TestCasePriority.REQUIRED)
                        .isPublic(false)
                        .build());
                testCases.add(TestCase.builder()
                        .exercise(exercise)
                        .input("private test input 2")
                        .expectedOutput("private expected output 2")
                        .priority(TestCasePriority.REQUIRED)
                        .isPublic(false)
                        .build());
            }
            testCaseRepository.saveAll(testCases);

//            // Tạo progress tracking
//            List<CourseEnrollment> courseEnrollments = new ArrayList<>();
//            courseEnrollments.add(CourseEnrollment.builder()
//                    .user(instructors.get(2))
//                    .course(courses.get(0))
//                    .completionPercentage(100f)
//                    .completedAt(LocalDateTime.now().minusDays((long) (Math.random() * 10)))
//                    .build());
//            for (Course course : courses) {
//                for (User instructor : instructors) {
//                    courseEnrollments.add(CourseEnrollment.builder()
//                            .user(instructor)
//                            .course(course)
//                            .completionPercentage((float) (Math.random() * 100))
//                            .completedAt(LocalDateTime.now().minusDays((long) (Math.random() * 10)))
//                            .build());
//                }
//            }
//            courseEnrollmentRepository.saveAll(courseEnrollments);

            // Tạo course ratings
            List<CourseRating> courseRatings = new ArrayList<>();
            for (Course course : courses) {
                for (User instructor : instructors) {
                    courseRatings.add(CourseRating.builder()
                            .user(instructor)
                            .course(course)
                            .rating(Math.round((1.0f + (float) (Math.random() * 4.0f)) * 10.0f) / 10.0f) // Rating từ 1.0-5.0 với 1 số thập phân
                            .comment("Great course! " + course.getTitle() + " is very informative and well-structured.")
                            .build());
                }
            }
            courseRatingRepository.saveAll(courseRatings);

            // Tạo lesson progress
            List<LessonProgress> lessonProgresses = new ArrayList<>();
            for (Lesson lesson : lessons) {
                for (User instructor : instructors) {
                    LessonProgressStatus status = Math.random() > 0.5 ?
                            LessonProgressStatus.PASSED : LessonProgressStatus.PENDING;

                    LocalDateTime startedAt = LocalDateTime.now().minusDays((long) (Math.random() * 10));
                    LocalDateTime completedAt = status == LessonProgressStatus.PASSED ?
                            startedAt.plusHours((long) (Math.random() * 24)) : null;

                    lessonProgresses.add(LessonProgress.builder()
                            .user(instructor)
                            .lesson(lesson)
                            .status(status)
                            .expGained(status == LessonProgressStatus.PASSED ?
                                    (int) (Math.random() * 100) + 50 : 0)
                            .startedAt(startedAt)
                            .completedAt(completedAt)
                            .build());
                }
            }
            lessonProgressRepository.saveAll(lessonProgresses);

            // Thêm 15 khóa học mới
            List<Course> additionalCourses = List.of(
                    Course.builder()
                            .title("Spring Boot Microservices")
                            .description("Build scalable microservices with Spring Boot and Spring Cloud")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(0))
                            .language(CodeLanguage.JAVA)
                            .price(new BigDecimal("85000"))
                            .discount(new BigDecimal("20.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Android Development with Kotlin")
                            .description("Create Android apps using Kotlin and Android Studio")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .language(CodeLanguage.JAVA)
                            .price(new BigDecimal("75000"))
                            .discount(new BigDecimal("15.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Natural Language Processing")
                            .description("Learn NLP techniques and build language models")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .language(CodeLanguage.CPP)
                            .price(new BigDecimal("95000"))
                            .discount(new BigDecimal("25.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Cloud Architecture with Azure")
                            .description("Design and implement cloud solutions on Microsoft Azure")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .language(CodeLanguage.CSHARP)
                            .price(new BigDecimal("90000"))
                            .discount(new BigDecimal("30.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Vue.js Frontend Development")
                            .description("Build modern web applications with Vue.js")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(0))
                            .language(CodeLanguage.JAVASCRIPT)
                            .price(new BigDecimal("65000"))
                            .discount(new BigDecimal("10.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("iOS App Development with SwiftUI")
                            .description("Create modern iOS apps using SwiftUI framework")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .language(CodeLanguage.JAVASCRIPT)
                            .price(new BigDecimal("80000"))
                            .discount(new BigDecimal("40.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Data Engineering with Python")
                            .description("Build data pipelines and ETL processes")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(2))
                            .language(CodeLanguage.PYTHON)
                            .price(new BigDecimal("85000"))
                            .discount(new BigDecimal("35.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Computer Vision with OpenCV")
                            .description("Learn image processing and computer vision")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .language(CodeLanguage.JAVA)
                            .price(new BigDecimal("90000"))
                            .discount(new BigDecimal("45.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Google Cloud Platform")
                            .description("Master cloud computing with Google Cloud Platform")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .language(CodeLanguage.CPP)
                            .price(new BigDecimal("95000"))
                            .discount(new BigDecimal("50.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Angular Advanced Patterns")
                            .description("Master advanced Angular patterns and best practices")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(0))
                            .language(CodeLanguage.JAVASCRIPT)
                            .price(new BigDecimal("70000"))
                            .discount(new BigDecimal("20.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Cross-Platform Mobile Development")
                            .description("Build mobile apps for iOS and Android using Xamarin")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .language(CodeLanguage.CSHARP)
                            .price(new BigDecimal("75000"))
                            .discount(new BigDecimal("25.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Big Data Analytics with Hadoop")
                            .description("Process and analyze big data using Hadoop ecosystem")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(2))
                            .language(CodeLanguage.CSHARP)
                            .price(new BigDecimal("85000"))
                            .discount(new BigDecimal("30.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Reinforcement Learning")
                            .description("Learn reinforcement learning algorithms and applications")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .language(CodeLanguage.RUBY)
                            .price(new BigDecimal("90000"))
                            .discount(new BigDecimal("35.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Serverless Architecture")
                            .description("Build serverless applications with AWS Lambda")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .language(CodeLanguage.C)
                            .price(new BigDecimal("80000"))
                            .discount(new BigDecimal("40.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build(),
                    Course.builder()
                            .title("Full Stack Development with Django")
                            .description("Build web applications with Django and React")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(0))
                            .language(CodeLanguage.JAVASCRIPT)
                            .price(new BigDecimal("75000"))
                            .discount(new BigDecimal("45.00"))
                            .status(CourseStatus.PUBLISHED)
                            .instructor(instructors.get(4))
                            .build()
            );
            courseRepository.saveAll(additionalCourses);

            List<Notification> notifications = List.of(
                    Notification.builder()
                            .title("System Maintain")
                            .content("CodeVerse is in maintenance from 30/7 to 1/8. Please notice that you cannot log in during this period.")
                            .createdBy(instructors.get(0))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    Notification.builder()
                            .title("System Maintain")
                            .content("CodeVerse is in maintenance from 30/3 to 1/4. Please notice that you cannot log in during this period.")
                            .createdBy(instructors.get(0))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    Notification.builder()
                            .title("System Maintain")
                            .content("CodeVerse is in maintenance from 30/12 to 1/1. Please notice that you cannot log in during this period.")
                            .createdBy(instructors.get(0))
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            notificationRepository.saveAll(notifications);

            List<UserNotification> userNotifications = List.of(
                    UserNotification.builder()
                            .user(instructors.get(2))
                            .notification(notifications.get(0))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    UserNotification.builder()
                            .user(instructors.get(3))
                            .notification(notifications.get(0))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    UserNotification.builder()
                            .user(instructors.get(4))
                            .notification(notifications.get(0))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    UserNotification.builder()
                            .user(instructors.get(2))
                            .notification(notifications.get(1))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    UserNotification.builder()
                            .user(instructors.get(3))
                            .notification(notifications.get(1))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    UserNotification.builder()
                            .user(instructors.get(4))
                            .notification(notifications.get(1))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    UserNotification.builder()
                            .user(instructors.get(2))
                            .notification(notifications.get(2))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    UserNotification.builder()
                            .user(instructors.get(3))
                            .notification(notifications.get(2))
                            .createdAt(LocalDateTime.now())
                            .build(),
                    UserNotification.builder()
                            .user(instructors.get(4))
                            .notification(notifications.get(2))
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            userNotificationRepository.saveAll(userNotifications);

            List<ReportReason> reasons = List.of(
                    ReportReason.builder().title("Spam or Scam").description("User is posting spam, advertising, or attempting to scam others.").build(),
                    ReportReason.builder().title("Harassment").description("User is harassing, threatening, or bullying others.").build(),
                    ReportReason.builder().title("Inappropriate Content").description("User is sharing inappropriate, offensive, or explicit content.").build(),
                    ReportReason.builder().title("Impersonation").description("User is pretending to be someone else.").build(),
                    ReportReason.builder().title("Other").description("Other reasons not listed above.").build()
            );
            reportReasonRepository.saveAll(reasons);


            List<ReportReason> reportReasons = reportReasonRepository.findAll();
            List<UserReport> reports = List.of(
                    UserReport.builder()
                            .reporter(instructors.get(2))
                            .reportedUser(instructors.get(3))
                            .reason(reportReasons.get(1))
                            .customReason("Sent multiple threatening messages.")
                            .evidenceUrl(certInstructor)
                            .status(ReportStatus.REVIEWED)
                            .adminNote("User has been warned.")
                            .createdAt(LocalDateTime.now().minusDays(3))
                            .reviewedAt(LocalDateTime.now().minusDays(1))
                            .build(),

                    UserReport.builder()
                            .reporter(instructors.get(3))
                            .reportedUser(instructors.get(2))
                            .reason(reportReasons.get(2))
                            .evidenceUrl(certInstructor)
                            .customReason("Posted inappropriate jokes during lesson.")
                            .status(ReportStatus.REJECTED)
                            .adminNote("Content was reviewed and not deemed a violation.")
                            .createdAt(LocalDateTime.now().minusDays(5))
                            .reviewedAt(LocalDateTime.now().minusDays(4))
                            .build(),

                    UserReport.builder()
                            .reporter(instructors.get(4))
                            .reportedUser(instructors.get(2))
                            .reason(reportReasons.get(0))
                            .evidenceUrl(certInstructor)
                            .customReason("Repeatedly invited learners to Telegram crypto group.")
                            .status(ReportStatus.REVIEWED)
                            .adminNote("Account suspended for 3 days.")
                            .createdAt(LocalDateTime.now().minusDays(7))
                            .reviewedAt(LocalDateTime.now().minusDays(6))
                            .build(),

                    UserReport.builder()
                            .reporter(instructors.get(3))
                            .reportedUser(instructors.get(4))
                            .reason(reportReasons.get(3))
                            .evidenceUrl(certInstructor)
                            .customReason("Pretending to be another instructor in discussion.")
                            .status(ReportStatus.PENDING)
                            .createdAt(LocalDateTime.now().minusHours(20))
                            .build(),

                    UserReport.builder()
                            .reporter(instructors.get(2))
                            .reportedUser(instructors.get(4))
                            .reason(reportReasons.get(4))
                            .customReason("Suspicious behavior during live coding session.")
                            .evidenceUrl(certInstructor)
                            .status(ReportStatus.PENDING)
                            .createdAt(LocalDateTime.now().minusMinutes(90))
                            .build()
            );

            userReportRepository.saveAll(reports);

            log.info("Dummy data has been initialized successfully");
            log.info("Additional dummy data has been initialized successfully");
        };
    }
}