import { getCurrentUser, logout } from "./auth.js";

export function showAlert(element, message, type = "error") {
    if (!element) {
        return;
    }

    element.textContent = message;
    element.className = `alert alert-${type}`;
}

export function hideAlert(element) {
    if (!element) {
        return;
    }

    element.textContent = "";
    element.className = "alert hidden";
}

export function setButtonLoading(button, loading, loadingText = "Loading…") {
    if (!button) {
        return;
    }

    if (loading) {
        button.dataset.originalText = button.textContent;
        button.textContent = loadingText;
        button.disabled = true;
    } else {
        button.textContent = button.dataset.originalText || button.textContent;
        button.disabled = false;
        delete button.dataset.originalText;
    }
}

export function formatCurrency(value, currency = "USD") {
    if (value === null || value === undefined || value === "") {
        return "—";
    }

    return new Intl.NumberFormat("en-US", {
        style: "currency",
        currency,
        maximumFractionDigits: 2
    }).format(Number(value));
}

export function formatNumber(value, maximumFractionDigits = 2) {
    if (value === null || value === undefined || value === "") {
        return "—";
    }

    return new Intl.NumberFormat("en-US", {
        maximumFractionDigits
    }).format(Number(value));
}

export function formatCompactNumber(value) {
    if (value === null || value === undefined || value === "") {
        return "—";
    }

    return new Intl.NumberFormat("en-US", {
        notation: "compact",
        maximumFractionDigits: 2
    }).format(Number(value));
}

export function formatPercent(value, { alreadyPercentage = false } = {}) {
    if (value === null || value === undefined || value === "") {
        return "—";
    }

    const numericValue = Number(value);

    return `${formatNumber(
    alreadyPercentage ? numericValue : numericValue * 100,
    2
)}%`;
}

export function formatDate(value) {
    if (!value) {
        return "—";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return new Intl.DateTimeFormat("en-GB", {
        day: "2-digit",
        month: "short",
        year: "numeric"
    }).format(date);
}

export function getRequiredQueryParameter(name) {
    const value = new URLSearchParams(window.location.search).get(name);

    if (!value) {
        throw new Error(`Missing required query parameter: ${name}`);
    }

    return value;
}

export async function renderHeader(activePage) {
    const header = document.getElementById("app-header");

    if (!header) {
        return null;
    }

    let user = null;

    try {
        user = await getCurrentUser();
    } catch {
        user = null;
    }

    const activeClass = page => activePage === page ? "active" : "";

    header.className = "app-header";

    if (!user) {
        header.innerHTML = `
<div class="header-inner">
    <a class="brand" href="/stocks.html">
    Stock Analyzer
</a>

<nav class="main-nav" aria-label="Main navigation">
    <a class="${activeClass("stocks")}" href="/stocks.html">
        Stocks
    </a>

    <a href="/login.html?redirect=${encodeURIComponent("/watchlists.html")}">
        Watchlists
    </a>
</nav>

<div class="user-menu">
    <a class="button button-secondary button-small"
       href="/login.html">
        Login
    </a>

    <a class="button button-primary button-small"
       href="/register.html">
        Register
    </a>
</div>
</div>
`;

        return null;
    }

    header.innerHTML = `
<div class="header-inner">
    <a class="brand" href="/stocks.html">
    Stock Analyzer
</a>

<nav class="main-nav" aria-label="Main navigation">
    <a class="${activeClass("stocks")}" href="/stocks.html">
        Stocks
    </a>

    <a class="${activeClass("watchlists")}" href="/watchlists.html">
        Watchlists
    </a>
</nav>

<div class="user-menu">
    <div class="user-summary">
        <strong>${escapeHtml(user.username)}</strong>
        <span>${escapeHtml(user.role)}</span>
    </div>

    <button id="logout-button"
            class="button button-secondary button-small"
            type="button">
        Logout
    </button>
</div>
</div>
`;

    document
        .getElementById("logout-button")
        .addEventListener("click", logout);

    return user;
}

export function escapeHtml(value) {
    return String(value ?? "")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}
