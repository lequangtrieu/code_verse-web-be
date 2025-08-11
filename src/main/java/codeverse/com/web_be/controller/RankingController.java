package codeverse.com.web_be.controller;

import codeverse.com.web_be.dto.response.RankingResponse.RankingDTO;
import codeverse.com.web_be.enums.PeriodType;
import codeverse.com.web_be.service.RankingService.IRankingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/ranking")
@RequiredArgsConstructor
public class RankingController {

    private final IRankingService rankingService;

    @GetMapping("/user-exp")
    public List<RankingDTO> getTopRanking(
            @RequestParam(defaultValue = "ALL") PeriodType period,
            @RequestParam(defaultValue = "100") int limit
    ) {
        return rankingService.getUserExpRankingByPeriod(period, limit);
    }
}
