import {
    clearAuthentication,
    get,
    isAuthenticated
} from "./api.js";

let currentUserPromise = null;

export function requireAuthentication() {
    if (!isAuthenticated()) {
        const redirect = encodeURIComponent(window.location.pathname + window.location.search);
        window.location.replace(`/login.html?redirect=${redirect}`);
        return false;
    }

    return true;
}

export function redirectToDefaultPage() {
    window.location.replace(isAuthenticated() ? "/stocks.html" : "/login.html");
}

export async function getCurrentUser({ force = false } = {}) {
    if (force || !currentUserPromise) {
        currentUserPromise = get("/api/auth/me").catch(error => {
            currentUserPromise = null;
            throw error;
        });
    }

    return currentUserPromise;
}

export function isAdmin(user) {
    if (!user?.role) {
        return false;
    }

    return user.role === "ADMIN" || user.role === "ROLE_ADMIN";
}

export function logout() {
    clearAuthentication();
    window.location.replace("/login.html");
}
