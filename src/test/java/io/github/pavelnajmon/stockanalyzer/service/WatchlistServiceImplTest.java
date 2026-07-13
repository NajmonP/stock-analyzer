package io.github.pavelnajmon.stockanalyzer.service;

import io.github.pavelnajmon.stockanalyzer.exception.DuplicateException;
import io.github.pavelnajmon.stockanalyzer.exception.EntityNotFoundException;
import io.github.pavelnajmon.stockanalyzer.mapper.WatchlistMapper;
import io.github.pavelnajmon.stockanalyzer.model.dto.request.CreateWatchlistRequest;
import io.github.pavelnajmon.stockanalyzer.model.dto.response.WatchlistResponse;
import io.github.pavelnajmon.stockanalyzer.model.entity.Stock;
import io.github.pavelnajmon.stockanalyzer.model.entity.User;
import io.github.pavelnajmon.stockanalyzer.model.entity.Watchlist;
import io.github.pavelnajmon.stockanalyzer.model.entity.WatchlistStock;
import io.github.pavelnajmon.stockanalyzer.repository.StockRepository;
import io.github.pavelnajmon.stockanalyzer.repository.WatchlistRepository;
import io.github.pavelnajmon.stockanalyzer.repository.WatchlistStockRepository;
import io.github.pavelnajmon.stockanalyzer.security.CustomUserDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WatchlistServiceImplTest {

    private WatchlistRepository watchlistRepository;
    private WatchlistStockRepository watchlistStockRepository;
    private WatchlistMapper watchlistMapper;
    private WatchlistServiceImpl watchlistService;
    private StockRepository stockRepository;

    @BeforeEach
    void setUp() {
        watchlistRepository = mock(WatchlistRepository.class);
        watchlistMapper = mock(WatchlistMapper.class);
        watchlistStockRepository = mock(WatchlistStockRepository.class);
        stockRepository = mock(StockRepository.class);
        watchlistService = new WatchlistServiceImpl(watchlistRepository, watchlistMapper,  watchlistStockRepository, stockRepository);
    }

    @Test
    void createWatchlist_shouldCreateWatchlist_whenNameIsUnique() {
        // given
        User user = mock(User.class);
        CustomUserDetails currentUser = mock(CustomUserDetails.class);

        CreateWatchlistRequest request = new CreateWatchlistRequest("Dividend stocks");
        WatchlistResponse expectedResponse = mock(WatchlistResponse.class);

        when(currentUser.getUser()).thenReturn(user);

        when(watchlistRepository.existsByUserAndName(user, "Dividend stocks"))
                .thenReturn(false);

        when(watchlistRepository.save(any(Watchlist.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        when(watchlistMapper.toResponse(any(Watchlist.class)))
                .thenReturn(expectedResponse);

        // when
        WatchlistResponse actualResponse = watchlistService.createWatchlist(request, currentUser);

        // then
        ArgumentCaptor<Watchlist> watchlistCaptor = ArgumentCaptor.forClass(Watchlist.class);
        verify(watchlistRepository).save(watchlistCaptor.capture());

        Watchlist savedWatchlist = watchlistCaptor.getValue();

        assertThat(savedWatchlist.getUser()).isSameAs(user);
        assertThat(savedWatchlist.getName()).isEqualTo("Dividend stocks");
        assertThat(savedWatchlist.getCreatedAt()).isNotNull();

        assertThat(actualResponse).isSameAs(expectedResponse);

        verify(currentUser).getUser();
        verify(watchlistRepository).existsByUserAndName(user, "Dividend stocks");
        verify(watchlistMapper).toResponse(savedWatchlist);
    }

    @Test
    void createWatchlist_shouldThrowDuplicateException_whenWatchlistNameAlreadyExistsForUser() {
        // given
        User user = mock(User.class);
        CustomUserDetails currentUser = mock(CustomUserDetails.class);

        CreateWatchlistRequest request = new CreateWatchlistRequest("Dividend stocks");

        when(currentUser.getUser()).thenReturn(user);

        when(watchlistRepository.existsByUserAndName(user, "Dividend stocks"))
                .thenReturn(true);

        // when + then
        assertThatThrownBy(() -> watchlistService.createWatchlist(request, currentUser))
                .isInstanceOf(DuplicateException.class);

        verify(currentUser).getUser();
        verify(watchlistRepository).existsByUserAndName(user, "Dividend stocks");
        verify(watchlistRepository, never()).save(any());
        verifyNoInteractions(watchlistMapper);
    }

    @Test
    void createWatchlist_shouldThrowAccessDeniedException_whenCurrentUserIsNull() {
        // given
        CreateWatchlistRequest request = new CreateWatchlistRequest("Dividend stocks");

        // when + then
        assertThatThrownBy(() -> watchlistService.createWatchlist(request, null))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessage("User is not authenticated");

        verifyNoInteractions(watchlistRepository);
        verifyNoInteractions(watchlistMapper);
    }

    @Test
    void deleteWatchlist_shouldDeleteWatchlist_whenWatchlistExists() {
        // given
        Long watchlistId = 1L;

        CustomUserDetails currentUser = mock(CustomUserDetails.class);
        Watchlist watchlist = mock(Watchlist.class);

        when(watchlistRepository.findById(watchlistId))
                .thenReturn(Optional.of(watchlist));

        // when
        watchlistService.deleteWatchlist(watchlistId, currentUser);

        // then
        verify(watchlistRepository).findById(watchlistId);
        verify(watchlistRepository).delete(watchlist);

        verifyNoInteractions(currentUser);
        verifyNoInteractions(watchlistMapper);
        verify(watchlistRepository, never()).findByIdAndUser(anyLong(), any());
    }

    @Test
    void deleteWatchlist_shouldThrowEntityNotFoundException_whenWatchlistDoesNotExist() {
        // given
        Long watchlistId = 999L;

        CustomUserDetails currentUser = mock(CustomUserDetails.class);

        when(watchlistRepository.findById(watchlistId))
                .thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> watchlistService.deleteWatchlist(watchlistId, currentUser))
                .isInstanceOf(EntityNotFoundException.class);

        verify(watchlistRepository).findById(watchlistId);
        verify(watchlistRepository, never()).delete(any());

        verifyNoInteractions(currentUser);
        verifyNoInteractions(watchlistMapper);
        verify(watchlistRepository, never()).findByIdAndUser(anyLong(), any());
    }

    @Test
    void deleteWatchlist_shouldDeleteWatchlist_evenWhenCurrentUserIsNull_becauseCurrentImplementationDoesNotUseCurrentUser() {
        // given
        Long watchlistId = 1L;

        Watchlist watchlist = mock(Watchlist.class);

        when(watchlistRepository.findById(watchlistId))
                .thenReturn(Optional.of(watchlist));

        // when
        watchlistService.deleteWatchlist(watchlistId, null);

        // then
        verify(watchlistRepository).findById(watchlistId);
        verify(watchlistRepository).delete(watchlist);

        verifyNoInteractions(watchlistMapper);
        verify(watchlistRepository, never()).findByIdAndUser(anyLong(), any());
    }

    @Test
    void getCurrentUserWatchlists_shouldReturnCurrentUserWatchlists() {
        // given
        User user = mock(User.class);
        CustomUserDetails currentUser = mock(CustomUserDetails.class);

        Watchlist firstWatchlist = mock(Watchlist.class);
        Watchlist secondWatchlist = mock(Watchlist.class);

        WatchlistResponse firstResponse = mock(WatchlistResponse.class);
        WatchlistResponse secondResponse = mock(WatchlistResponse.class);

        when(currentUser.getUser()).thenReturn(user);

        when(watchlistRepository.findAllByUserOrderByCreatedAtDesc(user))
                .thenReturn(List.of(firstWatchlist, secondWatchlist));

        when(watchlistMapper.toResponse(firstWatchlist)).thenReturn(firstResponse);
        when(watchlistMapper.toResponse(secondWatchlist)).thenReturn(secondResponse);

        // when
        List<WatchlistResponse> response = watchlistService.getCurrentUserWatchlists(currentUser);

        // then
        assertThat(response).containsExactly(firstResponse, secondResponse);

        verify(currentUser).getUser();
        verify(watchlistRepository).findAllByUserOrderByCreatedAtDesc(user);
        verify(watchlistMapper).toResponse(firstWatchlist);
        verify(watchlistMapper).toResponse(secondWatchlist);
    }

    @Test
    void getCurrentUserWatchlists_shouldThrowNullPointerException_whenCurrentUserIsNull() {
        // when + then
        assertThatThrownBy(() -> watchlistService.getCurrentUserWatchlists(null))
                .isInstanceOf(NullPointerException.class);

        verifyNoInteractions(watchlistRepository);
        verifyNoInteractions(watchlistMapper);
    }

    @Test
    void addStockToWatchlist_shouldSaveWatchlistStock_whenWatchlistAndStockExist() {
        // given
        Long watchlistId = 1L;
        Long stockId = 10L;

        Watchlist watchlist = mock(Watchlist.class);
        Stock stock = mock(Stock.class);

        when(watchlistRepository.findById(watchlistId)).thenReturn(Optional.of(watchlist));

        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));

        when(watchlistStockRepository.existsByWatchlistIdAndStockId(watchlistId, stockId)).thenReturn(false);

        // when
        watchlistService.addStockToWatchlist(watchlistId, stockId);

        // then
        ArgumentCaptor<WatchlistStock> captor = ArgumentCaptor.forClass(WatchlistStock.class);

        verify(watchlistStockRepository).save(captor.capture());

        WatchlistStock savedWatchlistStock = captor.getValue();

        assertThat(savedWatchlistStock.getWatchlist()).isSameAs(watchlist);

        assertThat(savedWatchlistStock.getStock()).isSameAs(stock);

        assertThat(savedWatchlistStock.getAddedAt()).isNotNull();

        verify(watchlistRepository).findById(watchlistId);
        verify(stockRepository).findById(stockId);

        verify(watchlistStockRepository).existsByWatchlistIdAndStockId(watchlistId, stockId);

        verifyNoInteractions(watchlistMapper);
    }

    @Test
    void addStockToWatchlist_shouldThrowEntityNotFoundException_whenWatchlistDoesNotExist() {
        // given
        Long watchlistId = 999L;
        Long stockId = 10L;

        when(watchlistRepository.findById(watchlistId)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> watchlistService.addStockToWatchlist(watchlistId, stockId)).isInstanceOf(EntityNotFoundException.class);

        verify(watchlistRepository).findById(watchlistId);

        verifyNoInteractions(stockRepository, watchlistStockRepository, watchlistMapper);
    }

    @Test
    void addStockToWatchlist_shouldThrowEntityNotFoundException_whenStockDoesNotExist() {
        // given
        Long watchlistId = 1L;
        Long stockId = 999L;

        Watchlist watchlist = mock(Watchlist.class);

        when(watchlistRepository.findById(watchlistId)).thenReturn(Optional.of(watchlist));

        when(stockRepository.findById(stockId)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> watchlistService.addStockToWatchlist(watchlistId, stockId)).isInstanceOf(EntityNotFoundException.class);

        verify(watchlistRepository).findById(watchlistId);
        verify(stockRepository).findById(stockId);

        verifyNoInteractions(watchlistStockRepository, watchlistMapper);
    }

    @Test
    void addStockToWatchlist_shouldThrowDuplicateException_whenStockIsAlreadyPresent() {
        // given
        Long watchlistId = 1L;
        Long stockId = 10L;

        Watchlist watchlist = mock(Watchlist.class);
        Stock stock = mock(Stock.class);

        when(watchlistRepository.findById(watchlistId)).thenReturn(Optional.of(watchlist));

        when(stockRepository.findById(stockId)).thenReturn(Optional.of(stock));

        when(watchlistStockRepository.existsByWatchlistIdAndStockId(watchlistId, stockId)).thenReturn(true);

        // when + then
        assertThatThrownBy(() -> watchlistService.addStockToWatchlist(watchlistId, stockId))
                .isInstanceOf(DuplicateException.class)
                .hasMessageContaining(stockId.toString())
                .hasMessageContaining(watchlistId.toString());

        verify(watchlistRepository).findById(watchlistId);
        verify(stockRepository).findById(stockId);

        verify(watchlistStockRepository).existsByWatchlistIdAndStockId(watchlistId, stockId);

        verify(watchlistStockRepository, never()).save(any());
        verifyNoInteractions(watchlistMapper);
    }

    @Test
    void removeStockFromWatchlist_shouldDeleteWatchlistStock_whenRelationExists() {
        // given
        Long watchlistId = 1L;
        Long stockId = 10L;

        WatchlistStock watchlistStock = mock(WatchlistStock.class);

        when(watchlistStockRepository.findByWatchlistIdAndStockId(watchlistId, stockId)).thenReturn(Optional.of(watchlistStock));

        // when
        watchlistService.removeStockFromWatchlist(watchlistId, stockId);

        // then
        verify(watchlistStockRepository).findByWatchlistIdAndStockId(watchlistId, stockId);

        verify(watchlistStockRepository).delete(watchlistStock);

        verifyNoInteractions(watchlistRepository, stockRepository, watchlistMapper);
    }

    @Test
    void removeStockFromWatchlist_shouldThrowEntityNotFoundException_whenRelationDoesNotExist() {
        // given
        Long watchlistId = 1L;
        Long stockId = 999L;

        when(watchlistStockRepository.findByWatchlistIdAndStockId(watchlistId, stockId)).thenReturn(Optional.empty());

        // when + then
        assertThatThrownBy(() -> watchlistService.removeStockFromWatchlist(watchlistId, stockId)).isInstanceOf(EntityNotFoundException.class);

        verify(watchlistStockRepository).findByWatchlistIdAndStockId(watchlistId, stockId);

        verify(watchlistStockRepository, never()).delete(any());

        verifyNoInteractions(watchlistRepository, stockRepository, watchlistMapper);
    }
}