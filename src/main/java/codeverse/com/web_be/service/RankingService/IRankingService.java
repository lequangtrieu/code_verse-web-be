package codeverse.com.web_be.service.RankingService;

import codeverse.com.web_be.dto.response.RankingResponse.RankingDTO;
import codeverse.com.web_be.enums.PeriodType;

import java.util.List;

public interface IRankingService {
    List<RankingDTO> getUserExpRankingByPeriod(PeriodType period, int limit);
}
