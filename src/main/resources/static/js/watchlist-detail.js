import { get, remove } from "./api.js";
import { requireAuthentication } from "./auth.js";
import {
    escapeHtml,
    formatCompactNumber,
    formatCurrency,
    formatDate,
    getRequiredQueryParameter,
    renderHeader,
    setButtonLoading,
    showAlert
} from "./ui.js";

if (!requireAuthentication()) {
    throw new Error("Authentication required.");
}

await renderHeader("watchlists");

const alert = document.getElementById("alert");
const loading = document.getElementById("loading");
const content = document.getElementById("watchlist-content");
const emptyState = document.getElementById("empty-state");
const tableWrapper = document.getElementById("stocks-table-wrapper");
const tableBody = document.getElementById("stocks-table-body");
const deleteButton = document.getElementById("delete-watchlist-button");

let watchlistId;
let watchlist;

try {
    watchlistId = getRequiredQueryParameter("id");
    watchlist = await get(`/api/watchlists/detail/${encodeURIComponent(watchlistId)}`);
    renderWatchlist();
    content.classList.remove("hidden");
} catch (error) {
    showAlert(alert, error.message);
} finally {
    loading.classList.add("hidden");
}

deleteButton.addEventListener("click", async () => {
    const confirmed = window.confirm(`Delete watchlist "${watchlist?.name || ""}"?`);

    if (!confirmed) {
        return;
    }

    setButtonLoading(deleteButton, true, "Deleting…");

    try {
        await remove(`/api/watchlists/delete/${encodeURIComponent(watchlistId)}`);
    window.location.replace("/watchlists.html");
} catch (error) {
    showAlert(alert, error.message);
    setButtonLoading(deleteButton, false);
}
});

tableBody.addEventListener("click", async event => {
    const button = event.target.closest("[data-remove-stock-id]");

    if (!button) {
        return;
    }

    const stockId = button.dataset.removeStockId;
    setButtonLoading(button, true, "Removing…");

    try {
        await remove(
            `/api/watchlists/${encodeURIComponent(watchlistId)}/stocks/${encodeURIComponent(stockId)}`
        );

        watchlist.stocks = watchlist.stocks.filter(
            stock => String(stock.id) !== String(stockId)
        );

        renderStocks();
    } catch (error) {
        showAlert(alert, error.message);
        setButtonLoading(button, false);
    }
});

function renderWatchlist() {
    document.title = `${watchlist.name} | Stock Analyzer`;

    document.getElementById("watchlist-name").textContent = watchlist.name;
    document.getElementById("watchlist-created-at").textContent =
        `Created ${formatDate(watchlist.createdAt)}`;

    renderStocks();
}

function renderStocks() {
    const stocks = watchlist.stocks || [];

    document.getElementById("stock-count").textContent =
        `${stocks.length} ${stocks.length === 1 ? "stock" : "stocks"}`;

    if (stocks.length === 0) {
        tableWrapper.classList.add("hidden");
        emptyState.classList.remove("hidden");
        return;
    }

    emptyState.classList.add("hidden");
    tableWrapper.classList.remove("hidden");

    tableBody.innerHTML = stocks.map(stock => {
        const companyLogo = stock.logoUrl
            ? `
                <div class="stock-logo-wrapper">
                    <img
                        class="stock-logo"
                        src="${escapeHtml(stock.logoUrl)}"
                        alt="${escapeHtml(stock.companyName)} logo"
                        loading="lazy"
                        referrerpolicy="no-referrer"
                        onerror="this.parentElement.classList.add('logo-fallback')"
                    >
                    <span>${escapeHtml((stock.ticker || "?").charAt(0))}</span>
                </div>
            `
            : `
                <div class="stock-logo-wrapper logo-fallback">
                    <span>${escapeHtml((stock.ticker || "?").charAt(0))}</span>
                </div>
            `;

        return `
            <tr>
                <td>
                    <a class="stock-company-cell"
                       href="/stock-detail.html?id=${encodeURIComponent(stock.id)}">
                        ${companyLogo}
                        <strong>${escapeHtml(stock.companyName)}</strong>
                    </a>
                </td>

                <td>
                    <span class="ticker-badge">
                        ${escapeHtml(stock.ticker)}
                    </span>
                </td>

                <td>${escapeHtml(stock.sector || "—")}</td>
                <td>${formatCurrency(stock.lastClosePrice)}</td>
                <td>${formatCompactNumber(stock.marketCapitalization)}</td>

                <td class="actions-column">
                    <a class="button button-secondary button-small"
                       href="/stock-detail.html?id=${encodeURIComponent(stock.id)}">
                        Detail
                    </a>

                    <button class="button button-ghost-danger button-small"
                            type="button"
                            data-remove-stock-id="${escapeHtml(stock.id)}">
                        Remove
                    </button>
                </td>
            </tr>
        `;
    }).join("");
}
