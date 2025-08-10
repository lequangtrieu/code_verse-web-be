package codeverse.com.web_be.service.RankingService;

import codeverse.com.web_be.dto.response.RankingResponse.RankingDTO;
import codeverse.com.web_be.enums.LessonProgressStatus;
import codeverse.com.web_be.enums.PeriodType;
import codeverse.com.web_be.repository.LessonProgressRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Pageable;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RankingService implements IRankingService {

    private final LessonProgressRepository lessonProgressRepository;

    @Override
    public List<RankingDTO> getUserExpRankingByPeriod(PeriodType period, int limit) {
        ZoneId zone = ZoneId.of("Asia/Ho_Chi_Minh");
        LocalDate today = LocalDate.now(zone);

        LocalDateTime start = null;
        LocalDateTime end   = null;

        switch (period) {
            case DAY -> {
                start = today.atStartOfDay();
                end   = start.plusDays(1);
            }
            case WEEK -> {
                LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
                start = startOfWeek.atStartOfDay();
                end   = start.plusWeeks(1);
            }
            case MONTH -> {
                LocalDate firstOfMonth = today.withDayOfMonth(1);
                start = firstOfMonth.atStartOfDay();
                end   = firstOfMonth.plusMonths(1).atStartOfDay();
            }
            case YEAR -> {
                LocalDate firstOfYear = today.withDayOfYear(1);
                start = firstOfYear.atStartOfDay();
                end   = firstOfYear.plusYears(1).atStartOfDay();
            }
            case ALL -> {
                start = null;
                end   = null;
            }
        }

        Pageable pageable = PageRequest.of(0, Math.max(1, limit));
        return lessonProgressRepository.findUserRankingByPeriod(
                start, end, LessonProgressStatus.PASSED, pageable
        );
    }
}