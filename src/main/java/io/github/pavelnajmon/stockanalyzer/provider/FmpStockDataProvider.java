package io.github.pavelnajmon.stockanalyzer.provider;

import io.github.pavelnajmon.stockanalyzer.configuration.FmpProperties;
import io.github.pavelnajmon.stockanalyzer.exception.StockNotFoundException;
import io.github.pavelnajmon.stockanalyzer.mapper.FmpClientExceptionMapper;
import io.github.pavelnajmon.stockanalyzer.mapper.FmpStockDataMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.FmpProfileDataResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.FmpRatiosResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

@Component
public class FmpStockDataProvider implements StockDataProvider {

    private final FmpProperties fmpProperties;
    private final RestClient restClient;
    private final FmpStockDataMapper fmpStockDataMapper;
    private final FmpClientExceptionMapper fmpClientExceptionMapper;

    public FmpStockDataProvider(FmpProperties fmpProperties, RestClient.Builder restClientBuilder, FmpStockDataMapper fmpStockDataMapper, FmpClientExceptionMapper fmpClientExceptionMapper) {
        this.fmpProperties = fmpProperties;
        this.fmpClientExceptionMapper = fmpClientExceptionMapper;
        this.restClient = restClientBuilder
                .baseUrl(fmpProperties.getBaseUrl())
                .build();
        this.fmpStockDataMapper = fmpStockDataMapper;
    }

    @Override
    public StockDataDto getStockData(String ticker) {
        FmpProfileDataResponse[] profileResponse = fetchProfile(ticker);
        FmpRatiosResponse[] ratiosResponse = fetchRatios(ticker);

        if (profileResponse == null || profileResponse.length == 0) {
            throw new StockNotFoundException(ticker);
        }

        FmpProfileDataResponse profile = profileResponse[0];
        FmpRatiosResponse ratios = null;

        if (ratiosResponse != null && ratiosResponse.length > 0) {
            ratios = ratiosResponse[0];
        }

        return fmpStockDataMapper.toStockData(profile, ratios);
    }

    private FmpProfileDataResponse[] fetchProfile(String ticker) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/profile")
                            .queryParam("symbol", ticker)
                            .queryParam("apikey", fmpProperties.getApiKey())
                            .build())
                    .retrieve()
                    .body(FmpProfileDataResponse[].class);
        } catch (HttpClientErrorException ex) {
            throw fmpClientExceptionMapper.mapFmpClientException(ex, ticker);
        }
    }

    private FmpRatiosResponse[] fetchRatios(String ticker) {
        try {
            return restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/ratios-ttm")
                            .queryParam("symbol", ticker)
                            .queryParam("apikey", fmpProperties.getApiKey())
                            .build())
                    .retrieve()
                    .body(FmpRatiosResponse[].class);
        } catch (HttpClientErrorException ex) {
            throw fmpClientExceptionMapper.mapFmpClientException(ex, ticker);
        }
    }
}
