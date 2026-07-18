package io.github.pavelnajmon.stockanalyzer.mapper;

import io.github.pavelnajmon.stockanalyzer.model.dto.MarketDayDto;
import io.github.pavelnajmon.stockanalyzer.model.entity.MarketDay;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class MarketDayMapper {

    public MarketDay toEntity(MarketDayDto marketDayDto) {
        MarketDay marketDay = new MarketDay();

        setMarketDayAttributes(marketDayDto, marketDay);

        return marketDay;
    }

    public void updateEntityFromDto(MarketDayDto marketDayDto, MarketDay marketDay) {
        setMarketDayAttributes(marketDayDto, marketDay);
    }

    public MarketDayDto toDto(MarketDay marketDay) {
        return new MarketDayDto(
                marketDay.getDate(),
                marketDay.getOpenPrice(),
                marketDay.getHighPrice(),
                marketDay.getLowPrice(),
                marketDay.getClosePrice(),
                marketDay.getVolume()
        );
    }

    private void setMarketDayAttributes(MarketDayDto marketDayDto, MarketDay marketDay) {
        marketDay.setDate(marketDayDto.date());
        marketDay.setOpenPrice(marketDayDto.openPrice());
        marketDay.setHighPrice(marketDayDto.highPrice());
        marketDay.setLowPrice(marketDayDto.lowPrice());
        marketDay.setClosePrice(marketDayDto.closePrice());
        marketDay.setVolume(marketDayDto.volume());
        marketDay.setCreatedAt(Instant.now());
    }
}
