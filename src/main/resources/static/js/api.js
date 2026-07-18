const ACCESS_TOKEN_KEY = "stockAnalyzer.accessToken";
const TOKEN_TYPE_KEY = "stockAnalyzer.tokenType";
const TOKEN_EXPIRATION_KEY = "stockAnalyzer.tokenExpiration";

export class ApiError extends Error {
    constructor(message, status) {
        super(message);
        this.name = "ApiError";
        this.status = status;
    }
}

export function saveAuthentication(authenticationResponse) {
    const tokenType = authenticationResponse.tokenType || "Bearer";
    const expiresIn = Number(authenticationResponse.expiresIn || 0);

    // OAuth/JWT APIs normally expose expiresIn in seconds. Values that are
    // already large enough to represent milliseconds remain unchanged.
    const expiresInMilliseconds =
        expiresIn > 0 && expiresIn < 31_536_000_000
            ? expiresIn * 1000
            : expiresIn;

    const expiration =
        expiresInMilliseconds > 0
            ? Date.now() + expiresInMilliseconds
            : null;

    localStorage.setItem(ACCESS_TOKEN_KEY, authenticationResponse.accessToken);
    localStorage.setItem(TOKEN_TYPE_KEY, tokenType);

    if (expiration !== null) {
        localStorage.setItem(TOKEN_EXPIRATION_KEY, String(expiration));
    } else {
        localStorage.removeItem(TOKEN_EXPIRATION_KEY);
    }
}

export function clearAuthentication() {
    localStorage.removeItem(ACCESS_TOKEN_KEY);
    localStorage.removeItem(TOKEN_TYPE_KEY);
    localStorage.removeItem(TOKEN_EXPIRATION_KEY);
}

export function getAccessToken() {
    const token = localStorage.getItem(ACCESS_TOKEN_KEY);
    const expiration = Number(localStorage.getItem(TOKEN_EXPIRATION_KEY));

    if (token && expiration && Date.now() >= expiration) {
        clearAuthentication();
        return null;
    }

    return token;
}

export function isAuthenticated() {
    return Boolean(getAccessToken());
}

export async function apiRequest(path, options = {}) {
    const headers = new Headers(options.headers || {});
    const token = getAccessToken();

    if (token) {
        const tokenType = localStorage.getItem(TOKEN_TYPE_KEY) || "Bearer";
        headers.set("Authorization", `${tokenType} ${token}`);
    }

    const hasBody = options.body !== undefined && options.body !== null;
    const isFormData = typeof FormData !== "undefined" && options.body instanceof FormData;

    if (hasBody && !isFormData && !headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    let response;

    try {
        response = await fetch(path, {
            ...options,
            headers
        });
    } catch {
        throw new ApiError("Unable to connect to the server.", 0);
    }

    if (response.status === 401) {
        clearAuthentication();

        if (!window.location.pathname.endsWith("/login.html")) {
            const redirect = encodeURIComponent(window.location.pathname + window.location.search);
            window.location.href = `/login.html?redirect=${redirect}`;
        }

        throw new ApiError("Authentication is required.", 401);
    }

    if (!response.ok) {
        const contentType = response.headers.get("Content-Type") || "";
        let message = `Request failed with status ${response.status}.`;

        try {
            if (contentType.includes("application/json")) {
                const errorBody = await response.json();
                message = errorBody.message || errorBody.error || JSON.stringify(errorBody);
            } else {
                const text = await response.text();
                if (text.trim()) {
                    message = text;
                }
            }
        } catch {
            // Keep the fallback message.
        }

        throw new ApiError(message, response.status);
    }

    if (response.status === 204) {
        return null;
    }

    const contentType = response.headers.get("Content-Type") || "";

    if (contentType.includes("application/json")) {
        return response.json();
    }

    const text = await response.text();
    return text || null;
}

export function get(path) {
    return apiRequest(path);
}

export function post(path, body) {
    return apiRequest(path, {
        method: "POST",
        body: body === undefined ? undefined : JSON.stringify(body)
    });
}

export function remove(path) {
    return apiRequest(path, {
        method: "DELETE"
    });
}
