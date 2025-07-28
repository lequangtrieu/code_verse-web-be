package codeverse.com.web_be.service.CourseEnrollmentService;

import codeverse.com.web_be.dto.response.CourseEnrollmentResponse.CertificateInfoDTO;
import codeverse.com.web_be.dto.response.CourseResponse.LearnerResponse.MonthlyLearnerStatisticResponse;
import codeverse.com.web_be.dto.response.RankingResponse.RankingDTO;
import codeverse.com.web_be.entity.CourseEnrollment;
import codeverse.com.web_be.enums.PeriodType;
import codeverse.com.web_be.service.IGenericService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ICourseEnrollmentService extends IGenericService<CourseEnrollment, Long> {
    List<CourseEnrollment> findByUserId(Long userId);
    Optional<CourseEnrollment> findByUserIdAndCourseId(Long userId, Long courseId);
    List<MonthlyLearnerStatisticResponse> getMonthlyStats(String username);
    Float getUserCompletionPercentage(Long userId, Long courseId);
    List<RankingDTO> getUserExpRanking(int limit);
    List<RankingDTO> getUserRankingSince(LocalDateTime startTime, int limit);
    List<RankingDTO> getUserExpRankingByPeriod(PeriodType periodType, int limit);
    List<CourseEnrollment> getCompletedCoursesByUserId(Long userId);
    CertificateInfoDTO getCertificateInfo(Long userId, Long courseId);
    boolean isUserEnrolled(Long courseId, String username);
}