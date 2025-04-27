package codeverse.com.web_be.config.SystemConfig;

import codeverse.com.web_be.entity.*;
import codeverse.com.web_be.enums.CourseLevel;
import codeverse.com.web_be.enums.DiscountType;
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
                            .username("dolv@gmail.com")
                            .password(passwordEncoder.encode(password))
                            .name("Lê Văn Độ")
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
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build(),
                    Course.builder()
                            .title("Machine Learning with Python")
                            .description("Master machine learning algorithms and techniques")
                            .thumbnailUrl(thumbnailUrl1)
                            .level(CourseLevel.ADVANCED)
                            .category(categories.get(3))
                            .price(new BigDecimal("129.99"))
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
                            .isPublished(true)
                            .instructor(instructors.get(0))
                            .build()
            );
            courseRepository.saveAll(courses);

            // Tạo material sections cho khóa học đầu tiên
            List<MaterialSection> materialSections = List.of(
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
                            .build()
            );
            materialSectionRepository.saveAll(materialSections);

            // Tạo lessons cho material section đầu tiên
            List<Lesson> lessons = List.of(
                    Lesson.builder()
                            .materialSection(materialSections.get(0))
                            .title("What is Web Development?")
                            .orderIndex(1)
                            .defaultCode("// Welcome to web development!")
                            .build(),
                    Lesson.builder()
                            .materialSection(materialSections.get(0))
                            .title("Web Development Tools")
                            .orderIndex(2)
                            .defaultCode("// Let's set up your development environment")
                            .build(),
                    Lesson.builder()
                            .materialSection(materialSections.get(0))
                            .title("Understanding the Web")
                            .orderIndex(3)
                            .defaultCode("// How the web works")
                            .build(),
                    Lesson.builder()
                            .materialSection(materialSections.get(0))
                            .title("Your First Web Page")
                            .orderIndex(4)
                            .defaultCode("<!DOCTYPE html>\n<html>\n<head>\n<title>My First Page</title>\n</head>\n<body>\n</body>\n</html>")
                            .build(),
                    Lesson.builder()
                            .materialSection(materialSections.get(0))
                            .title("Web Development Best Practices")
                            .orderIndex(5)
                            .defaultCode("// Follow these best practices")
                            .build()
            );
            lessonRepository.saveAll(lessons);

            // Tạo theories cho lessons
            List<Theory> theories = List.of(
                    Theory.builder()
                            .lesson(lessons.get(0))
                            .title("Introduction to Web Development")
                            .content("Web development is the work involved in developing a website for the Internet...")
                            .build(),
                    Theory.builder()
                            .lesson(lessons.get(1))
                            .title("Essential Development Tools")
                            .content("To start web development, you'll need some essential tools...")
                            .build(),
                    Theory.builder()
                            .lesson(lessons.get(2))
                            .title("How the Web Works")
                            .content("The web is a complex system of interconnected computers...")
                            .build(),
                    Theory.builder()
                            .lesson(lessons.get(3))
                            .title("HTML Basics")
                            .content("HTML is the standard markup language for creating web pages...")
                            .build(),
                    Theory.builder()
                            .lesson(lessons.get(4))
                            .title("Best Practices in Web Development")
                            .content("Following best practices ensures your code is maintainable...")
                            .build()
            );
            theoryRepository.saveAll(theories);

            // Tạo exercises cho lessons
            List<Exercise> exercises = List.of(
                    Exercise.builder()
                            .lesson(lessons.get(0))
                            .title("Web Development Quiz")
                            .expReward(100)
                            .instruction("Test your knowledge about web development basics")
                            .build(),
                    Exercise.builder()
                            .lesson(lessons.get(1))
                            .title("Tool Setup Challenge")
                            .expReward(150)
                            .instruction("Set up your development environment")
                            .build(),
                    Exercise.builder()
                            .lesson(lessons.get(2))
                            .title("Web Architecture Exercise")
                            .expReward(200)
                            .instruction("Draw the architecture of a simple web application")
                            .build(),
                    Exercise.builder()
                            .lesson(lessons.get(3))
                            .title("HTML Practice")
                            .expReward(250)
                            .instruction("Create a simple HTML page with basic elements")
                            .build(),
                    Exercise.builder()
                            .lesson(lessons.get(4))
                            .title("Code Review Exercise")
                            .expReward(300)
                            .instruction("Review and improve a given code snippet")
                            .build()
            );
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

            log.info("Dummy data has been initialized successfully");
        };
    }
} 