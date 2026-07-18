import { isAuthenticated, post } from "./api.js";
import {
    hideAlert,
    setButtonLoading,
    showAlert
} from "./ui.js";

if (isAuthenticated()) {
    window.location.replace("/stocks.html");
}

const form = document.getElementById("register-form");
const alert = document.getElementById("alert");
const submitButton = document.getElementById("submit-button");

form.addEventListener("submit", async event => {
    event.preventDefault();
    hideAlert(alert);

    const username = document.getElementById("username").value.trim();
    const email = document.getElementById("email").value.trim();
    const password = document.getElementById("password").value;
    const passwordConfirmation = document.getElementById("password-confirmation").value;

    if (password !== passwordConfirmation) {
        showAlert(alert, "Passwords do not match.");
        return;
    }

    setButtonLoading(submitButton, true, "Creating account…");

    try {
        await post("/api/auth/register", {
            username,
            email,
            password
        });

        window.location.replace("/login.html?registered=true");
    } catch (error) {
        showAlert(alert, error.message);
    } finally {
        setButtonLoading(submitButton, false);
    }
});

if (new URLSearchParams(window.location.search).get("registered") === "true") {
    showAlert(alert, "Registration completed. You can now sign in.", "success");
}
