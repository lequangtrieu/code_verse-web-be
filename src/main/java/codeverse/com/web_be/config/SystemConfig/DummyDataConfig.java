package codeverse.com.web_be.config.SystemConfig;

import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.enums.CourseLevel;
import codeverse.com.web_be.enums.DiscountType;
import codeverse.com.web_be.enums.LessonProgressStatus;
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
    String password = "pass";
    String thumbnailUrl1 = "https://firebasestorage.googleapis.com/v0/b/codeverse-7830f.firebasestorage.app/o/images%2Fa53129ba-4965-4353-8bd2-6e917bdc9d3a_tutien.png?alt=media";
    String thumbnailUrl2 = "https://firebasestorage.googleapis.com/v0/b/codeverse-7830f.firebasestorage.app/o/images%2F5ec60b9c-00a0-4f6e-a8d5-5d217f286e4b_tutien2.png?alt=media";

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
                            .instructor(instructors.get(1))
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
                            .instructor(instructors.get(1))
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

            // Tạo lessons cho tất cả material sections
            List<Lesson> lessons = new ArrayList<>();
            for (MaterialSection section : materialSections) {
                for (int i = 1; i <= 5; i++) {
                    lessons.add(Lesson.builder()
                            .materialSection(section)
                            .title(section.getTitle() + " - Lesson " + i)
                            .orderIndex(i)
                            .defaultCode("// Default code for " + section.getTitle() + " - Lesson " + i)
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

            log.info("Dummy data has been initialized successfully");
        };
    }
} 