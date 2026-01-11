(async function collectClientSignals() {
    const collectedData = {};

    /** 1. Session & Device Consistency */
    collectedData.userAgent = navigator.userAgent;
    collectedData.language = navigator.language;
    collectedData.platform = navigator.platform;
    collectedData.screen = {
        width: screen.width,
        height: screen.height,
        availWidth: screen.availWidth,
        availHeight: screen.availHeight,
        colorDepth: screen.colorDepth,
        pixelDepth: screen.pixelDepth
    };
    collectedData.timezone = Intl.DateTimeFormat().resolvedOptions().timeZone;

    /** 2. Behavioral Biometrics */
    const behavior = { keystrokes: [], mouseMoves: [], touchMoves: [] };

    document.addEventListener('keydown', e => {
        behavior.keystrokes.push({ key: e.key, time: Date.now() });
    });

    document.addEventListener('mousemove', e => {
        behavior.mouseMoves.push({ x: e.clientX, y: e.clientY, time: Date.now() });
    });

    document.addEventListener('touchmove', e => {
        const touch = e.touches[0];
        behavior.touchMoves.push({ x: touch.clientX, y: touch.clientY, time: Date.now() });
    });

    collectedData.behavior = behavior;

    /** 3. Geolocation */
    collectedData.geolocation = await new Promise((resolve) => {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(
                pos => resolve({
                    latitude: pos.coords.latitude,
                    longitude: pos.coords.longitude,
                    accuracy: pos.coords.accuracy
                }),
                err => resolve({ error: err.message })
            );
        } else {
            resolve({ error: "Geolocation not supported" });
        }
    });

    /** 4. IP Address & Reputation (needs external API) */
    try {
        const ipRes = await fetch('https://ipapi.co/json/');
        collectedData.ipInfo = await ipRes.json();
    } catch (e) {
        collectedData.ipInfo = { error: "Unable to fetch IP info" };
    }

    /** 5. Device & Environment Fingerprinting */
    collectedData.hardware = {
        deviceMemory: navigator.deviceMemory || "Unknown",
        hardwareConcurrency: navigator.hardwareConcurrency || "Unknown",
        maxTouchPoints: navigator.maxTouchPoints || 0
    };
    collectedData.plugins = Array.from(navigator.plugins).map(p => p.name);
    collectedData.cookiesEnabled = navigator.cookieEnabled;
    collectedData.localStorage = !!window.localStorage;
    collectedData.sessionStorage = !!window.sessionStorage;

    /** Print Everything */
    console.log("Collected Client Signals:", collectedData);
})();
