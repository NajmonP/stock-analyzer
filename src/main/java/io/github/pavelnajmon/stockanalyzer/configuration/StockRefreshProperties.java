package io.github.pavelnajmon.stockanalyzer.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "stock.refresh")
public class StockRefreshProperties {

    private String marketDaysCron;
    private String stockDataCron;
    private String zone;
    private int stockDataLimit;

    public String getMarketDaysCron() {
        return marketDaysCron;
    }

    public void setMarketDaysCron(String marketDaysCron) {
        this.marketDaysCron = marketDaysCron;
    }

    public String getStockDataCron() {
        return stockDataCron;
    }

    public void setStockDataCron(String stockDataCron) {
        this.stockDataCron = stockDataCron;
    }

    public String getZone() {
        return zone;
    }

    public void setZone(String zone) {
        this.zone = zone;
    }

    public int getStockDataLimit() {
        return stockDataLimit;
    }

    public void setStockDataLimit(int stockDataLimit) {
        this.stockDataLimit = stockDataLimit;
    }
}