import { get, post } from "./api.js";
import {
    escapeHtml,
    formatCompactNumber,
    formatCurrency,
    formatNumber,
    formatPercent,
    getRequiredQueryParameter,
    hideAlert,
    renderHeader,
    setButtonLoading,
    showAlert
} from "./ui.js";

const user = await renderHeader("stocks");

const alert = document.getElementById("alert");
const loading = document.getElementById("loading");
const content = document.getElementById("stock-content");
const watchlistForm = document.getElementById("watchlist-form");
const watchlistSelect = document.getElementById("watchlist-select");
const watchlistButton = document.getElementById("watchlist-button");
const watchlistAlert = document.getElementById("watchlist-alert");

let stockId;

try {
    stockId = getRequiredQueryParameter("id");

    const detail = await get(
        `/api/stocks/detail/${encodeURIComponent(stockId)}`
    );

    renderStock(detail);

    if (user) {
        const watchlists = await get("/api/watchlists");
        renderWatchlists(watchlists);
    } else {
        renderAuthenticationRequired();
    }

    content.classList.remove("hidden");
} catch (error) {
    showAlert(alert, error.message);
} finally {
    loading.classList.add("hidden");
}

if (user) {
    watchlistForm.addEventListener("submit", async event => {
        event.preventDefault();
        hideAlert(watchlistAlert);

        const watchlistId = watchlistSelect.value;

        if (!watchlistId) {
            showAlert(
                watchlistAlert,
                "Create or select a watchlist first."
            );

            return;
        }

        setButtonLoading(watchlistButton, true, "Adding…");

        try {
            await post(
                `/api/watchlists/${encodeURIComponent(watchlistId)}` +
                `/stocks/${encodeURIComponent(stockId)}`
            );

            showAlert(
                watchlistAlert,
                "Stock was added to the watchlist.",
                "success"
            );
        } catch (error) {
            showAlert(watchlistAlert, error.message);
        } finally {
            setButtonLoading(watchlistButton, false);
        }
    });
}

function renderAuthenticationRequired() {
    watchlistForm.classList.add("hidden");

    const authenticationMessage = document.createElement("div");
    authenticationMessage.className = "empty-state";
    authenticationMessage.innerHTML = `
<h3>Sign in to use watchlists</h3>

<p>
    You need to be signed in to add this stock to a watchlist.
</p>

<a class="button button-primary"
   href="/login.html?redirect=${encodeURIComponent(
               window.location.pathname + window.location.search
           )}">
    Sign in
</a>
    `;

    watchlistForm.insertAdjacentElement(
        "beforebegin",
        authenticationMessage
    );
}

function renderStock(detail) {
    const stock = detail.stockData;

    const marketDays = [...(detail.marketDays || [])]
        .sort((a, b) => a.date.localeCompare(b.date));

    document.title = `${stock.ticker} | Stock Analyzer`;

    document.getElementById("stock-ticker").textContent =
        stock.ticker;

    document.getElementById("company-name").textContent =
        stock.companyName;

    document.getElementById("stock-meta").textContent =
        [
            stock.exchange,
            stock.sector,
            stock.industry
        ]
            .filter(Boolean)
            .join(" · ");

    const description =
        document.getElementById("company-description");

    description.textContent =
        stock.description || "No company description is available.";

    const website =
        document.getElementById("company-website");

    if (stock.websiteUrl) {
        website.href = stock.websiteUrl;
        website.classList.remove("hidden");
    }

    const logoWrapper =
        document.getElementById("logo-wrapper");

    const logo =
        document.getElementById("company-logo");

    if (stock.logoUrl) {
        logo.src = stock.logoUrl;
        logo.alt = `${stock.companyName} logo`;

        logoWrapper.classList.remove("hidden");

        logo.addEventListener("error", () => {
            logoWrapper.classList.add("hidden");
        });
    }

    const metricDefinitions = [
        [
            "Market capitalization",
            formatCompactNumber(stock.marketCapitalization)
        ],
        [
            "52-week low",
            formatCurrency(
                stock.fiftyTwoWeekLow,
                stock.currency || "USD"
            )
        ],
        [
            "52-week high",
            formatCurrency(
                stock.fiftyTwoWeekHigh,
                stock.currency || "USD"
            )
        ],
        [
            "P/E ratio",
            formatNumber(stock.peRatio)
        ],
        [
            "EPS",
            formatCurrency(
                stock.earningsPerShare,
                stock.currency || "USD"
            )
        ],
        [
            "Dividend yield",
            formatPercent(stock.dividendYield)
        ],
        [
            "Beta",
            formatNumber(stock.beta)
        ],
        [
            "Debt service coverage",
            formatNumber(stock.debtServiceCoverageRatio)
        ],
        [
            "Free cash flow / share",
            formatCurrency(
                stock.freeCashFlowPerShare,
                stock.currency || "USD"
            )
        ],
        [
            "Operating margin",
            formatPercent(stock.operatingMargin)
        ]
    ];

    document.getElementById("metric-grid").innerHTML =
        metricDefinitions.map(([label, value]) => `
<div class="metric-card">
    <span>${escapeHtml(label)}</span>
<strong>${escapeHtml(value)}</strong>
</div>
`).join("");

    document.getElementById("history-caption").textContent =
        marketDays.length > 0
            ? `${marketDays.length} stored market days`
            : "No stored market data";

    renderChart(
        marketDays,
        stock.currency || "USD"
    );
}

function renderWatchlists(watchlists) {
    if (!watchlists || watchlists.length === 0) {
        watchlistSelect.innerHTML = `
<option value="">
    No watchlists available
</option>
    `;

        watchlistButton.disabled = true;
        return;
    }

    watchlistSelect.innerHTML = watchlists
        .map(watchlist => `
<option value="${escapeHtml(watchlist.id)}">
    ${escapeHtml(watchlist.name)}
</option>
`)
        .join("");
}

function renderChart(marketDays, currency) {
    const canvas = document.getElementById("price-chart");

    if (!window.Chart || marketDays.length === 0) {
        canvas.parentElement.innerHTML = `
<div class="empty-state">
    <p>
    ${
    window.Chart
        ? "No historical prices are available."
        : "Chart library could not be loaded."
}
</p>
</div>
`;

        return;
    }

    new Chart(canvas, {
        type: "line",
        data: {
            labels: marketDays.map(day => day.date),
            datasets: [
                {
                    label: "Close price",
                    data: marketDays.map(
                        day => Number(day.closePrice)
                    ),
                    borderWidth: 2,
                    pointRadius: 0,
                    pointHoverRadius: 4,
                    tension: 0.15
                }
            ]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            interaction: {
                intersect: false,
                mode: "index"
            },
            plugins: {
                legend: {
                    display: false
                },
                tooltip: {
                    callbacks: {
                        label: context =>
                            formatCurrency(
                                context.parsed.y,
                                currency
                            )
                    }
                }
            },
            scales: {
                x: {
                    ticks: {
                        maxTicksLimit: 10
                    },
                    grid: {
                        display: false
                    }
                },
                y: {
                    ticks: {
                        callback: value =>
                            formatCurrency(value, currency)
                    }
                }
            }
        }
    });
}
