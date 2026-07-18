import {
    isAuthenticated,
    post,
    saveAuthentication
} from "./api.js";
import {
    hideAlert,
    setButtonLoading,
    showAlert
} from "./ui.js";

if (isAuthenticated()) {
    window.location.replace("/stocks.html");
}

const form = document.getElementById("login-form");
const alert = document.getElementById("alert");
const submitButton = document.getElementById("submit-button");

form.addEventListener("submit", async event => {
    event.preventDefault();
    hideAlert(alert);
    setButtonLoading(submitButton, true, "Signing in…");

    const usernameOrEmail = document.getElementById("username-or-email").value.trim();
    const password = document.getElementById("password").value;

    try {
        const authentication = await post("/api/auth/login", {
            usernameOrEmail,
            password
        });

        saveAuthentication(authentication);

        const redirect = new URLSearchParams(window.location.search).get("redirect");
        window.location.replace(redirect || "/stocks.html");
    } catch (error) {
        showAlert(alert, error.message);
    } finally {
        setButtonLoading(submitButton, false);
    }
});
