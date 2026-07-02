---Market Day---
CREATE INDEX idx_market_day_stock_id ON stock_analyzer.market_day (stock_id);
CREATE INDEX idx_market_day_date ON stock_analyzer.market_day (date);
CREATE INDEX idx_market_day_stock_date_desc ON stock_analyzer.market_day (stock_id, date DESC);
---Watchlist---
CREATE INDEX idx_watchlist_user_id ON stock_analyzer.watchlist (user_id);
CREATE INDEX idx_watchlist_stock_watchlist_id ON stock_analyzer.watchlist_stock (watchlist_id);
CREATE INDEX idx_watchlist_stock_stock_id ON stock_analyzer.watchlist_stock (stock_id);