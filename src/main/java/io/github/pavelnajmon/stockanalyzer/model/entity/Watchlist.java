package io.github.pavelnajmon.stockanalyzer.model.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "watchlist", schema = "stock_analyzer", uniqueConstraints = {
@UniqueConstraint(name = "unique_user_id_watchlist_name", columnNames = {"user_id", "name"}
)})
public class Watchlist {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "watchlist_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @OneToMany(mappedBy = "watchlist", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WatchlistStock> watchlistStocks = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public void addWatchlistStock(WatchlistStock watchlistStock) {
        watchlistStocks.add(watchlistStock);
        watchlistStock.setWatchlist(this);
    }

    public void removeWatchlistStock(WatchlistStock watchlistStock) {
        watchlistStocks.remove(watchlistStock);
        watchlistStock.setWatchlist(null);
    }

    public List<WatchlistStock> getWatchlistStocks() {
        return watchlistStocks;
    }

    public void setWatchlistStocks(List<WatchlistStock> watchlistStocks) {
        this.watchlistStocks = watchlistStocks;
    }
}
