import { get, post } from "./api.js";
import { isAdmin, requireAuthentication } from "./auth.js";
import {
    escapeHtml,
    hideAlert,
    renderHeader,
    setButtonLoading,
    showAlert
} from "./ui.js";

if (!requireAuthentication()) {
    throw new Error("Authentication required.");
}

const grid = document.getElementById("watchlists-grid");
const loading = document.getElementById("loading");
const emptyState = document.getElementById("empty-state");
const alert = document.getElementById("alert");
const formAlert = document.getElementById("form-alert");
const form = document.getElementById("create-watchlist-form");
const createButton = document.getElementById("create-button");
const nameInput = document.getElementById("watchlist-name");
const pageDescription = document.getElementById("page-description");

const user = await renderHeader("watchlists");

if (isAdmin(user)) {
    pageDescription.textContent = "View and manage watchlists available to your administrator account.";
}

await loadWatchlists();

form.addEventListener("submit", async event => {
    event.preventDefault();
    hideAlert(formAlert);

    const name = nameInput.value.trim();

    if (!name) {
        showAlert(formAlert, "Watchlist name is required.");
        return;
    }

    setButtonLoading(createButton, true, "Creating…");

    try {
        await post("/api/watchlists/add", { name });
        nameInput.value = "";
        showAlert(formAlert, "Watchlist was created.", "success");
        await loadWatchlists();
    } catch (error) {
        showAlert(formAlert, error.message);
    } finally {
        setButtonLoading(createButton, false);
    }
});

async function loadWatchlists() {
    loading.classList.remove("hidden");
    grid.classList.add("hidden");
    emptyState.classList.add("hidden");
    hideAlert(alert);

    try {
        const watchlists = await get("/api/watchlists");
        renderWatchlists(watchlists);
    } catch (error) {
        showAlert(alert, error.message);
    } finally {
        loading.classList.add("hidden");
    }
}

function renderWatchlists(watchlists) {
    if (!watchlists || watchlists.length === 0) {
        emptyState.classList.remove("hidden");
        return;
    }

    grid.classList.remove("hidden");
    grid.innerHTML = watchlists.map(watchlist => `
        <article class="watchlist-card">
            <div class="watchlist-card-header">
                <div>
                    <p class="eyebrow">Watchlist</p>
                    <h2>${escapeHtml(watchlist.name)}</h2>
                </div>
            </div>
            <a class="button button-primary" href="/watchlist-detail.html?id=${encodeURIComponent(watchlist.id)}">
                Open watchlist
            </a>
        </article>
    `).join("");
}
