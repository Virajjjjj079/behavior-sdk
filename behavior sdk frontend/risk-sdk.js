// (function (window) {
//   const RiskSDK = {};
//   let config = {};
//   let behavior = {
//     keyData: [],
//     mouseData: [],
//     startTime: null,
//     lastKeyTime: null,
//     lastMove: null,
//     stopTime: null
//   };

//   const MAX_BEHAVIOR_DURATION = 30 * 1000; // 30 seconds

//   /* =========================
//      SDK INITIALIZATION
//   ========================= */
//   RiskSDK.init = function (options) {
//     config = {
//       apiKey: options.apiKey,
//       userId: options.userId,
//       endpoint: options.endpoint || "http://localhost:8080/",
//       consent: options.consent ?? true
//     };

//     if (!config.userId || !config.apiKey) {
//       throw new Error("RiskSDK: userId and apiKey are required");
//     }
//   };

//   /* =========================
//      BEHAVIOR COLLECTION (2 MIN)
//   ========================= */
//   RiskSDK.startBehavior = function () {
//     if (!config.consent) return;

//     behavior.startTime = Date.now();
//     behavior.stopTime = behavior.startTime + MAX_BEHAVIOR_DURATION;

//     document.addEventListener("keydown", onKeyDown);
//     document.addEventListener("keyup", onKeyUp);
//     document.addEventListener("mousemove", onMouseMove);

//     setTimeout(stopBehavior, MAX_BEHAVIOR_DURATION);
//   };

//   function stopBehavior() {
//     document.removeEventListener("keydown", onKeyDown);
//     document.removeEventListener("keyup", onKeyUp);
//     document.removeEventListener("mousemove", onMouseMove);
//   }

//   function onKeyDown(e) {
//     const now = Date.now();
//     if (behavior.lastKeyTime) {
//       behavior.keyData.push({
//         flightTime: now - behavior.lastKeyTime,
//         holdTime: null,
//         timestamp: now
//       });
//     }
//     behavior.lastKeyTime = now;
//   }

//   function onKeyUp() {
//     const last = behavior.keyData[behavior.keyData.length - 1];
//     if (last && last.holdTime === null) {
//       last.holdTime = Date.now() - behavior.lastKeyTime;
//     }
//   }

//   function onMouseMove(e) {
//     const now = Date.now();
//     if (behavior.lastMove) {
//       const dx = e.pageX - behavior.lastMove.x;
//       const dy = e.pageY - behavior.lastMove.y;
//       const dist = Math.sqrt(dx * dx + dy * dy);
//       const dt = now - behavior.lastMove.time;

//       behavior.mouseData.push({
//         dx,
//         dy,
//         dist,
//         dt
//       });
//     }
//     behavior.lastMove = { x: e.pageX, y: e.pageY, time: now };
//   }

//   /* =========================
//      BEHAVIOR METRICS
//   ========================= */
//   function summarizeBehavior() {
//     const totalTime =
//       (Math.min(Date.now(), behavior.stopTime) - behavior.startTime) / 1000;

//     const speeds = behavior.mouseData
//       .filter(m => m.dt > 0)
//       .map(m => m.dist / m.dt);

//     return {
//       typingSpeed: behavior.keyData.length / totalTime,
//       avgKeyHoldTime: avg(behavior.keyData.map(k => k.holdTime)),
//       avgFlightTime: avg(behavior.keyData.map(k => k.flightTime)),
//       totalTypingDuration: totalTime,

//       straightness: calculateStraightness(),
//       speedCv: coefficientOfVariation(speeds),
//       idleRatio:
//         behavior.mouseData.filter(m => m.dt > 200).length /
//         (behavior.mouseData.length || 1),
//       directionChangeRate: calculateDirectionChangeRate()
//     };
//   }

//   function calculateStraightness() {
//     if (behavior.mouseData.length < 2) return 0;

//     let straight = 0;
//     let total = 0;

//     for (let i = 1; i < behavior.mouseData.length; i++) {
//       const prev = behavior.mouseData[i - 1];
//       const curr = behavior.mouseData[i];

//       const dot =
//         prev.dx * curr.dx + prev.dy * curr.dy;
//       const mag =
//         Math.sqrt(prev.dx ** 2 + prev.dy ** 2) *
//         Math.sqrt(curr.dx ** 2 + curr.dy ** 2);

//       if (mag > 0) {
//         straight += dot / mag;
//         total++;
//       }
//     }
//     return total ? Math.abs(straight / total) : 0;
//   }

//   function calculateDirectionChangeRate() {
//     let changes = 0;

