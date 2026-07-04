package io.github.pavelnajmon.stockanalyzer.provider;

import io.github.pavelnajmon.stockanalyzer.configuration.FmpProperties;
import io.github.pavelnajmon.stockanalyzer.mapper.FmpStockDataMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.FmpProfileDataResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.FmpRatiosResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class FmpStockDataProvider implements StockDataProvider {

    private final FmpProperties fmpProperties;
    private final RestClient restClient;
    private final FmpStockDataMapper fmpStockDataMapper;

    public FmpStockDataProvider(FmpProperties fmpProperties, RestClient.Builder restClientBuilder, FmpStockDataMapper fmpStockDataMapper) {
        this.fmpProperties = fmpProperties;
        this.restClient = restClientBuilder
                .baseUrl(fmpProperties.getBaseUrl())
                .build();
        this.fmpStockDataMapper = fmpStockDataMapper;
    }

    @Override
    public StockDataDto getStockData(String ticker) {
        FmpProfileDataResponse[] profileResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/profile")
                        .queryParam("symbol", ticker)
                        .queryParam("apikey", fmpProperties.getApiKey())
                        .build())
                .retrieve()
                .body(FmpProfileDataResponse[].class);

        FmpRatiosResponse[] ratiosResponse = restClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/ratios-ttm")
                        .queryParam("symbol", ticker)
                        .queryParam("apikey", fmpProperties.getApiKey())
                        .build())
                .retrieve()
                .body(FmpRatiosResponse[].class);

        if (profileResponse == null || profileResponse.length == 0) {
            throw new IllegalArgumentException("Stock profile not found for ticker: " + ticker);
        }

        FmpProfileDataResponse profile = profileResponse[0];
        FmpRatiosResponse ratios = null;

        if (ratiosResponse != null && ratiosResponse.length > 0) {
            ratios = ratiosResponse[0];
        }

        return fmpStockDataMapper.toStockData(profile, ratios);
    }
}
