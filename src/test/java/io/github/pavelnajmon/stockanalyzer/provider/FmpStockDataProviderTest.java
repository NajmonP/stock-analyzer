package io.github.pavelnajmon.stockanalyzer.provider;

import io.github.pavelnajmon.stockanalyzer.configuration.FmpProperties;
import io.github.pavelnajmon.stockanalyzer.exception.DataNotProvidedException;
import io.github.pavelnajmon.stockanalyzer.exception.StockNotFoundException;
import io.github.pavelnajmon.stockanalyzer.mapper.FmpClientExceptionMapper;
import io.github.pavelnajmon.stockanalyzer.mapper.FmpStockDataMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.FmpProfileDataResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.FmpRatiosResponse;
import io.github.pavelnajmon.stockanalyzer.model.dto.StockDataDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.ExpectedCount.once;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FmpStockDataProviderTest {

    private static final String BASE_URL = "https://financialmodelingprep.com/stable";
    private static final String API_KEY = "test-api-key";

    private MockRestServiceServer server;
    private FmpStockDataMapper fmpStockDataMapper;
    private FmpClientExceptionMapper fmpClientExceptionMapper;
    private FmpStockDataProvider provider;

    @BeforeEach
    void setUp() {
        RestClient.Builder restClientBuilder = RestClient.builder();
        server = MockRestServiceServer.bindTo(restClientBuilder).build();

        FmpProperties fmpProperties = new FmpProperties();
        fmpProperties.setBaseUrl(BASE_URL);
        fmpProperties.setApiKey(API_KEY);

        fmpStockDataMapper = mock(FmpStockDataMapper.class);
        fmpClientExceptionMapper = mock(FmpClientExceptionMapper.class);

        provider = new FmpStockDataProvider(
                fmpProperties,
                restClientBuilder,
                fmpStockDataMapper,
                fmpClientExceptionMapper
        );
    }

    @Test
    void getStockData_shouldReturnMappedStockDataWhenProfileAndRatiosExist() {
        // given
        StockDataDto expectedStockData = mock(StockDataDto.class);

        String profileJson = """
                [
                  {
                    "symbol": "AAPL",
                    "companyName": "Apple Inc."
                  }
                ]
                """;

        String ratiosJson = """
                [
                  {
                    "peRatioTTM": 25.5
                  }
                ]
                """;

        server.expect(once(), requestTo(BASE_URL + "/profile?symbol=AAPL&apikey=" + API_KEY))
                .andRespond(withSuccess(profileJson, MediaType.APPLICATION_JSON));

        server.expect(once(), requestTo(BASE_URL + "/ratios-ttm?symbol=AAPL&apikey=" + API_KEY))
                .andRespond(withSuccess(ratiosJson, MediaType.APPLICATION_JSON));

        when(fmpStockDataMapper.toStockData(
                any(FmpProfileDataResponse.class),
                any(FmpRatiosResponse.class)
        )).thenReturn(expectedStockData);

        // when
        StockDataDto result = provider.getStockData("AAPL");

        // then
        assertThat(result).isSameAs(expectedStockData);

        verify(fmpStockDataMapper).toStockData(
                any(FmpProfileDataResponse.class),
                any(FmpRatiosResponse.class)
        );

        verifyNoInteractions(fmpClientExceptionMapper);

        server.verify();
    }

    @Test
    void getStockData_shouldReturnMappedStockDataWhenRatiosAreEmpty() {
        // given
        StockDataDto expectedStockData = mock(StockDataDto.class);

        String profileJson = """
                [
                  {
                    "symbol": "AAPL",
                    "companyName": "Apple Inc."
                  }
                ]
                """;

        String ratiosJson = "[]";

        server.expect(once(), requestTo(BASE_URL + "/profile?symbol=AAPL&apikey=" + API_KEY))
                .andRespond(withSuccess(profileJson, MediaType.APPLICATION_JSON));

        server.expect(once(), requestTo(BASE_URL + "/ratios-ttm?symbol=AAPL&apikey=" + API_KEY))
                .andRespond(withSuccess(ratiosJson, MediaType.APPLICATION_JSON));

        when(fmpStockDataMapper.toStockData(
                any(FmpProfileDataResponse.class),
                isNull()
        )).thenReturn(expectedStockData);

        // when
        StockDataDto result = provider.getStockData("AAPL");

        // then
        assertThat(result).isSameAs(expectedStockData);

        verify(fmpStockDataMapper).toStockData(
                any(FmpProfileDataResponse.class),
                isNull()
        );

        verifyNoInteractions(fmpClientExceptionMapper);

        server.verify();
    }

    @Test
    void getStockData_shouldThrowExceptionWhenProfileResponseIsEmpty() {
        // given
        String profileJson = "[]";

        String ratiosJson = """
                [
                  {
                    "peRatioTTM": 25.5
                  }
                ]
                """;

        server.expect(once(), requestTo(BASE_URL + "/profile?symbol=UNKNOWN&apikey=" + API_KEY))
                .andRespond(withSuccess(profileJson, MediaType.APPLICATION_JSON));

        server.expect(once(), requestTo(BASE_URL + "/ratios-ttm?symbol=UNKNOWN&apikey=" + API_KEY))
                .andRespond(withSuccess(ratiosJson, MediaType.APPLICATION_JSON));

        // when + then
        assertThatThrownBy(() -> provider.getStockData("UNKNOWN"))
                .isInstanceOf(StockNotFoundException.class);

        verifyNoInteractions(fmpStockDataMapper);
        verifyNoInteractions(fmpClientExceptionMapper);

        server.verify();
    }

    @Test
    void getStockData_shouldThrowExceptionWhenProfileResponseIsNull() {
        // given
        String ratiosJson = """
                [
                  {
                    "peRatioTTM": 25.5
                  }
                ]
                """;

        server.expect(once(), requestTo(BASE_URL + "/profile?symbol=AAPL&apikey=" + API_KEY))
                .andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

        server.expect(once(), requestTo(BASE_URL + "/ratios-ttm?symbol=AAPL&apikey=" + API_KEY))
                .andRespond(withSuccess(ratiosJson, MediaType.APPLICATION_JSON));

        // when + then
        assertThatThrownBy(() -> provider.getStockData("AAPL"))
                .isInstanceOf(StockNotFoundException.class);

        verifyNoInteractions(fmpStockDataMapper);
        verifyNoInteractions(fmpClientExceptionMapper);

        server.verify();
    }

    @Test
    void getStockData_shouldThrowMappedExceptionWhenProfileRequestFails() {
        // given
        DataNotProvidedException expectedException =
                new DataNotProvidedException("Financial data for ticker AAPL is not available");

        server.expect(once(), requestTo(BASE_URL + "/profile?symbol=AAPL&apikey=" + API_KEY))
                .andRespond(withStatus(HttpStatus.PAYMENT_REQUIRED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                  "Error Message": "This value set for 'symbol' is not available under your current subscription"
                                }
                                """));

        when(fmpClientExceptionMapper.mapFmpClientException(
                any(HttpClientErrorException.class),
                eq("AAPL")
        )).thenReturn(expectedException);

        // when + then
        assertThatThrownBy(() -> provider.getStockData("AAPL"))
                .isSameAs(expectedException);

        verify(fmpClientExceptionMapper).mapFmpClientException(
                any(HttpClientErrorException.class),
                eq("AAPL")
        );

        verifyNoInteractions(fmpStockDataMapper);

        server.verify();
    }

    @Test
    void getStockData_shouldThrowMappedExceptionWhenRatiosRequestFails() {
        // given
        DataNotProvidedException expectedException =
                new DataNotProvidedException("Financial ratios for ticker AAPL are not available");

        String profileJson = """
                [
                  {
                    "symbol": "AAPL",
                    "companyName": "Apple Inc."
                  }
                ]
                """;

        server.expect(once(), requestTo(BASE_URL + "/profile?symbol=AAPL&apikey=" + API_KEY))
                .andRespond(withSuccess(profileJson, MediaType.APPLICATION_JSON));

        server.expect(once(), requestTo(BASE_URL + "/ratios-ttm?symbol=AAPL&apikey=" + API_KEY))
                .andRespond(withStatus(HttpStatus.PAYMENT_REQUIRED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body("""
                                {
                                  "Error Message": "This value set for 'symbol' is not available under your current subscription"
                                }
                                """));

        when(fmpClientExceptionMapper.mapFmpClientException(
                any(HttpClientErrorException.class),
                eq("AAPL")
        )).thenReturn(expectedException);

        // when + then
        assertThatThrownBy(() -> provider.getStockData("AAPL"))
                .isSameAs(expectedException);

        verify(fmpClientExceptionMapper).mapFmpClientException(
                any(HttpClientErrorException.class),
                eq("AAPL")
        );

        verifyNoInteractions(fmpStockDataMapper);

        server.verify();
    }
}