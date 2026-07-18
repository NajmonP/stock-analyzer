package io.github.pavelnajmon.stockanalyzer.mapper;

import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.StockSummaryResponse;
import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.Instant;

@Component
public class StockMapper {

    public Stock toEntity(StockDataDto stockDataDto) {
        Stock stock = new Stock();
        setStockAttributes(stockDataDto, stock);
        return stock;
    }

    public void updateEntityFromDto(StockDataDto stockDataDto, Stock stock) {
        setStockAttributes(stockDataDto, stock);
    }

    public StockSummaryResponse toStockSummaryResponse(Stock stock, BigDecimal lastClosePrice) {
        return new StockSummaryResponse(
                stock.getId(),
                stock.getTicker(),
                stock.getCompanyName(),
                stock.getSector(),
                stock.getIndustry(),
                lastClosePrice,
                stock.getMarketCapitalization()
        );
    }

    public StockDataDto toDto(Stock stock) {
        return new StockDataDto(
                stock.getTicker(),
                stock.getCompanyName(),
                stock.getDescription(),
                stock.getSector(),
                stock.getIndustry(),
                stock.getExchange(),
                stock.getCurrency(),
                stock.getWebsiteUrl(),
                stock.getLogoUrl(),
                stock.getMarketCapitalization(),
                stock.getFiftyTwoWeekLow(),
                stock.getFiftyTwoWeekHigh(),
                stock.getPeRatio(),
                stock.getEarningsPerShare(),
                stock.getDividendYield(),
                stock.getBeta(),
                stock.getDebtServiceCoverageRatio(),
                stock.getFreeCashFlowPerShare(),
                stock.getOperatingMargin()
        );
    }

    private void setStockAttributes(StockDataDto stockDataDto, Stock stock) {
        stock.setTicker(stockDataDto.ticker());
        stock.setCompanyName(stockDataDto.companyName());
        stock.setDescription(stockDataDto.description());
        stock.setSector(stockDataDto.sector());
        stock.setIndustry(stockDataDto.industry());
        stock.setExchange(stockDataDto.exchange());
        stock.setCurrency(stockDataDto.currency());
        stock.setWebsiteUrl(stockDataDto.websiteUrl());
        stock.setLogoUrl(stockDataDto.logoUrl());
        stock.setMarketCapitalization(stockDataDto.marketCapitalization());
        stock.setFiftyTwoWeekLow(stockDataDto.fiftyTwoWeekLow());
        stock.setFiftyTwoWeekHigh(stockDataDto.fiftyTwoWeekHigh());
        stock.setPeRatio(stockDataDto.peRatio());
        stock.setEarningsPerShare(stockDataDto.earningsPerShare());
        stock.setDividendYield(stockDataDto.dividendYield());
        stock.setBeta(stockDataDto.beta());
        stock.setDebtServiceCoverageRatio(stockDataDto.debtServiceCoverageRatio());
        stock.setFreeCashFlowPerShare(stockDataDto.freeCashFlowPerShare());
        stock.setOperatingMargin(stockDataDto.operatingMargin());
        stock.setLastUpdatedAt(Instant.now());
    }
}
