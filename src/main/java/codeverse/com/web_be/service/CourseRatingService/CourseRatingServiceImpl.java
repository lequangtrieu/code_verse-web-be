package codeverse.com.web_be.service.CourseRatingService;

import codeverse.com.web_be.dto.request.CourseRatingRequest.CourseRatingRequestDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.CourseRatingResponseDto;
import codeverse.com.web_be.dto.response.CourseRatingResponse.ReviewItem;
import codeverse.com.web_be.entity.Course;
import codeverse.com.web_be.entity.CourseRating;
import codeverse.com.web_be.entity.User;
import codeverse.com.web_be.repository.CourseRatingRepository;
import codeverse.com.web_be.repository.CourseRepository;
import codeverse.com.web_be.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CourseRatingServiceImpl implements CourseRatingService {

    private final CourseRatingRepository courseRatingRepository;
    private final UserRepository userRepository;
    private final CourseRepository courseRepository;

    @Override
    @Transactional
    public void submitRating(Long userId, CourseRatingRequestDto dto) {
        Optional<CourseRating> existing = courseRatingRepository.findByUserIdAndCourseId(userId, dto.getCourseId());

        if (existing.isPresent()) {
            CourseRating rating = existing.get();
            rating.setRating(dto.getRating());
            rating.setComment(dto.getComment());
            rating.setCreatedAt(LocalDateTime.now());
            courseRatingRepository.save(rating);
        } else {
            CourseRating newRating = CourseRating.builder()
                    .user(userRepository.getReferenceById(userId))
                    .course(courseRepository.getReferenceById(dto.getCourseId()))
                    .rating(dto.getRating())
                    .comment(dto.getComment())
                    .createdAt(LocalDateTime.now())
                    .build();
            courseRatingRepository.save(newRating);
        }
    }


    @Override
    public CourseRatingResponseDto getRatingsByCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        List<CourseRating> ratings = courseRatingRepository.findByCourse(course);

        if (ratings.isEmpty()) {
            return CourseRatingResponseDto.builder()
                    .average(0)
                    .totalReviews(0)
                    .distribution(Map.of())
                    .reviews(List.of())
                    .build();
        }

        int totalReviews = ratings.size();
        double average = ratings.stream().mapToDouble(CourseRating::getRating).average().orElse(0);

        Map<Integer, Integer> distribution = ratings.stream()
                .map(r -> Math.round(r.getRating()))
                .collect(Collectors.toMap(
                        Integer::intValue,
                        v -> 1,
                        Integer::sum
                ));

        List<ReviewItem> reviewItems = ratings.stream()
                .map(r -> ReviewItem.builder()
                        .id(r.getId())
                        .username(r.getUser().getUsername())
                        .userAvatar(r.getUser().getAvatar())
                        .rating(r.getRating())
                        .comment(r.getComment())
                        .createdAt(r.getCreatedAt())
                        .build())
                .collect(Collectors.toList());

        return CourseRatingResponseDto.builder()
                .average(average)
                .totalReviews(totalReviews)
                .distribution(distribution)
                .reviews(reviewItems)
                .build();
    }
    @Override
    public CourseRatingDto getUserRatingForCourse(Long courseId, Long userId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        CourseRating rating = courseRatingRepository.findByUserAndCourse(user, course)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        return new CourseRatingDto(
                rating.getId(),
                userId,
                courseId,
                rating.getRating(),
                rating.getComment()
        );
    }

    @Override
    public void updateRating(Long ratingId, CourseRatingRequestDto requestDto) {
        CourseRating rating = courseRatingRepository.findById(ratingId)
                .orElseThrow(() -> new RuntimeException("Rating not found"));

        rating.setRating(requestDto.getRating());
        rating.setComment(requestDto.getComment());
        rating.setCreatedAt(LocalDateTime.now());

        courseRatingRepository.save(rating);
    }
}
