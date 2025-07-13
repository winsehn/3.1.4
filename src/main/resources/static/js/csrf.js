let csrfToken = null;
export async function initCsrf() {
    const res = await fetch("/api/csrf", { credentials: "same-origin" });
    const data = await res.json();
    csrfToken = data.token;
}
export function getCsrfHeader() {
    if (!csrfToken) {
        throw new Error("CSRF token not initialized. Call initCsrf() first.");
    }

    return {
        "X-XSRF-TOKEN": csrfToken,
        "Content-Type": "application/json",
        "Accept": "application/json"
    };
}