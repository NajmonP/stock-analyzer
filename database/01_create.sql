CREATE SCHEMA IF NOT EXISTS stock_analyzer;

SET
search_path TO stock_analyzer, public;

CREATE TYPE stock_analyzer.user_role AS ENUM (
    'USER',
    'ADMIN'
);

CREATE TABLE IF NOT EXISTS stock_analyzer.users (
    user_id       BIGSERIAL PRIMARY KEY,
    username      VARCHAR(100)             NOT NULL UNIQUE,
    email         VARCHAR(255)             NOT NULL UNIQUE,
    password_hash VARCHAR(255)             NOT NULL,
    role          stock_analyzer.user_role NOT NULL DEFAULT 'USER',
    created_at    TIMESTAMPTZ              NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stock_analyzer.stock (
    stock_id                    BIGSERIAL PRIMARY KEY,
    ticker                      VARCHAR(20)  NOT NULL UNIQUE,
    company_name                VARCHAR(255) NOT NULL,
    description                 TEXT,
    sector                      VARCHAR(100),
    industry                    VARCHAR(150),
    exchange                    VARCHAR(100),
    currency                    VARCHAR(10),
    website_url                 VARCHAR(500),
    logo_url                    VARCHAR(500),
    market_capitalization       NUMERIC(24, 2),
    fifty_two_week_low          NUMERIC(18, 4),
    fifty_two_week_high         NUMERIC(18, 4),
    pe_ratio                    NUMERIC(18, 4),
    earnings_per_share          NUMERIC(18, 4),
    dividend_yield              NUMERIC(10, 6),
    beta                        NUMERIC(10, 4),
    debt_service_coverage_ratio NUMERIC(18, 4),
    free_cash_flow_per_share    NUMERIC(18, 4),
    operating_margin            NUMERIC(10, 6),
    last_updated_at             TIMESTAMPTZ
);

CREATE TABLE IF NOT EXISTS stock_analyzer.market_day (
    market_day_id        BIGSERIAL PRIMARY KEY,
    stock_id             BIGINT      NOT NULL REFERENCES stock_analyzer.stock (stock_id) ON DELETE CASCADE,
    date                 DATE        NOT NULL,
    open_price           NUMERIC(18, 4) NOT NULL,
    close_price          NUMERIC(18, 4) NOT NULL,
    high_price           NUMERIC(18, 4) NOT NULL,
    low_price            NUMERIC(18, 4) NOT NULL,
    volume               BIGINT NOT NULL,
    created_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (stock_id, date)
);

CREATE TABLE IF NOT EXISTS stock_analyzer.watchlist (
    watchlist_id BIGSERIAL PRIMARY KEY,
    user_id      BIGINT       NOT NULL REFERENCES stock_analyzer.users (user_id) ON DELETE CASCADE,
    name         VARCHAR(150) NOT NULL,
    created_at   TIMESTAMPTZ  NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, name)
);

CREATE TABLE IF NOT EXISTS stock_analyzer.watchlist_stock (
    watchlist_stock_id BIGSERIAL PRIMARY KEY,
    watchlist_id       BIGINT      NOT NULL REFERENCES stock_analyzer.watchlist (watchlist_id) ON DELETE CASCADE,
    stock_id           BIGINT      NOT NULL REFERENCES stock_analyzer.stock (stock_id) ON DELETE CASCADE,
    added_at           TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (watchlist_id, stock_id)
);