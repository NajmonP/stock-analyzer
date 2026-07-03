package io.github.pavelnajmon.stockanalyzer.mapper;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.entity.MarketDay;
import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;

import java.time.Instant;

public final class MarketDayMapper {
    private MarketDayMapper() {}

    public static MarketDay toEntity(MarketDayDto marketDayDto) {
        MarketDay marketDay = new MarketDay();

        marketDay.setDate(marketDayDto.date());
        marketDay.setOpenPrice(marketDayDto.openPrice());
        marketDay.setHighPrice(marketDayDto.highPrice());
        marketDay.setLowPrice(marketDayDto.lowPrice());
        marketDay.setClosePrice(marketDayDto.closePrice());
        marketDay.setVolume(marketDayDto.volume());
        marketDay.setCreatedAt(Instant.now());

        return marketDay;
    }
}
