import { get, post } from "./api.js";
import { isAdmin } from "./auth.js";
import {
    escapeHtml,
    formatCompactNumber,
    formatCurrency,
    hideAlert,
    renderHeader,
    setButtonLoading,
    showAlert
} from "./ui.js";

const tableWrapper = document.getElementById("stocks-table-wrapper");
const tableBody = document.getElementById("stocks-table-body");
const loading = document.getElementById("loading");
const emptyState = document.getElementById("empty-state");
const alert = document.getElementById("alert");
const searchInput = document.getElementById("stock-search");
const stockCount = document.getElementById("stock-count");
const refreshButton = document.getElementById("refresh-button");
const adminPanel = document.getElementById("admin-panel");
const adminAlert = document.getElementById("admin-alert");
const addStockForm = document.getElementById("add-stock-form");
const addStockButton = document.getElementById("add-stock-button");

let stocks = [];

const user = await renderHeader("stocks");

if (isAdmin(user)) {
    adminPanel.classList.remove("hidden");
}

await loadStocks();

refreshButton.addEventListener("click", loadStocks);
searchInput.addEventListener("input", renderStocks);

addStockForm.addEventListener("submit", async event => {
    event.preventDefault();
    hideAlert(adminAlert);

    if (!isAdmin(user)) {
        showAlert(adminAlert, "Administrator access is required.");
        return;
    }

    const tickerInput = document.getElementById("ticker");
    const ticker = tickerInput.value.trim().toUpperCase();

    if (!ticker) {
        showAlert(adminAlert, "Ticker is required.");
        return;
    }

    setButtonLoading(addStockButton, true, "Adding…");

    try {
        await post(`/api/stocks/add?ticker=${encodeURIComponent(ticker)}`);

        tickerInput.value = "";

        showAlert(
            adminAlert,
            `${ticker} was added successfully.`,
            "success"
        );

        await loadStocks();
    } catch (error) {
        showAlert(adminAlert, error.message);
    } finally {
        setButtonLoading(addStockButton, false);
    }
});

async function loadStocks() {
    loading.classList.remove("hidden");
    tableWrapper.classList.add("hidden");
    emptyState.classList.add("hidden");
    hideAlert(alert);
    refreshButton.disabled = true;

    try {
        stocks = await get("/api/stocks");
        renderStocks();
    } catch (error) {
        showAlert(alert, error.message);
    } finally {
        loading.classList.add("hidden");
        refreshButton.disabled = false;
    }
}

function renderStocks() {
    const query = searchInput.value.trim().toLowerCase();

    const filteredStocks = stocks.filter(stock => {
        const values = [
            stock.ticker,
            stock.companyName,
            stock.sector,
            stock.industry
        ];

        return values.some(value =>
            String(value || "").toLowerCase().includes(query)
        );
    });

    stockCount.textContent =
        `${filteredStocks.length} ${
    filteredStocks.length === 1 ? "stock" : "stocks"
}`;

    if (filteredStocks.length === 0) {
        tableWrapper.classList.add("hidden");
        emptyState.classList.remove("hidden");
        return;
    }

    emptyState.classList.add("hidden");
    tableWrapper.classList.remove("hidden");

    tableBody.innerHTML = filteredStocks.map(stock => {
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
    <span>
    ${escapeHtml((stock.ticker || "?").charAt(0))}
</span>
</div>
`
            : `
<div class="stock-logo-wrapper logo-fallback">
    <span>
    ${escapeHtml((stock.ticker || "?").charAt(0))}
</span>
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
<td>${escapeHtml(stock.industry || "—")}</td>
<td>${formatCurrency(stock.lastClosePrice)}</td>
<td>${formatCompactNumber(stock.marketCapitalization)}</td>

<td class="actions-column">
    <a class="button button-primary button-small"
       href="/stock-detail.html?id=${encodeURIComponent(stock.id)}">
        Detail
    </a>
</td>
</tr>
`;
    }).join("");
}

