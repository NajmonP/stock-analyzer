package io.github.pavelnajmon.stockanalyzer.mapper;

import io.github.pavelnajmon.stockanalyzer.model.dto.response.FmpProfileDataResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.FmpRatiosResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@Component
public class FmpStockDataMapper {

    public StockDataDto toStockData(FmpProfileDataResponse profile, FmpRatiosResponse ratios) {
        BigDecimal fiftyTwoWeekLow = parseRangeLow(profile.range());
        BigDecimal fiftyTwoWeekHigh = parseRangeHigh(profile.range());

        return new StockDataDto(
                profile.ticker(),
                profile.companyName(),
                profile.description(),
                profile.sector(),
                profile.industry(),
                profile.exchange(),
                profile.currency(),
                profile.websiteUrl(),
                profile.logoUrl(),
                profile.marketCapitalization(),
                fiftyTwoWeekLow,
                fiftyTwoWeekHigh,
                ratios != null ? ratios.peRatio() : null,
                ratios != null ? ratios.earningsPerShare() : null,
                ratios != null ? ratios.dividendYield() : null,
                profile.beta(),
                ratios != null ? ratios.debtCoverageRatio() : null,
                ratios != null ? ratios.freeCashFlowPerShare() : null,
                ratios != null ? ratios.operatingMargin() : null
        );
    }

    private BigDecimal parseRangeLow(String range) {
        if (range == null || !range.contains("-")) {
            return null;
        }

        String[] parts = range.split("-");
        return new BigDecimal(parts[0].trim());
    }

    private BigDecimal parseRangeHigh(String range) {
        if (range == null || !range.contains("-")) {
            return null;
        }

        String[] parts = range.split("-");
        return new BigDecimal(parts[1].trim());
    }
}