//     for (let i = 1; i < behavior.mouseData.length; i++) {
//       const prev = behavior.mouseData[i - 1];
//       const curr = behavior.mouseData[i];
//       if (prev.dx * curr.dx < 0 || prev.dy * curr.dy < 0) {
//         changes++;
//       }
//     }
//     return changes / (behavior.mouseData.length || 1);
//   }

//   function coefficientOfVariation(arr) {
//     const mean = avg(arr);
//     const variance =
//       avg(arr.map(v => Math.pow(v - mean, 2)));
//     return mean ? Math.sqrt(variance) / mean : 0;
//   }

//   function avg(arr) {
//     const valid = arr.filter(v => typeof v === "number");
//     return valid.reduce((a, b) => a + b, 0) / (valid.length || 1);
//   }

//   /* =========================
//      SEND TO BACKEND
//   ========================= */
//   RiskSDK.collectAndSend = async function () {
//     const payload = {
//       userId: config.userId,
//       fingerprint: await collectFingerprint(),
//       behavior: summarizeBehavior(),
//       location: await collectLocation(),
//       timestamp: new Date().toISOString()
//     };

//     const res = await fetch(config.endpoint, {
//       method: "POST",
//       headers: {
//         "Content-Type": "application/json",
//         "x-api-key": config.apiKey
//       },
//       body: JSON.stringify(payload)
//     });

//     return res.json();
//   };

//   window.RiskSDK = RiskSDK;
// })(window);



