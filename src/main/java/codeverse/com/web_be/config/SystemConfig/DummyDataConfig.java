package codeverse.com.web_be.config.SystemConfig;

import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.enums.CourseLevel;
import codeverse.com.web_be.enums.DiscountType;
import codeverse.com.web_be.enums.LessonProgressStatus;
import codeverse.com.web_be.enums.SubmissionStatus;
import codeverse.com.web_be.enums.TestCasePriority;
import codeverse.com.web_be.enums.UserRole;
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
    VoucherRepository voucherRepository;
    MaterialSectionRepository materialSectionRepository;
    LessonRepository lessonRepository;
    TheoryRepository theoryRepository;
    ExerciseRepository exerciseRepository;
    PasswordEncoder passwordEncoder;
    ProgressTrackingRepository progressTrackingRepository;
    CourseRatingRepository courseRatingRepository;
    LessonProgressRepository lessonProgressRepository;
    ExerciseTaskRepository exerciseTaskRepository;
    TestCaseRepository testCaseRepository;
    SubmissionRepository submissionRepository;

    String password = "pass";
    String thumbnailUrl1 = "https://vtiacademy.edu.vn/upload/images/artboard-1-copy-7-100.jpg";
    String thumbnailUrl2 = "https://letdiv.com/wp-content/uploads/2024/04/khoa-hoc-react.png";

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
                            .password(passwordEncoder.encode("admin"))
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
                            .role(UserRole.LEARNER)
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
                            .role(UserRole.LEARNER)
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
                            .price(new BigDecimal("99.99"))
                            .discount(new BigDecimal("10.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("iOS App Development with Swift")
                            .description("Build iOS apps from scratch using Swift and Xcode")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .price(new BigDecimal("79.99"))
                            .discount(new BigDecimal("15.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Data Science Fundamentals")
                            .description("Learn Python, NumPy, Pandas, and data visualization")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.BEGINNER)
                            .category(categories.get(2))
                            .price(new BigDecimal("89.99"))
                            .discount(new BigDecimal("0.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Machine Learning with Python")
                            .description("Master machine learning algorithms and techniques")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .price(new BigDecimal("0.00"))
                            .discount(new BigDecimal("0.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("AWS Certified Solutions Architect")
                            .description("Prepare for AWS certification with hands-on projects")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .price(new BigDecimal("149.99"))
                            .discount(new BigDecimal("50.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    // Thêm 10 khóa học mới
                    Course.builder()
                            .title("Flutter Mobile App Development")
                            .description("Build cross-platform mobile apps with Flutter and Dart")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .price(new BigDecimal("50000"))
                            .discount(new BigDecimal("0.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Advanced JavaScript Patterns")
                            .description("Master advanced JavaScript concepts and design patterns")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(0))
                            .price(new BigDecimal("75000"))
                            .discount(new BigDecimal("100.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Python for Data Analysis")
                            .description("Learn data analysis with Python, Pandas, and NumPy")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.BEGINNER)
                            .category(categories.get(2))
                            .price(new BigDecimal("25000"))
                            .discount(new BigDecimal("25.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Deep Learning with TensorFlow")
                            .description("Build and train neural networks with TensorFlow")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .price(new BigDecimal("100000"))
                            .discount(new BigDecimal("30.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("DevOps with Docker and Kubernetes")
                            .description("Master containerization and orchestration")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .price(new BigDecimal("85000"))
                            .discount(new BigDecimal("15.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("React Native Mobile Development")
                            .description("Build native mobile apps with React Native")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .price(new BigDecimal("65000"))
                            .discount(new BigDecimal("45.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Full Stack Development with MERN")
                            .description("Build full stack applications with MongoDB, Express, React, and Node.js")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(0))
                            .price(new BigDecimal("90000"))
                            .discount(new BigDecimal("20.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Big Data Processing with Spark")
                            .description("Process and analyze big data with Apache Spark")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(2))
                            .price(new BigDecimal("95000"))
                            .discount(new BigDecimal("10.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Blockchain Development")
                            .description("Learn blockchain development and smart contracts")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(0))
                            .price(new BigDecimal("80000"))
                            .discount(new BigDecimal("35.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Game Development with Unity")
                            .description("Create games using Unity and C#")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .price(new BigDecimal("70000"))
                            .discount(new BigDecimal("40.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build()
            );
            courseRepository.saveAll(courses);

            // Tạo material sections cho tất cả khóa học
            List<MaterialSection> materialSections = List.of(
                    // Web Development Course
                    MaterialSection.builder()
                            .course(courses.get(0))
                            .title("Introduction to Web Development")
                            .orderIndex(1)
                            .previewable(true)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(0))
                            .title("HTML & CSS Fundamentals")
                            .orderIndex(2)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(0))
                            .title("JavaScript Basics")
                            .orderIndex(3)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(0))
                            .title("React.js Introduction")
                            .orderIndex(4)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(0))
                            .title("Backend Development with Node.js")
                            .orderIndex(5)
                            .previewable(false)
                            .build(),

                    // iOS Development Course
                    MaterialSection.builder()
                            .course(courses.get(1))
                            .title("Introduction to iOS Development")
                            .orderIndex(1)
                            .previewable(true)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(1))
                            .title("Swift Fundamentals")
                            .orderIndex(2)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(1))
                            .title("UIKit Basics")
                            .orderIndex(3)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(1))
                            .title("SwiftUI Introduction")
                            .orderIndex(4)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(1))
                            .title("iOS App Architecture")
                            .orderIndex(5)
                            .previewable(false)
                            .build(),

                    // Data Science Course
                    MaterialSection.builder()
                            .course(courses.get(2))
                            .title("Introduction to Data Science")
                            .orderIndex(1)
                            .previewable(true)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(2))
                            .title("Python for Data Science")
                            .orderIndex(2)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(2))
                            .title("NumPy and Pandas")
                            .orderIndex(3)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(2))
                            .title("Data Visualization")
                            .orderIndex(4)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(2))
                            .title("Data Analysis Projects")
                            .orderIndex(5)
                            .previewable(false)
                            .build(),

                    // Machine Learning Course
                    MaterialSection.builder()
                            .course(courses.get(3))
                            .title("Introduction to Machine Learning")
                            .orderIndex(1)
                            .previewable(true)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(3))
                            .title("Supervised Learning")
                            .orderIndex(2)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(3))
                            .title("Unsupervised Learning")
                            .orderIndex(3)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(3))
                            .title("Deep Learning Basics")
                            .orderIndex(4)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(3))
                            .title("ML Project Implementation")
                            .orderIndex(5)
                            .previewable(false)
                            .build(),

                    // AWS Course
                    MaterialSection.builder()
                            .course(courses.get(4))
                            .title("Introduction to AWS")
                            .orderIndex(1)
                            .previewable(true)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(4))
                            .title("EC2 and VPC")
                            .orderIndex(2)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(4))
                            .title("S3 and Storage Services")
                            .orderIndex(3)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(4))
                            .title("Database Services")
                            .orderIndex(4)
                            .previewable(false)
                            .build(),
                    MaterialSection.builder()
                            .course(courses.get(4))
                            .title("Security and Compliance")
                            .orderIndex(5)
                            .previewable(false)
                            .build()
            );
            materialSectionRepository.saveAll(materialSections);

            // Tạo material sections cho 10 khóa học mới
            List<MaterialSection> newMaterialSections = new ArrayList<>();
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
                    newMaterialSections.add(MaterialSection.builder()
                            .course(course)
                            .title(course.getTitle() + " - " + sectionTitles[j])
                            .orderIndex(j + 1)
                            .previewable(j == 0) // Chỉ section đầu tiên là previewable
                            .build());
                }
            }
            materialSectionRepository.saveAll(newMaterialSections);

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
            for (MaterialSection section : materialSections) {
                for (int i = 0; i < 5; i++) {
                    lessons.add(Lesson.builder()
                            .materialSection(section)
                            .title(section.getTitle() + " - " + lessonTypes[i])
                            .orderIndex(i + 1)
                            .defaultCode("// Default code for " + section.getTitle() + " - " + lessonTypes[i])
                            .duration(10)
                            .build());
                }
            }

            // Tạo lessons cho material sections mới
            for (MaterialSection section : newMaterialSections) {
                for (int i = 0; i < 5; i++) {
                    lessons.add(Lesson.builder()
                            .materialSection(section)
                            .title(section.getTitle() + " - " + lessonTypes[i])
                            .orderIndex(i + 1)
                            .defaultCode("// Default code for " + section.getTitle() + " - " + lessonTypes[i])
                            .duration(10)
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
                        .content("This is the theory content for " + lesson.getTitle() + ". Learn about the concepts and principles...")
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

            // Tạo submissions cho exercises
            List<Submission> submissions = new ArrayList<>();
            for (Exercise exercise : exercises) {
                for (User instructor : instructors) {
                    // Successful submission
                    submissions.add(Submission.builder()
                            .exercise(exercise)
                            .learner(instructor)
                            .code("// Solution for " + exercise.getTitle() + "\n" +
                                    "function solution(input) {\n" +
                                    "    // Implementation\n" +
                                    "    return result;\n" +
                                    "}")
                            .executionTime((float) (Math.random() * 1000))
                            .memoryUsage((float) (Math.random() * 100))
                            .status(SubmissionStatus.PASSED)
                            .passRate(1.0f)
                            .testCaseCount(4)
                            .build());

                    // Failed submission
                    submissions.add(Submission.builder()
                            .exercise(exercise)
                            .learner(instructor)
                            .code("// Failed solution for " + exercise.getTitle() + "\n" +
                                    "function solution(input) {\n" +
                                    "    // Incorrect implementation\n" +
                                    "    return wrongResult;\n" +
                                    "}")
                            .executionTime((float) (Math.random() * 1000))
                            .memoryUsage((float) (Math.random() * 100))
                            .status(SubmissionStatus.FAILED)
                            .passRate(0.5f)
                            .testCaseCount(4)
                            .build());

                    // Time limit exceeded submission
                    submissions.add(Submission.builder()
                            .exercise(exercise)
                            .learner(instructor)
                            .code("// Time limit exceeded solution for " + exercise.getTitle() + "\n" +
                                    "function solution(input) {\n" +
                                    "    // Inefficient implementation\n" +
                                    "    while(true) { /* infinite loop */ }\n" +
                                    "}")
                            .executionTime(2000.0f)
                            .memoryUsage((float) (Math.random() * 100))
                            .status(SubmissionStatus.PENDING)
                            .passRate(0.0f)
                            .testCaseCount(4)
                            .build());
                }
            }
            submissionRepository.saveAll(submissions);

            // Tạo vouchers
            List<Voucher> vouchers = List.of(
                    Voucher.builder()
                            .code("WELCOME10")
                            .description("Welcome discount 10%")
                            .discountType(DiscountType.PERCENT)
                            .discountValue(new BigDecimal("10"))
                            .minOrderAmount(new BigDecimal("50"))
                            .maxDiscountValue(new BigDecimal("20"))
                            .startDate(LocalDateTime.now())
                            .endDate(LocalDateTime.now().plusMonths(1))
                            .usageLimit(100)
                            .isActive(true)
                            .build(),
                    Voucher.builder()
                            .code("SUMMER20")
                            .description("Summer special 20% off")
                            .discountType(DiscountType.PERCENT)
                            .discountValue(new BigDecimal("20"))
                            .minOrderAmount(new BigDecimal("100"))
                            .maxDiscountValue(new BigDecimal("50"))
                            .startDate(LocalDateTime.now())
                            .endDate(LocalDateTime.now().plusMonths(2))
                            .usageLimit(50)
                            .isActive(true)
                            .build()
            );
            voucherRepository.saveAll(vouchers);

            // Tạo progress tracking
            List<ProgressTracking> progressTrackings = new ArrayList<>();
            progressTrackings.add(ProgressTracking.builder()
                    .user(instructors.get(0))
                    .course(courses.get(0))
                    .completionPercentage(100f)
                    .lastAccessed(LocalDateTime.now().minusDays((long) (Math.random() * 10)))
                    .build());
            for (Course course : courses) {
                for (User instructor : instructors) {
                    progressTrackings.add(ProgressTracking.builder()
                            .user(instructor)
                            .course(course)
                            .completionPercentage((float) (Math.random() * 100))
                            .lastAccessed(LocalDateTime.now().minusDays((long) (Math.random() * 10)))
                            .build());
                }
            }
            progressTrackingRepository.saveAll(progressTrackings);

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
                            LessonProgressStatus.COMPLETED : LessonProgressStatus.IN_PROGRESS;
                    
                    LocalDateTime startedAt = LocalDateTime.now().minusDays((long) (Math.random() * 10));
                    LocalDateTime completedAt = status == LessonProgressStatus.COMPLETED ? 
                            startedAt.plusHours((long) (Math.random() * 24)) : null;
                    
                    lessonProgresses.add(LessonProgress.builder()
                            .user(instructor)
                            .lesson(lesson)
                            .status(status)
                            .expGained(status == LessonProgressStatus.COMPLETED ? 
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
                            .price(new BigDecimal("85000"))
                            .discount(new BigDecimal("20.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Android Development with Kotlin")
                            .description("Create Android apps using Kotlin and Android Studio")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .price(new BigDecimal("75000"))
                            .discount(new BigDecimal("15.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Natural Language Processing")
                            .description("Learn NLP techniques and build language models")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .price(new BigDecimal("95000"))
                            .discount(new BigDecimal("25.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Cloud Architecture with Azure")
                            .description("Design and implement cloud solutions on Microsoft Azure")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .price(new BigDecimal("90000"))
                            .discount(new BigDecimal("30.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Vue.js Frontend Development")
                            .description("Build modern web applications with Vue.js")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(0))
                            .price(new BigDecimal("65000"))
                            .discount(new BigDecimal("10.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("iOS App Development with SwiftUI")
                            .description("Create modern iOS apps using SwiftUI framework")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .price(new BigDecimal("80000"))
                            .discount(new BigDecimal("40.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Data Engineering with Python")
                            .description("Build data pipelines and ETL processes")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(2))
                            .price(new BigDecimal("85000"))
                            .discount(new BigDecimal("35.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Computer Vision with OpenCV")
                            .description("Learn image processing and computer vision")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .price(new BigDecimal("90000"))
                            .discount(new BigDecimal("45.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Google Cloud Platform")
                            .description("Master cloud computing with Google Cloud Platform")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .price(new BigDecimal("95000"))
                            .discount(new BigDecimal("50.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Angular Advanced Patterns")
                            .description("Master advanced Angular patterns and best practices")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(0))
                            .price(new BigDecimal("70000"))
                            .discount(new BigDecimal("20.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Cross-Platform Mobile Development")
                            .description("Build mobile apps for iOS and Android using Xamarin")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(1))
                            .price(new BigDecimal("75000"))
                            .discount(new BigDecimal("25.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Big Data Analytics with Hadoop")
                            .description("Process and analyze big data using Hadoop ecosystem")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(2))
                            .price(new BigDecimal("85000"))
                            .discount(new BigDecimal("30.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Reinforcement Learning")
                            .description("Learn reinforcement learning algorithms and applications")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .price(new BigDecimal("90000"))
                            .discount(new BigDecimal("35.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Serverless Architecture")
                            .description("Build serverless applications with AWS Lambda")
                            .thumbnailUrl(thumbnailUrl2)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(4))
                            .price(new BigDecimal("80000"))
                            .discount(new BigDecimal("40.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Full Stack Development with Django")
                            .description("Build web applications with Django and React")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.INTERMEDIATE)
                            .category(categories.get(0))
                            .price(new BigDecimal("75000"))
                            .discount(new BigDecimal("45.00"))
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build()
            );
            courseRepository.saveAll(additionalCourses);

            // Tạo material sections cho 15 khóa học mới
            List<MaterialSection> additionalMaterialSections = new ArrayList<>();
            String[] additionalSectionTitles = {
                "Introduction and Setup",
                "Core Concepts",
                "Advanced Topics",
                "Practical Projects",
                "Best Practices and Optimization"
            };

            for (Course course : additionalCourses) {
                for (int j = 0; j < 5; j++) {
                    additionalMaterialSections.add(MaterialSection.builder()
                            .course(course)
                            .title(course.getTitle() + " - " + additionalSectionTitles[j])
                            .orderIndex(j + 1)
                            .previewable(j == 0)
                            .build());
                }
            }
            materialSectionRepository.saveAll(additionalMaterialSections);

            // Tạo lessons cho material sections mới
            List<Lesson> additionalLessons = new ArrayList<>();
            String[] additionalLessonTypes = {
                "Overview and Introduction",
                "Basic Concepts",
                "Hands-on Practice",
                "Advanced Techniques",
                "Project Work"
            };

            for (MaterialSection section : additionalMaterialSections) {
                for (int i = 0; i < 5; i++) {
                    additionalLessons.add(Lesson.builder()
                            .materialSection(section)
                            .title(section.getTitle() + " - " + additionalLessonTypes[i])
                            .orderIndex(i + 1)
                            .defaultCode("// Default code for " + section.getTitle() + " - " + additionalLessonTypes[i])
                            .duration(10)
                            .build());
                }
            }
            lessonRepository.saveAll(additionalLessons);

            // Tạo theories cho lessons mới
            List<Theory> additionalTheories = new ArrayList<>();
            for (Lesson lesson : additionalLessons) {
                additionalTheories.add(Theory.builder()
                        .lesson(lesson)
                        .title(lesson.getTitle() + " - Theory")
                        .content("This is the theory content for " + lesson.getTitle() + ". Learn about the concepts and principles...")
                        .build());
            }
            theoryRepository.saveAll(additionalTheories);

            // Tạo exercises cho lessons mới
            List<Exercise> additionalExercises = new ArrayList<>();
            for (Lesson lesson : additionalLessons) {
                additionalExercises.add(Exercise.builder()
                        .lesson(lesson)
                        .title(lesson.getTitle() + " - Exercise")
                        .expReward(100 * lesson.getOrderIndex())
                        .instruction("Complete the exercise for " + lesson.getTitle() + ". Practice what you've learned...")
                        .build());
            }
            exerciseRepository.saveAll(additionalExercises);

            // Tạo exercise tasks cho exercises mới
            List<ExerciseTask> additionalExerciseTasks = new ArrayList<>();
            for (Exercise exercise : additionalExercises) {
                additionalExerciseTasks.add(ExerciseTask.builder()
                        .exercise(exercise)
                        .description("Task 1: Implement the basic functionality for " + exercise.getTitle() + "\n" +
                                "Requirements:\n" +
                                "1. Create a function that handles the main logic\n" +
                                "2. Implement error handling\n" +
                                "3. Add input validation\n" +
                                "4. Write unit tests")
                        .build());
                additionalExerciseTasks.add(ExerciseTask.builder()
                        .exercise(exercise)
                        .description("Task 2: Optimize the solution for " + exercise.getTitle() + "\n" +
                                "Requirements:\n" +
                                "1. Improve time complexity\n" +
                                "2. Reduce memory usage\n" +
                                "3. Add comments and documentation\n" +
                                "4. Handle edge cases")
                        .build());
            }
            exerciseTaskRepository.saveAll(additionalExerciseTasks);

            // Tạo test cases cho exercises mới
            List<TestCase> additionalTestCases = new ArrayList<>();
            for (Exercise exercise : additionalExercises) {
                // Public test cases
                additionalTestCases.add(TestCase.builder()
                        .exercise(exercise)
                        .input("test input 1")
                        .expectedOutput("expected output 1")
                        .priority(TestCasePriority.REQUIRED)
                        .isPublic(true)
                        .build());
                additionalTestCases.add(TestCase.builder()
                        .exercise(exercise)
                        .input("test input 2")
                        .expectedOutput("expected output 2")
                        .priority(TestCasePriority.REQUIRED)
                        .isPublic(true)
                        .build());
                
                // Private test cases
                additionalTestCases.add(TestCase.builder()
                        .exercise(exercise)
                        .input("private test input 1")
                        .expectedOutput("private expected output 1")
                        .priority(TestCasePriority.REQUIRED)
                        .isPublic(false)
                        .build());
                additionalTestCases.add(TestCase.builder()
                        .exercise(exercise)
                        .input("private test input 2")
                        .expectedOutput("private expected output 2")
                        .priority(TestCasePriority.REQUIRED)
                        .isPublic(false)
                        .build());
            }
            testCaseRepository.saveAll(additionalTestCases);

            // Tạo submissions cho exercises mới
            List<Submission> additionalSubmissions = new ArrayList<>();
            for (Exercise exercise : additionalExercises) {
                for (User instructor : instructors) {
                    // Successful submission
                    additionalSubmissions.add(Submission.builder()
                            .exercise(exercise)
                            .learner(instructor)
                            .code("// Solution for " + exercise.getTitle() + "\n" +
                                    "function solution(input) {\n" +
                                    "    // Implementation\n" +
                                    "    return result;\n" +
                                    "}")
                            .executionTime((float) (Math.random() * 1000))
                            .memoryUsage((float) (Math.random() * 100))
                            .status(SubmissionStatus.PASSED)
                            .passRate(1.0f)
                            .testCaseCount(4)
                            .build());

                    // Failed submission
                    additionalSubmissions.add(Submission.builder()
                            .exercise(exercise)
                            .learner(instructor)
                            .code("// Failed solution for " + exercise.getTitle() + "\n" +
                                    "function solution(input) {\n" +
                                    "    // Incorrect implementation\n" +
                                    "    return wrongResult;\n" +
                                    "}")
                            .executionTime((float) (Math.random() * 1000))
                            .memoryUsage((float) (Math.random() * 100))
                            .status(SubmissionStatus.FAILED)
                            .passRate(0.5f)
                            .testCaseCount(4)
                            .build());

                    // Time limit exceeded submission
                    additionalSubmissions.add(Submission.builder()
                            .exercise(exercise)
                            .learner(instructor)
                            .code("// Time limit exceeded solution for " + exercise.getTitle() + "\n" +
                                    "function solution(input) {\n" +
                                    "    // Inefficient implementation\n" +
                                    "    while(true) { /* infinite loop */ }\n" +
                                    "}")
                            .executionTime(2000.0f)
                            .memoryUsage((float) (Math.random() * 100))
                            .status(SubmissionStatus.PENDING)
                            .passRate(0.0f)
                            .testCaseCount(4)
                            .build());
                }
            }
            submissionRepository.saveAll(additionalSubmissions);

            // Tạo progress tracking cho khóa học mới
            List<ProgressTracking> additionalProgressTrackings = new ArrayList<>();
            for (Course course : additionalCourses) {
                for (User instructor : instructors) {
                    additionalProgressTrackings.add(ProgressTracking.builder()
                            .user(instructor)
                            .course(course)
                            .completionPercentage((float) (Math.random() * 100))
                            .lastAccessed(LocalDateTime.now().minusDays((long) (Math.random() * 10)))
                            .build());
                }
            }
            progressTrackingRepository.saveAll(additionalProgressTrackings);

            // Tạo course ratings cho khóa học mới
            List<CourseRating> additionalCourseRatings = new ArrayList<>();
            for (Course course : additionalCourses) {
                for (User instructor : instructors) {
                    additionalCourseRatings.add(CourseRating.builder()
                            .user(instructor)
                            .course(course)
                            .rating(Math.round((1.0f + (float) (Math.random() * 4.0f)) * 10.0f) / 10.0f)
                            .comment("Great course! " + course.getTitle() + " is very informative and well-structured.")
                            .build());
                }
            }
            courseRatingRepository.saveAll(additionalCourseRatings);

            // Tạo lesson progress cho lessons mới
            List<LessonProgress> additionalLessonProgresses = new ArrayList<>();
            for (Lesson lesson : additionalLessons) {
                for (User instructor : instructors) {
                    LessonProgressStatus status = Math.random() > 0.5 ? 
                            LessonProgressStatus.COMPLETED : LessonProgressStatus.IN_PROGRESS;
                    
                    LocalDateTime startedAt = LocalDateTime.now().minusDays((long) (Math.random() * 10));
                    LocalDateTime completedAt = status == LessonProgressStatus.COMPLETED ? 
                            startedAt.plusHours((long) (Math.random() * 24)) : null;
                    
                    additionalLessonProgresses.add(LessonProgress.builder()
                            .user(instructor)
                            .lesson(lesson)
                            .status(status)
                            .expGained(status == LessonProgressStatus.COMPLETED ? 
                                    (int) (Math.random() * 100) + 50 : 0)
                            .startedAt(startedAt)
                            .completedAt(completedAt)
                            .build());
                }
            }
            lessonProgressRepository.saveAll(additionalLessonProgresses);

            log.info("Dummy data has been initialized successfully");
            log.info("Additional dummy data has been initialized successfully");
        };
    }
} 