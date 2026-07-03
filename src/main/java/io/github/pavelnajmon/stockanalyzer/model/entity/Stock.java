package io.github.pavelnajmon.stockanalyzer.model.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stock", schema = "stock_analyzer")
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @Column(name = "ticker", nullable = false, length = 20, unique = true)
    private String ticker;

    @Column(name = "company_name", nullable = false, length = 255)
    private String companyName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "sector", length = 100)
    private String sector;

    @Column(name = "industry", length = 150)
    private String industry;

    @Column(name = "exchange", length = 100)
    private String exchange;

    @Column(name = "currency", length = 10)
    private String currency;

    @Column(name = "website_url", length = 500)
    private String websiteUrl;

    @Column(name = "logo_url", length = 500)
    private String logoUrl;

    @Column(name = "market_capitalization", precision = 24, scale = 2)
    private BigDecimal marketCapitalization;

    @Column(name = "fifty_two_week_low", precision = 18, scale = 4)
    private BigDecimal fiftyTwoWeekLow;

    @Column(name = "fifty_two_week_high", precision = 18, scale = 4)
    private BigDecimal fiftyTwoWeekHigh;

    @Column(name = "pe_ratio", precision = 18, scale = 4)
    private BigDecimal peRatio;

    @Column(name = "earnings_per_share", precision = 18, scale = 4)
    private BigDecimal earningsPerShare;

    @Column(name = "dividend_yield", precision = 10, scale = 6)
    private BigDecimal dividendYield;

    @Column(name = "beta", precision = 10, scale = 4)
    private BigDecimal beta;

    @Column(name = "debt_service_coverage_ratio", precision = 18, scale = 4)
    private BigDecimal debtServiceCoverageRatio;

    @Column(name = "free_cash_flow_per_share", precision = 18, scale = 4)
    private BigDecimal freeCashFlowPerShare;

    @Column(name = "operating_margin", precision = 10, scale = 6)
    private BigDecimal operatingMargin;

    @Column(name = "last_updated_at")
    private Instant lastUpdatedAt;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MarketDay> marketDays = new ArrayList<>();

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchlistStock> watchlistStocks = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTicker() {
        return ticker;
    }

    public void setTicker(String ticker) {
        this.ticker = ticker;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSector() {
        return sector;
    }

    public void setSector(String sector) {
        this.sector = sector;
    }

    public String getIndustry() {
        return industry;
    }

    public void setIndustry(String industry) {
        this.industry = industry;
    }

    public String getExchange() {
        return exchange;
    }

    public void setExchange(String exchange) {
        this.exchange = exchange;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public void setWebsiteUrl(String websiteUrl) {
        this.websiteUrl = websiteUrl;
    }

    public String getLogoUrl() {
        return logoUrl;
    }

    public void setLogoUrl(String logoUrl) {
        this.logoUrl = logoUrl;
    }

    public BigDecimal getMarketCapitalization() {
        return marketCapitalization;
    }

    public void setMarketCapitalization(BigDecimal marketCapitalization) {
        this.marketCapitalization = marketCapitalization;
    }

    public BigDecimal getFiftyTwoWeekLow() {
        return fiftyTwoWeekLow;
    }

    public void setFiftyTwoWeekLow(BigDecimal fiftyTwoWeekLow) {
        this.fiftyTwoWeekLow = fiftyTwoWeekLow;
    }

    public BigDecimal getFiftyTwoWeekHigh() {
        return fiftyTwoWeekHigh;
    }

    public void setFiftyTwoWeekHigh(BigDecimal fiftyTwoWeekHigh) {
        this.fiftyTwoWeekHigh = fiftyTwoWeekHigh;
    }

    public BigDecimal getPeRatio() {
        return peRatio;
    }

    public void setPeRatio(BigDecimal peRatio) {
        this.peRatio = peRatio;
    }

    public BigDecimal getEarningsPerShare() {
        return earningsPerShare;
    }

    public void setEarningsPerShare(BigDecimal earningsPerShare) {
        this.earningsPerShare = earningsPerShare;
    }

    public BigDecimal getDividendYield() {
        return dividendYield;
    }

    public void setDividendYield(BigDecimal dividendYield) {
        this.dividendYield = dividendYield;
    }

    public BigDecimal getBeta() {
        return beta;
    }

    public void setBeta(BigDecimal beta) {
        this.beta = beta;
    }

    public BigDecimal getDebtServiceCoverageRatio() {
        return debtServiceCoverageRatio;
    }

    public void setDebtServiceCoverageRatio(BigDecimal debtServiceCoverageRatio) {
        this.debtServiceCoverageRatio = debtServiceCoverageRatio;
    }

    public BigDecimal getFreeCashFlowPerShare() {
        return freeCashFlowPerShare;
    }

    public void setFreeCashFlowPerShare(BigDecimal freeCashFlowPerShare) {
        this.freeCashFlowPerShare = freeCashFlowPerShare;
    }

    public BigDecimal getOperatingMargin() {
        return operatingMargin;
    }

    public void setOperatingMargin(BigDecimal operatingMargin) {
        this.operatingMargin = operatingMargin;
    }

    public Instant getLastUpdatedAt() {
        return lastUpdatedAt;
    }

    public void setLastUpdatedAt(Instant lastUpdatedAt) {
        this.lastUpdatedAt = lastUpdatedAt;
    }

    public List<MarketDay> getMarketDays() {
        return marketDays;
    }

    public  void setMarketDays(List<MarketDay> marketDays) {
        this.marketDays = marketDays;
    }

    public void addMarketDay(MarketDay marketDay) {
        marketDays.add(marketDay);
        marketDay.setStock(this);
    }

    public List<WatchlistStock> getWatchlistStocks() {
        return watchlistStocks;
    }

    public void setWatchlistStocks(List<WatchlistStock> watchlistStocks) {
        this.watchlistStocks = watchlistStocks;
    }
}