(function (window) {
  const RiskSDK = {};
  let config = {};
  let behavior = {
    keyData: [],
    mouseData: [],
    startTime: null,
    lastKeyTime: null,
    lastMove: null,
    stopTime: null
  };

  const MAX_BEHAVIOR_DURATION = 30 * 1000; // 30 seconds

  /* =========================
     SDK INITIALIZATION
  ========================= */
  RiskSDK.init = function (options) {
    config = {
      apiKey: options.apiKey,
      userId: options.userId,
      backendUrl: options.backendUrl || "http://localhost:8080",
      consent: options.consent ?? true
    };

    if (!config.userId || !config.apiKey) {
      throw new Error("RiskSDK: userId and apiKey are required");
    }
  };

  /* =========================
     BEHAVIOR COLLECTION
  ========================= */
  RiskSDK.startBehavior = function () {
    if (!config.consent) return;

    behavior.startTime = Date.now();
    behavior.stopTime = behavior.startTime + MAX_BEHAVIOR_DURATION;

    document.addEventListener("keydown", onKeyDown);
    document.addEventListener("keyup", onKeyUp);
    document.addEventListener("mousemove", onMouseMove);

    setTimeout(stopBehavior, MAX_BEHAVIOR_DURATION);
  };

  function stopBehavior() {
    document.removeEventListener("keydown", onKeyDown);
    document.removeEventListener("keyup", onKeyUp);
    document.removeEventListener("mousemove", onMouseMove);
  }

  function onKeyDown(e) {
    const now = Date.now();
    if (behavior.lastKeyTime) {
      behavior.keyData.push({
        flightTime: now - behavior.lastKeyTime,
        holdTime: null,
        timestamp: now
      });
    }
    behavior.lastKeyTime = now;
  }

  function onKeyUp() {
    const last = behavior.keyData[behavior.keyData.length - 1];
    if (last && last.holdTime === null) {
      last.holdTime = Date.now() - behavior.lastKeyTime;
    }
  }

  function onMouseMove(e) {
    const now = Date.now();
    if (behavior.lastMove) {
      const dx = e.pageX - behavior.lastMove.x;
      const dy = e.pageY - behavior.lastMove.y;
      const dist = Math.sqrt(dx * dx + dy * dy);
      const dt = now - behavior.lastMove.time;

      behavior.mouseData.push({ dx, dy, dist, dt });
    }
    behavior.lastMove = { x: e.pageX, y: e.pageY, time: now };
  }

  /* =========================
     BEHAVIOR METRICS
  ========================= */
  function summarizeBehavior() {
    const totalTime =
      (Math.min(Date.now(), behavior.stopTime) - behavior.startTime) / 1000;

    const speeds = behavior.mouseData
      .filter(m => m.dt > 0)
      .map(m => m.dist / m.dt);

    return {
      typingSpeed: behavior.keyData.length / totalTime,
      avgKeyHoldTime: avg(behavior.keyData.map(k => k.holdTime)),
      avgFlightTime: avg(behavior.keyData.map(k => k.flightTime)),
      totalTypingDuration: totalTime,

      straightness: calculateStraightness(),
      speedCv: coefficientOfVariation(speeds),
      idleRatio:
        behavior.mouseData.filter(m => m.dt > 200).length /
        (behavior.mouseData.length || 1),
      directionChangeRate: calculateDirectionChangeRate()
    };
  }

  function calculateStraightness() {
    if (behavior.mouseData.length < 2) return 0;
    let straight = 0, total = 0;
    for (let i = 1; i < behavior.mouseData.length; i++) {
      const prev = behavior.mouseData[i - 1];
      const curr = behavior.mouseData[i];
      const dot = prev.dx * curr.dx + prev.dy * curr.dy;
      const mag = Math.sqrt(prev.dx ** 2 + prev.dy ** 2) * Math.sqrt(curr.dx ** 2 + curr.dy ** 2);
      if (mag > 0) { straight += dot / mag; total++; }
    }
    return total ? Math.abs(straight / total) : 0;
  }

  function calculateDirectionChangeRate() {
    let changes = 0;
    for (let i = 1; i < behavior.mouseData.length; i++) {
      const prev = behavior.mouseData[i - 1];
      const curr = behavior.mouseData[i];
      if (prev.dx * curr.dx < 0 || prev.dy * curr.dy < 0) changes++;
    }
    return changes / (behavior.mouseData.length || 1);
  }

  function coefficientOfVariation(arr) {
    const mean = avg(arr);
    const variance = avg(arr.map(v => Math.pow(v - mean, 2)));
    return mean ? Math.sqrt(variance) / mean : 0;
  }

  function avg(arr) {
    const valid = arr.filter(v => typeof v === "number");
    return valid.reduce((a, b) => a + b, 0) / (valid.length || 1);
  }

  /* =========================
     HELPER: Collect fingerprint
  ========================= */
  async function collectFingerprint() {
    return {
      userAgent: navigator.userAgent,
      platform: navigator.platform,
      language: navigator.language,
      screenResolution: `${window.screen.width}x${window.screen.height}`,
      timezone: Intl.DateTimeFormat().resolvedOptions().timeZone,
      timezoneOffset: new Date().getTimezoneOffset(),
      colorDepth: window.screen.colorDepth,
      plugins: Array.from(navigator.plugins).map(p => p.name).join(", "),
      fonts: "Arial, Verdana, Courier New, Times New Roman, Comic Sans MS, Georgia" // Example static
    };
  }

  /* =========================
     HELPER: Collect location
  ========================= */
  async function collectLocation() {
    return new Promise(resolve => {
      if (!navigator.geolocation) return resolve({ latitude: null, longitude: null });
      navigator.geolocation.getCurrentPosition(pos => {
        resolve({ latitude: pos.coords.latitude, longitude: pos.coords.longitude });
      }, () => resolve({ latitude: null, longitude: null }));
    });
  }

  /* =========================
     SEND TO BACKEND
  ========================= */
  RiskSDK.collectAndSend = async function () {
    const fingerprint = await collectFingerprint();
    const behaviorData = summarizeBehavior();
    const locationData = await collectLocation();

    // 1️⃣ Send fingerprint to backend
    await fetch(`${config.backendUrl}/device-fingerprint/save`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "x-api-key": config.apiKey
      },
      body: JSON.stringify({
        userID: config.userId,
        userName: "TestUser",
        deviceData: JSON.stringify(fingerprint)
      })
    });

    // 2️⃣ Send behavior to backend
    await fetch(`${config.backendUrl}/api/mouse-keyboard/base`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "x-api-key": config.apiKey
      },
      body: JSON.stringify({
        userID: config.userId,
        userName: "TestUser",
        ...behaviorData,
        timestamp: new Date().toISOString()
      })
    });

    // 3️⃣ Send location to backend
    await fetch(`${config.backendUrl}/api/location/save-location`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "x-api-key": config.apiKey
      },
      body: JSON.stringify({
        userID: config.userId,
        userName: "TestUser",
        latitude: locationData.latitude,
        longitude: locationData.longitude,
        time: new Date().toISOString()
      })
    });

    // 4️⃣ Optionally: call comparison endpoints
    const compareDevice = await fetch(`${config.backendUrl}/device-fingerprint/compare/${config.userId}`);
    const deviceResult = await compareDevice.json();

    const compareBehavior = await fetch(`${config.backendUrl}/api/mouse-keyboard/compare/${config.userId}`);
    const behaviorResult = await compareBehavior.json();

    const compareLocation = await fetch(`${config.backendUrl}/api/location/compare/${config.userId}`);
    const locationResult = await compareLocation.json();

    return { deviceResult, behaviorResult, locationResult };
  };

  window.RiskSDK = RiskSDK;
})(window);
